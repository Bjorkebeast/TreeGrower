package com.bjorkebeast.Grower;

import com.bjorkebeast.helpers.BlockProperties;
import com.bjorkebeast.helpers.Reference;
import com.bjorkebeast.modblock.ModBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

public class Grower extends ModBlock {

    public static final String registryName = "grower";
    public static ModBlock factory(){ return new Grower(); }

    @ObjectHolder( Reference.MODID + ":" + registryName )
    public static Grower instance;

    public static BlockProperties getBlockProperties(){

        BlockProperties result = new BlockProperties();

        result.material     = Material.WOOD;
        result.soundType    = SoundType.WOOD;

        result.harvestTool = ToolType.AXE;

        return result;
    }

    @Override
    protected String registryName() { return registryName; }

    public Grower(){

        super( getBlockProperties() );
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getFacingFromVector((float) (entity.posX - clickedBlock.getX()), (float) (entity.posY - clickedBlock.getY()), (float) (entity.posZ - clickedBlock.getZ()));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(BlockStateProperties.FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer( builder );
        //builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if ( worldIn.isRemote ){

            return true;
        }

        GrowerTile tile = (GrowerTile)worldIn.getTileEntity(pos);

        NetworkHooks.openGui( (ServerPlayerEntity)player, tile, pos );
        return true;
    }

//    @Override
//    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
//
//        if ( world == null ){
//            return 0;
//        }
//
//        GrowerTile tile = (GrowerTile)world.getTileEntity(pos);
//
//        if ( tile == null ) {
//            return 0;
//        }
//
//        return tile.lightLevel;
//    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {

        if ( (state.hasTileEntity()) && (state.getBlock() != newState.getBlock() ) ) {

            // Drop input
            worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(h -> {
                for (int i = 0; i < h.getSlots(); i++) {
                    spawnAsEntity(worldIn, pos, h.getStackInSlot(i));
                }
            });

            // Drop output
            worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(h -> {
                for (int i = 0; i < h.getSlots(); i++) {
                    spawnAsEntity(worldIn, pos, h.getStackInSlot(i));
                }
            });

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world ){
        return new GrowerTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }
}
