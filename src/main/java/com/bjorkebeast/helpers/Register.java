package com.bjorkebeast.helpers;

import com.bjorkebeast.TreeGrower;
import com.bjorkebeast.modblock.ModBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Register {

    public static List<Class> BLOCK_CLASSES = new ArrayList<Class>();

    public static void setupBlock( Class<? extends  ModBlock> blockClass ) {

        BLOCK_CLASSES.add( blockClass );
    }

    private static void registerItem ( final RegistryEvent.Register<Item> event, Class<? extends ModBlock> block ){

        try {

            Field registryName = block.getDeclaredField("registryName");
            Field instance = block.getDeclaredField("instance");
            Method getItemProperties = block.getMethod("getItemProperties" );

            // Debug
            // System.out.println((String) registryName.get(block));

            event.getRegistry().register(new BlockItem((ModBlock) instance.get(block), ((ItemProperties) getItemProperties.invoke(block)).export()).setRegistryName((String) registryName.get(block)));

        } catch ( InvocationTargetException e ){

            TreeGrower.LOGGER.warn("InvocationTargetException: " + e.getMessage());

        } catch ( IllegalAccessException e ){

            TreeGrower.LOGGER.warn("IllegalAccessException: " + e.getMessage());
        } catch ( NoSuchFieldException e ){

            TreeGrower.LOGGER.warn("NoSuchFieldException: " + e.getMessage());
        } catch ( NoSuchMethodException e ){

            TreeGrower.LOGGER.warn("NoSuchMethodException: " + e.getMessage());
       }

    }

    private static void registerBlock ( final RegistryEvent.Register<Block> event, Class<? extends ModBlock> block ){

       try {

           Method factory = block.getDeclaredMethod("factory" );

           event.getRegistry().register(  (ModBlock)factory.invoke(block) );

       } catch ( InvocationTargetException e ){

           TreeGrower.LOGGER.warn("InvocationTargetException: " + e.getMessage());
       } catch ( IllegalAccessException e ){

           TreeGrower.LOGGER.warn("IllegalAccessException: " + e.getMessage());
       }  catch ( NoSuchMethodException e ){

           TreeGrower.LOGGER.warn("NoSuchMethodException: " + e.getMessage());
       }
    }

    public static void registerItems( final RegistryEvent.Register<Item> event ) {

        for ( Class blockClass : BLOCK_CLASSES ){
            registerItem( event, blockClass );
        }
    }

    public static void registerBlock ( final RegistryEvent.Register<Block> event ){

        for ( Class blockClass : BLOCK_CLASSES ){
            registerBlock( event, blockClass );
        }
    }
}
