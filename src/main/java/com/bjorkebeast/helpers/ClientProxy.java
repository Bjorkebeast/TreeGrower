package com.bjorkebeast.helpers;

import com.bjorkebeast.Grower.GrowerContainer;
import com.bjorkebeast.Grower.GrowerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

    @Override
    public Boolean onServer() { return false; }

    @Override
    public void init() {

        ScreenManager.registerFactory(GrowerContainer.Type, GrowerScreen::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
