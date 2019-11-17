package com.bjorkebeast.Grower;

import com.bjorkebeast.helpers.Reference;
import com.bjorkebeast.modblock.ModIItemHandler;
import com.bjorkebeast.modblock.ModItemHandler;
import com.bjorkebeast.modblock.ModTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrowerTile extends ModTileEntity implements ITickableTileEntity, INamedContainerProvider {

    LazyOptional<ModIItemHandler>inputHandler  = LazyOptional.of( this::createInputHandler );
    LazyOptional<ModIItemHandler>outputHandler = LazyOptional.of( this::createOutputHandler );

    public static final SaplingData[] SAPLING_LIST = {
            new SaplingData( Items.OAK_SAPLING,         6, Blocks.OAK_LOG,          1.2f,   40 ),
            new SaplingData( Items.ACACIA_SAPLING,      8, Blocks.ACACIA_LOG,       1f,     50 ),
            new SaplingData( Items.BIRCH_SAPLING,       8, Blocks.BIRCH_LOG,        1.2f,   45 ),
            new SaplingData( Items.JUNGLE_SAPLING,      10, Blocks.JUNGLE_LOG,      1f,     60 ),
            new SaplingData( Items.SPRUCE_SAPLING,      10, Blocks.SPRUCE_LOG,      1.2f,   55 ),
            new SaplingData( Items.DARK_OAK_SAPLING,    10, Blocks.DARK_OAK_LOG,    1f,     55 ),
    };

    public static int acceptedInputIndex( Item sapling ){

        int size = SAPLING_LIST.length;
        for( int i = 0; i < size; i++ ){

            if ( SAPLING_LIST[i].equals( sapling ) ){
                return i;
            }
        }

        return -1;
    }

    public static boolean acceptedInput( Item sapling ){

        return ( acceptedInputIndex( sapling ) > -1 );
    }

    @ObjectHolder(Reference.MODID + ":" + Grower.registryName )
    public static TileEntityType<GrowerTile> Tile;

    public static final int EC_NONE = 0;
    public static final int EC_CANT_SEE_SUN             = 3;
    public static final int EC_DIM_HAS_NO_SKY           = 4;
    public static final int EC_WEATHER_IS_BAD           = 5;
    public static final int EC_NO_INPUT_SAPLING         = 10;
    public static final int EC_NO_INPUT_BONEMEAL        = 11;
    public static final int EC_WRONG_WOOD_OUTPUT        = 12;
    public static final int EC_NO_OUTPUT_LOG_SPACE      = 13;
    public static final int EC_WRONG_SAPLING_OUTPUT     = 14;
    public static final int EC_NO_OUTPUT_SAPLING_SPACE  = 15;

    public static String getErrorText( int errorCode ){

        switch ( errorCode ){

            case EC_NONE:
                return "Working";
            case EC_CANT_SEE_SUN:
                return "Not enough sunlight";
            case EC_DIM_HAS_NO_SKY:
                return "Dimension has no sky";
            case EC_WEATHER_IS_BAD:
                return "Weather is bad";
            case EC_NO_INPUT_SAPLING:
                return "No input sapling";
            case EC_NO_INPUT_BONEMEAL:
                return "No bonemeal";
            case EC_WRONG_WOOD_OUTPUT:
                return "Wrong type of log output";
            case EC_NO_OUTPUT_LOG_SPACE:
                return "No space for log output";
            case EC_WRONG_SAPLING_OUTPUT:
                return "Wrong type of sapling output";
            case EC_NO_OUTPUT_SAPLING_SPACE:
                return "No space for sapling output";
            default:
                return "??UNKNOWN??";
        }
    }

    private int counter         = 0;
    private int maxCounter      = 0;
    private int saplingIndex;

    private int errorCode       = EC_NONE;

    public int getDurationStatus(){

        if ( maxCounter == 0 ){

            return 0;
        }

        return (int)( ( counter * 100.0f ) / maxCounter);
    }

    public void setDurationStatus( int value ){
        maxCounter = 100;
        counter = value;
    }

    public int getErrorCode(){

        return errorCode;
    }

    public void setErrorCode( int value ){

        errorCode = value;
    }

    public GrowerTile() {
        super( Tile );
    }

    private int getDaylightValue(){
        return world.getLightFor( LightType.SKY, pos.up() ) - world.getSkylightSubtracted();
    }

    private boolean canSeeSun(){

        if ( !world.dimension.hasSkyLight() ){

            errorCode = EC_DIM_HAS_NO_SKY;

            return false;
        }

        if ( getDaylightValue() < 13 ){

            errorCode = EC_CANT_SEE_SUN;

            return false;
        }

        if ( world.isRaining() || world.isThundering() ){

            errorCode = EC_WEATHER_IS_BAD;

            return false;
        }


        return true;
    }

    @Override
    public void tick() {

        if ( ( world == null ) || ( world.isRemote ) ) {
            return;
        }

//        if (  ( this.world == null ) || ( this.world.isRemote ) ) {
//            return;
//        }


        if ( counter > 0 ) {

            if ( !canSeeSun() ){
                // dont tick without the sun
                return;
            }

            errorCode = EC_NONE;

            counter--;
            markDirty();

            if ( counter > 0 ){

                return;
            }

            outputHandler.ifPresent( h -> {

                ItemStack logs      = SAPLING_LIST[ saplingIndex ].createLogStack();
                ItemStack saplings  = SAPLING_LIST[ saplingIndex ].createSaplingStack();

                if ( logs != ItemStack.EMPTY ) {
                    h.forceInsertItem(0, logs, false);
                }

                if ( saplings != ItemStack.EMPTY ) {
                    h.forceInsertItem(1, saplings, false);
                }

                counter = 0;
            });


            // energy.ifPresent( e -> ((EnergyStorageExt)e).addEnergy(1000) );
        } else {

            inputHandler.ifPresent( h -> {

                ItemStack sapling = h.getStackInSlot(0);
                ItemStack fuel = h.getStackInSlot(1);


                saplingIndex = acceptedInputIndex( sapling.getItem() );

                if ( saplingIndex < 0 ){ // not accepted

                    errorCode = EC_NO_INPUT_SAPLING;

                    return;
                }

                if ( fuel.getItem() != Items.BONE_MEAL ){

                    errorCode = EC_NO_INPUT_BONEMEAL;

                    return;
                }

                outputHandler.ifPresent( i ->{

                    ItemStack outputLog = i.getStackInSlot(0);
                    ItemStack outputSapling = i.getStackInSlot(1);

                    if ( !outputLog.isEmpty() ){

                        if ( !outputLog.getItem().equals( SAPLING_LIST[ saplingIndex ].woodLog.asItem() ) ){


                            errorCode = EC_WRONG_WOOD_OUTPUT;
                            return;
                        }

                        if ( outputLog.getCount() >= outputLog.getMaxStackSize() ){

                            errorCode = EC_NO_OUTPUT_LOG_SPACE;
                            return;
                        }

                    }

                    if ( !outputSapling.isEmpty() ){

                        if ( !outputSapling.getItem().equals( SAPLING_LIST[ saplingIndex ].sapling ) ){

                            errorCode = EC_WRONG_SAPLING_OUTPUT;
                            return;
                        }

                        if ( outputSapling.getCount() >= outputLog.getMaxStackSize() ){

                            errorCode = EC_NO_OUTPUT_SAPLING_SPACE;
                            return;
                        }
                    }

                    // Extract a sapling
                    h.extractItem( 0, 1, false );

                    // Extract some bonemeal
                    h.extractItem( 1, 1, false );

                    // Wait for the tree to grow
                    maxCounter  = SAPLING_LIST[ saplingIndex ].getDuration();
                    counter     = maxCounter;

                    errorCode   = EC_NONE;

                    markDirty();
                });

            });

//            handler.ifPresent( h -> {
//                ItemStack stack = h.getStackInSlot(0);
//                if ( stack.getItem() == Items.DIAMOND ){
//                    h.extractItem(0, 1, false);
//                    counter = 20;
//                }
//            });

        }
    }

    @Override
    public void read(CompoundNBT tag) {

        counter = tag.getInt("counter");
        maxCounter = tag.getInt("maxcounter");
        saplingIndex = tag.getInt("saplingIndex");

        inputHandler.ifPresent( h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(tag.getCompound("invInput")) );
        outputHandler.ifPresent( h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(tag.getCompound("invOutput")) );

        super.read( tag );
    }

    @Override
    public CompoundNBT write(CompoundNBT tag ) {

        inputHandler.ifPresent( h -> {
            tag.put("invInput", ((INBTSerializable<CompoundNBT>)h).serializeNBT() );
        });

        outputHandler.ifPresent( h -> {
            tag.put("invOutput", ((INBTSerializable<CompoundNBT>)h).serializeNBT() );
        });

        tag.putInt("counter", counter);
        tag.putInt("maxcounter", maxCounter);
        tag.putInt("saplingIndex", saplingIndex );

        return super.write(tag );
    }

//    public boolean acceptedInput( Item item ){
//
//        return ItemTags.getCollection().getOrCreate(acceptedInputs).contains( item );
//    }
//
    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {

        return new GrowerContainer( windowId, world, pos, playerInventory, playerEntity );
    }

    private ModIItemHandler createInputHandler(){

        return new ModItemHandler( this, 2 ){

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

                switch ( slot ) {
                    case 0:

                        if ( !acceptedInput( stack.getItem() ) ) {

                            return false;
                        }

                        break;
                    case 1:
                        if (stack.getItem() != Items.BONE_MEAL) {
                            return false;
                        }
                        break;
                }

                return super.isItemValid(slot, stack);
            }
        };
    }

    private ModIItemHandler createOutputHandler(){

        return new ModItemHandler(this, 2){

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }

        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        if ( cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){

            if ( side == Direction.DOWN ){

                return this.outputHandler.cast();
            }

            return this.inputHandler.cast();
        }

        return super.getCapability(cap, side);
    }
}
