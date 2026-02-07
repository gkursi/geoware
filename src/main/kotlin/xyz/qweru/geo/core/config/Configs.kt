package xyz.qweru.geo.core.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.helper.tree.SystemContext
import xyz.qweru.geo.extend.kotlin.file.findOrCreateDir
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import kotlin.concurrent.thread

object Configs {

    @Volatile
    var syncId = 0L

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    val emptyJson = JsonObject()

    val configDirectory = Core.dir.findOrCreateDir("config")
    val configFile = Core.dir.resolve("configs.json")
    val configs = Object2ObjectOpenHashMap<String, Config>()

    // last loaded module/friend sources
    var friendSource = Config.EMPTY
    var moduleSource = Config.EMPTY

    @Volatile
    private var scanSyncId = -1L

    init {
        val watchService = FileSystems.getDefault().newWatchService()

        configDirectory.toPath().register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )

        thread(start = true, isDaemon = true) {
            var watchKey: WatchKey? = null
            Core.logger.info("Watching $configDirectory")

            do {
                watchKey = watchService.take()
                syncId++
            } while (watchKey.reset())
        }

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            watchService.close()
            FileWriter(configFile).use {
                val json = JsonObject()
                saveLastConfig(json)
                it.write(gson.toJson(json))
            }
        })
    }

    fun init() {
        loadPreviousConfigs(
            if (configFile.exists()) {
                FileReader(configFile).use { reader ->
                    JsonParser.parseReader(reader).asJsonObject
                }
            } else {
                emptyJson
            }
        )

        for (system in Systems.getSystems()) {
            system.load(
                moduleSource.json[system.name]
                    ?.asJsonObject
                    ?: continue,
                SystemContext.of(moduleSource.type)
            )
        }
    }

    fun loadPreviousConfigs(json: JsonObject) {
        findSource("modules", ConfigType.ALL_MODULE, json, ::moduleSource::set)
        findSource("friends", ConfigType.FRIEND, json, ::friendSource::set)

        if (moduleSource.isEmpty || friendSource.isEmpty) {
            Core.logger.warn("A previously used config is invalid! You can safely ignore this message if this is your first run.")
        }
    }

    fun saveLastConfig(json: JsonObject) {
        if (moduleSource.isEmpty && friendSource.isEmpty) {
            moduleSource = Config("config", ConfigType.ALL, JsonObject())
            friendSource = moduleSource
        }

        json.addProperty("friends", friendSource.name)
        saveConfigToFile(friendSource)

        json.addProperty("modules", moduleSource.name)
        saveConfigToFile(moduleSource)
    }

    fun updateAndSave(name: String, type: ConfigType) {
        val config = findConfig(name) ?: Config(name, type, JsonObject())
        if (config == Config.EMPTY) return
        saveConfigToFile(config)
    }

    fun findConfig(name: String): Config? {
        if (name == "") return Config.EMPTY
        if (scanSyncId != syncId) scanConfigs()
        return configs[name]
    }

    fun applyConfig(config: Config) {
        for (system in Systems.getSystems()) {
            system.load(config.json.get(system.name)?.asJsonObject ?: emptyJson, SystemContext.of(config.type))
        }

        if (config.type.containsModules) moduleSource = config
        if (config.type.containsFriends) friendSource = config
    }

    private fun findSource(name: String, type: ConfigType, json: JsonObject, consume: (Config) -> Unit) {
        findConfig(json[name]?.asString ?: "")?.let {
            if (it.isEmpty) return@let
            consume(Config(it.name, type, it.json))
        }
    }

    private fun scanConfigs() {
        Core.logger.info("Scanning configs ($scanSyncId, $syncId)")
        scanSyncId = syncId
        configs.clear()

        for (file in configDirectory.listFiles()) {
            try {
                val config = readFile(file) ?: continue
                configs[file.nameWithoutExtension] = config
            } catch (_: IOException) {
                Core.logger.warn("Skipping invalid config entry: $file")
            }
        }
    }

    private fun saveConfigToFile(config: Config) {
        if (config === Config.EMPTY) {
            throw IllegalArgumentException("Config.EMPTY passed to writeConfig")
        }

        config.json = JsonObject()
            .also(config.type::addToJson)

        for (system in Systems.getSystems()) {
            val json = JsonObject()
            system.save(json, SystemContext.of(config.type))
            config.json.add(system.name, json)
        }

        val file = configDirectory.resolve("${config.name}.json")

        FileWriter(file).use {
            it.write(gson.toJson(config.json))
        }
    }

    private fun readFile(file: File): Config? = FileReader(file).use { reader ->
        val json = JsonParser.parseReader(reader).asJsonObject
        val type = ConfigType.fromJson(json) ?: return@use null.also {
            Core.logger.warn("Could not read $file")
        }
        Core.logger.info("Read $file")
        return@use Config(file.nameWithoutExtension, type, json)
    }
}