package com.bjorkebeast.modblock;

import com.bjorkebeast.helpers.BlockProperties;
import com.bjorkebeast.helpers.ItemProperties;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import static java.util.Objects.requireNonNull;

public abstract class ModBlock extends Block {

    public static ModBlock instance = null;
    public static final String registryName = "";
    public static Item getBlockItem(){ return null; }

    public static ItemProperties getItemProperties(){

        return new ItemProperties();
    }

    public static BlockProperties getBlockProperties(){

        return new BlockProperties();
    }

    public ModBlock( BlockProperties properties ){

        super( properties.export() );

        setRegistryName( registryName() );
    }

    protected abstract String registryName();

}
