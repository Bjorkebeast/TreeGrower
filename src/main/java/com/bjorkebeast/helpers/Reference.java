package com.bjorkebeast.helpers;

import com.bjorkebeast.Grower.Grower;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Reference {
    public static final String MODID = "treegrower";
    public static final String NAME  = "Tree Grower";
    public static final String VERSION = "1.1";
    public static final String ACCEPTED_VERSIONS = "[1.14]";

    public static ItemGroup defaultItemGroup = new ItemGroup(MODID) {

        @Override
        public ItemStack createIcon() { // icon for creative tab
            return new ItemStack(Grower.instance );
        }
    };
}
