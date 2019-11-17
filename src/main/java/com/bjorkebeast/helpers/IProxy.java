package com.bjorkebeast.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    Boolean onServer();

    public void init();
    public World getClientWorld();

    public PlayerEntity getClientPlayer();
}
