package com.bjorkebeast;

import com.bjorkebeast.Grower.Grower;
import com.bjorkebeast.Grower.GrowerContainer;
import com.bjorkebeast.Grower.GrowerTile;
import com.bjorkebeast.helpers.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod( Reference.MODID )
public class TreeGrower {

    public static final Logger LOGGER = LogManager.getLogger( Reference.MODID );

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy() );

    public TreeGrower() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Setup the blocks
        Register.setupBlock( Grower.class );
    }

    private void setup(final FMLCommonSetupEvent event) {

        proxy.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {

            // System.out.println("onBlocksRegistry!");
            Register.registerBlock( event );
        }

        @SubscribeEvent
        public static void onItemRegistry( final RegistryEvent.Register<Item> event ){

            // System.out.println("onItemRegistry!");
            Register.registerItems( event );
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event ){

            event.getRegistry().register( TileEntityType.Builder.create( GrowerTile::new, Grower.instance ).build(null).setRegistryName( Grower.registryName) );
        }

        @SubscribeEvent
        public static void onContainerRegistry( final RegistryEvent.Register<ContainerType<?>> event ){

            event.getRegistry().register( IForgeContainerType.create(((windowId, playerInventory, data) -> {
                BlockPos blockPos = data.readBlockPos();
                return new GrowerContainer( windowId, proxy.getClientWorld(), blockPos, playerInventory, proxy.getClientPlayer()  );
            })).setRegistryName(Grower.registryName));

        }
    }
}
