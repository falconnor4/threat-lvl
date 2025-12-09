package com.github.threatlvl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ThreatLvl.MODID, version = ThreatLvl.VERSION, name = ThreatLvl.NAME)
public class ThreatLvl {
    public static final String MODID = "threatlvl";
    public static final String NAME = "Threat Level";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ThreatRenderer());
        System.out.println("[ThreatLvl] Mod initialized!");
    }
}
