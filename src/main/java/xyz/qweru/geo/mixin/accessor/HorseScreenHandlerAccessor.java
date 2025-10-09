package xyz.qweru.geo.mixin.accessor;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.swing.text.html.parser.Entity;

@Mixin(HorseScreenHandler.class)
public interface HorseScreenHandlerAccessor {

    @Accessor("entity")
    AbstractHorseEntity geo_getEntity();

}
