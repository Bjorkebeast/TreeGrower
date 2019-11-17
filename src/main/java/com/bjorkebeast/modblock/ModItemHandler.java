package com.bjorkebeast.modblock;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ModItemHandler extends ItemStackHandler implements ModIItemHandler {

    public TileEntity tileEntity;

    public ModItemHandler( TileEntity tileEntity, int size){

        super( size );
        this.tileEntity = tileEntity;
    }

    public ModItemHandler (TileEntity tileEntity ){

        this( tileEntity,1 );
    }

    @Override
    protected void onContentsChanged(int slot) { tileEntity.markDirty(); }

    @Nonnull
    public ItemStack forceInsertItem( int slot, @Nonnull ItemStack stack, boolean simulate ){



        return super.insertItem( slot, stack, simulate );
    }


    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if ( !isItemValid( slot, stack ) ){
            return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }
}
