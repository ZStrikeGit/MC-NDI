package dev.imabad.ndi.neoforge;

import dev.imabad.ndi.NDIMod;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(NDIMod.MOD_ID)
public class NDIModNeoForge {

    public NDIModNeoForge(IEventBus modEventBus) {
        NDIMod.init();
        modEventBus.addListener(this::onRegisterKeys);
        NeoForge.EVENT_BUS.register(this);
    }

    public void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(NDIMod.getNewCameraKey());
        event.register(NDIMod.getRemoveCameraMap());
    }

    @SubscribeEvent
    public void onClientTickEvent(ClientTickEvent.Post event) {
        NDIMod.handleKeybind(Minecraft.getInstance());
    }
}