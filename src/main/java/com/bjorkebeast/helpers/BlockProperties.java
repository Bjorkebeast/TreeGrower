package com.bjorkebeast.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockProperties {

    public Material material;
    public SoundType soundType;

    public float hardness   = 1.0f;
    public float resistance = 1.0f;

    public int lightValue = 0;

    public int harvestLevel = 0;
    public ToolType harvestTool;

    public boolean blockMovement    = true;
    public boolean noDrops          = false;
    public boolean tickRandomly     = false;

    public float slipperiness = 0.6F;

    public Block lootFrom;

    public Block.Properties export(){

        Block.Properties result = Block.Properties.create( material );

        result.sound( soundType );
        result.hardnessAndResistance( hardness, resistance );

        result.lightValue( lightValue );
        result.harvestLevel( harvestLevel );
        result.harvestTool( harvestTool );

        result.slipperiness( slipperiness );
        if ( lootFrom != null ) {
            result.lootFrom(lootFrom);
        }

        if ( !blockMovement ) {
            result.doesNotBlockMovement();
        }

        if ( noDrops ) {
            result.noDrops();
        }

        if ( tickRandomly ){
            result.tickRandomly();
        }

        return result;
    }
}
