package xyz.qweru.geo.core.system.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.helper.tree.SystemContext
import xyz.qweru.geo.extend.findOrCreateDir
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import kotlin.concurrent.thread

// TODO: fix previous config not being saved properly, config scanning (rescan might not get called when listing suggestions)
class Configs : System("configs", type = Type.INTERNAL) {

    val configFile = Global.DIRECTORY.resolve("$name.json")
    val configDir = Global.DIRECTORY.findOrCreateDir("config")
    val knownConfigs = Object2ObjectOpenHashMap<String, Config>()
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    val emptyJson = JsonObject()

    var sources = ObjectArrayList<Config>(3)
    // last loaded module/friend sources
    var friendSource = Config.EMPTY
    var moduleSource = Config.EMPTY

    @Volatile
    private var configsChanged = true
    private val watchService = FileSystems.getDefault().newWatchService()

    init {
        configDir.toPath().register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )

        thread(start = true) {
            Global.logger.info("Watching $configDir for changes")
            var watchKey: WatchKey? = null
            do {
                watchKey = watchService.take()
                configsChanged = true
            } while (watchKey.reset())
        }

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            watchService.close()
            writeFile(configFile, throwOnFail = false) {
                val json = JsonObject()
                saveThis(json)
                it.write(gson.toJson(json))
            }
        })
    }

    override fun initThis() {
        loadThis((if (configFile.exists()) FileReader(configFile).use { JsonParser.parseReader(it)?.asJsonObject } else emptyJson) ?: emptyJson)
        for (system in Systems.getSubsystems()) {
            if (system.type != Type.ROOT) continue
            system.load(moduleSource.json.get(system.name)?.asJsonObject ?: emptyJson, SystemContext.of(moduleSource.type))
        }
    }

    override fun loadThis(json: JsonObject) {
        findConfig(json.get("modules")?.asString ?: "")?.let {
            if (it.isEmpty) return@let
            moduleSource = Config(it.name, ConfigType.MODULE, it.json)
        }
        findConfig(json.get("friends")?.asString ?: "")?.let {
            if (it.isEmpty) return@let
            friendSource = Config(it.name, ConfigType.FRIEND, it.json)
        }

        if (moduleSource.isEmpty || friendSource.isEmpty)
            Global.logger.warn("A previously used config is invalid! You can safely ignore this message if this is your first run.")
    }

    override fun saveThis(json: JsonObject) {
        // first run
        if (moduleSource.isEmpty && friendSource.isEmpty) {
            moduleSource = Config("config", ConfigType.ALL, JsonObject())
            friendSource = moduleSource
        }

        json.addProperty("modules", moduleSource.name)
        json.addProperty("friends", friendSource.name)

        writeConfig(moduleSource)
        writeConfig(friendSource)
        saveConfig(friendSource)
        saveConfig(moduleSource)
    }

    fun save(name: String, type: ConfigType) {
        val config = findConfig(name) ?: Config(name, type, JsonObject())
        if (config == Config.EMPTY) return
        writeConfig(config)
        saveConfig(config)
    }

    fun findConfig(name: String): Config? {
        if (name == "") return Config.EMPTY
        if (configsChanged) scanConfigs()
        return knownConfigs[name]
    }

    /**
     * Writes the contents of the config to a file
     */
    fun saveConfig(config: Config, throwOnError: Boolean = true) {
        if (config === Config.EMPTY) return
            val file = configDir.resolve("${config.name}.json")
        writeFile(file, throwOnError) {
            it.write(gson.toJson(config.json))
        }
    }

    fun loadConfig(config: Config) {
        for (system in Systems.getSubsystems()) {
            if (system.type != Type.ROOT) continue
            system.load(config.json.get(system.name)?.asJsonObject ?: emptyJson, SystemContext.of(config.type))
        }

        if (config.type.hasModules) moduleSource = config
        if (config.type.hasFriends) friendSource = config
    }

    /**
     * Writes the json of the config
     */
    fun writeConfig(config: Config) {
        if (config === Config.EMPTY) throw IllegalArgumentException("Config.EMPTY passed to writeConfig")
        config.json = JsonObject()
        config.type.addTo(config.json)
        for (system in Systems.getSubsystems()) {
            if (system.type != Type.ROOT) continue
            val json = JsonObject()
            system.save(json, SystemContext.of(config.type))
            config.json.add(system.name, json)
        }
    }

    private fun scanConfigs() {
        knownConfigs.clear()
        for (file in configDir.listFiles()) {
            try {
                val config = getConfig(file) ?: continue
                knownConfigs[file.nameWithoutExtension] = config
            } catch (_: IOException) {
                Global.logger.warn("Skipping invalid config entry: $file")
            }
        }
        knownConfigs.trim()
        configsChanged = false
    }

    private fun writeFile(file: File, throwOnFail: Boolean = true, block: (FileWriter) -> Unit) {
        try {
            FileWriter(file).use(block)
        } catch (t: IOException) {
            Global.logger.warn("Failed to write $file", t)
            if (!throwOnFail) return
            throw RuntimeException(t)
        }
    }

    private fun getConfig(file: File): Config? = FileReader(file).use { reader ->
        val json = JsonParser.parseReader(reader).asJsonObject
        val type = ConfigType.fromJson(json) ?: return@use null
        return@use Config(file.nameWithoutExtension, type, json)
    }
}