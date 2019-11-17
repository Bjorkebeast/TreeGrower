package com.bjorkebeast.helpers;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class ItemProperties {

    public int maxStackSize = 64;
    public int maxDamage    = 0;
    // public ItemGroup group;
    public Rarity rarity = Rarity.COMMON;
    public Food food;
    public boolean canRepair = true;


    public Item.Properties export(){

        Item.Properties result = new Item.Properties();

        // Add to default group
        result.group( Reference.defaultItemGroup );

        // Choose damage or stacksize
        if ( maxDamage > 0 ) {
            result.maxDamage(maxDamage);
        }

        if ( maxStackSize > 0 ) {
            result.maxStackSize(maxStackSize);
        }

        result.rarity( rarity );
        if ( food != null ) {
            result.food(food);
        }

        if ( !canRepair ){
            result.setNoRepair();
        }

        return result;
    }
}
