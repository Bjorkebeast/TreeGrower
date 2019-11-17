package com.bjorkebeast.modblock;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public interface ModIItemHandler extends IItemHandler {

    public ItemStack forceInsertItem(int slot, @Nonnull ItemStack stack, boolean simulate );
}
