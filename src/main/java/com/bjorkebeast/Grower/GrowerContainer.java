package com.bjorkebeast.Grower;

import com.bjorkebeast.helpers.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ObjectHolder;

public class GrowerContainer extends Container {

    @ObjectHolder(Reference.MODID + ":" + Grower.registryName )
    public static ContainerType<GrowerContainer> Type;

    private GrowerTile tileEntity;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;

    private int slotCount;

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {

        // itemstack11

        if ( ( slotId >= 0 ) && ( clickTypeIn == ClickType.PICKUP ) ){

            Slot slot = this.inventorySlots.get(slotId);

            ItemStack handStack = player.inventory.getItemStack();
            ItemStack slotStack = slot.getStack();

            if ( ( !handStack.isEmpty() ) && ( !slotStack.isEmpty() ) && ( handStack.getItem().equals( slotStack.getItem() ) ) && ( ( handStack.getCount() + slotStack.getCount() ) > handStack.getMaxStackSize() ) ){

                return ItemStack.EMPTY;
            }
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    public GrowerContainer(int worldId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        super( Type, worldId );

        tileEntity = (GrowerTile)world.getTileEntity(pos);

        this.playerEntity = playerEntity;
        this.playerInventory = new InvWrapper( playerInventory );

        // Get input handler
        tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(h ->{

            addSlot( new SlotItemHandler(h, 0, 46, 8));
            addSlot( new SlotItemHandler(h, 1, 46, 32));
        });

        // Get output handler
        tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(h ->{
            addSlot( new SlotItemHandler(h, 0, 118, 8));
            addSlot( new SlotItemHandler(h, 1, 118, 32));
        });

        this.slotCount = this.inventorySlots.size();

        layoutPlayerInventorySlots(10, 70);

        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {

                return getDurationStatus();
            }

            @Override
            public void set(int value) {
                setDurationStatus( value );
                // tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> ((EnergyStorageExt)h).setEnergy(value) );
            }
        });

        trackInt(new IntReferenceHolder() {

            private boolean onOpen = true;

            @Override
            public boolean isDirty() {

                if ( this.onOpen ){

                    this.onOpen = false;
                    return true;
                }

                return super.isDirty();
            }

            @Override
            public int get() {
                return getErrorCode();
            }

            @Override
            public void set(int value) {

                setErrorCode( value );
            }
        });

    }

    public int getDurationStatus(){

        if ( tileEntity == null ){

            return -1;
        }

        return tileEntity.getDurationStatus();
    }

    public void setDurationStatus(int value){

        if ( tileEntity == null ){

            return;
        }

        tileEntity.setDurationStatus( value );
    }

    public int getErrorCode(){

        if ( tileEntity == null ){

            return -1;
        }

        return tileEntity.getErrorCode();
    }

    public void setErrorCode(int value){

        if ( tileEntity == null ){

            return;
        }

        tileEntity.setErrorCode( value );
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {

        ItemStack itemstack = ItemStack.EMPTY;

        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            if (index < slotCount) {

                if (!this.mergeItemStack(stack, slotCount, 36 + slotCount, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else {

                if( tileEntity.acceptedInput( stack.getItem() ) ){

                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }

                } else if ( stack.getItem() == Items.BONE_MEAL ){

                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }

                } else if (index < 27 + slotCount) {
                    if (!this.mergeItemStack(stack, 27 + slotCount, 36 + slotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 36 + slotCount && !this.mergeItemStack(stack, slotCount, 27 + slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx ){

        for( int i = 0; i < amount; i++ ){
            addSlot( new SlotItemHandler(handler, index + i, x + (dx * i), y) );
        }

        return amount + index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy ){

        for( int i = 0; i < verAmount; i++ ){
            index = addSlotRange( handler, index, x, y + (i*dy), horAmount, dx );
        }

        return index;
    }

    private void layoutPlayerInventorySlots( int leftCol, int topRow ){

        addSlotBox( playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        // Hotbar
        addSlotRange( playerInventory, 0, leftCol, topRow, 9, 18 );
    }


    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {

        return isWithinUsableDistance( IWorldPosCallable.of( tileEntity.getWorld(), tileEntity.getPos() ), playerEntity, Grower.instance );
    }
}
