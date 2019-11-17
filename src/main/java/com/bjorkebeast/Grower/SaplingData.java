package com.bjorkebeast.Grower;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SaplingData {

    public Item sapling;
    public String registryName;
    public int treeSize;
    public Block woodLog;
    public float saplingReturn;
    public int duration;

    public SaplingData(Item sapling, int treeSize, Block woodLog, float saplingReturn, float duration ) {

        this.sapling        = sapling;
        this.registryName   = this.sapling.getRegistryName().toString();
        this.treeSize       = treeSize;
        this.woodLog        = woodLog;
        this.saplingReturn  = saplingReturn;
        this.duration       = (int)(duration * 20);
    }

    public boolean equals( Item item ){

        return item.equals( this.sapling );
    }

    private int getRandom( float i, float low, float high ){

        low = i * low;
        high = i * high;

        return (int)(( Math.random() * ( (high - low) + 1 ) ) + low);
    }

    public ItemStack createLogStack(){

        int stacksize = getRandom( treeSize, 0.6f, 1.4f );
        if ( stacksize == 0 ){
            return ItemStack.EMPTY;
        }

        return new ItemStack( woodLog.asItem(), stacksize );
    }

    public ItemStack createSaplingStack(){

        int stacksize = getRandom( saplingReturn, 1f, 2.01f );
        if ( stacksize == 0 ){
            return ItemStack.EMPTY;
        }

        return new ItemStack( sapling.asItem(), stacksize ) ;
    }

    public int getDuration(){

        return getRandom( duration, 0.8f, 1.3f );
    }



}
