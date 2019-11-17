package com.bjorkebeast.Grower;

import com.bjorkebeast.helpers.Reference;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GrowerScreen extends ContainerScreen<GrowerContainer> {

    private ResourceLocation GUI = new ResourceLocation( Reference.MODID, "textures/gui/grower_gui.png");

    public GrowerScreen(GrowerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F );
        this.minecraft.getTextureManager().bindTexture(GUI);

        int centerX = ( this.width - this.xSize ) / 2;
        int centerY = ( this.height - this.ySize ) / 2;

        this.blit( centerX, centerY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private boolean pointInRect( int pX, int pY, int rX, int rY, int rW, int rH ){

        // System.out.println( String.format( "X: %d, Y: %d", pX, pY ) );

        if ( pX < rX ){

            return false;
        }

        if ( pX > rX + rW ){
            return false;
        }

        if ( pY < rY ){
            return false;
        }

        if ( pY > rY + rH ){
            return false;
        }

        return true;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // this.font.drawString( this.title.getFormattedText(), 8.0F, 6.0F, 4210752 );

        if ( pointInRect( mouseX - guiLeft, mouseY - guiTop, 2, 2, 14,13) ){

            int ec = container.getErrorCode();
            String ecText = GrowerTile.getErrorText( ec );

            this.renderTooltip( ecText, 10,30);

            // this.font.drawString( ecText, 5, 5, 0x800080 );
        }



        this.font.drawString( "Grow status: " + (100 - container.getDurationStatus()) + "%", 50, 55, 0x404040 );



//        if ( ec == GrowerTile.EC_NONE ){
//            return;
//        }


        //this.font.drawString( ecText, 5, 5, 0x800080 );

        // drawString(Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy(), 10, 10, 0x404040 );
        // this.font.drawString( this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 30 + 2), 0x404040);
    }
}
