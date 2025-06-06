package noppes.npcs.client.renderer.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.BlockRotated;
import noppes.npcs.blocks.tiles.TilePedestal;
import noppes.npcs.client.model.blocks.ModelPedestal;
import noppes.npcs.client.model.blocks.legacy.ModelLegacyPedestal;
import noppes.npcs.config.ConfigClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class BlockPedestalRenderer extends BlockRendererInterface {

    private final ModelLegacyPedestal legacyModel = new ModelLegacyPedestal();
    private final static ResourceLocation resource = new ResourceLocation("customnpcs:textures/models/legacy/npcPedestal.png");

    private final ModelPedestal model = new ModelPedestal();
    private final static ResourceLocation wood = new ResourceLocation("customnpcs:textures/models/pedestal/wood.png");
    private final static ResourceLocation stone = new ResourceLocation("customnpcs:textures/models/pedestal/stone.png");
    private final static ResourceLocation iron = new ResourceLocation("customnpcs:textures/models/pedestal/iron.png");
    private final static ResourceLocation gold = new ResourceLocation("customnpcs:textures/models/pedestal/gold.png");
    private final static ResourceLocation diamond = new ResourceLocation("customnpcs:textures/models/pedestal/diamond.png");

    public BlockPedestalRenderer() {
        ((BlockRotated) CustomItems.pedestal).renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this);
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4,
                                   double var6, float var8) {
        TilePedestal tile = (TilePedestal) var1;
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) var2 + 0.5f, (float) var4 + 1.5f, (float) var6 + 0.5f);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glRotatef(90 * tile.rotation, 0, 1, 0);
        GL11.glColor3f(1, 1, 1);

        if (ConfigClient.LegacyPedestal) {
            setMaterialTexture(var1.getBlockMetadata());
            legacyModel.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
            GL11.glScalef(1f, 0.99f, 1f);
            TextureManager manager = Minecraft.getMinecraft().getTextureManager();
            manager.bindTexture(resource);
            legacyModel.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        } else {
            setPedestalTexture(var1.getBlockMetadata());
            GL11.glPushMatrix();
            GL11.glScalef(1f, 1.3f, 1f);
            GL11.glTranslatef(0f, -0.34f, 0f);
            model.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
            GL11.glPopMatrix();
            GL11.glScalef(1f, 0.99f, 1f);
        }
        if (!this.playerTooFar(tile))
            doRender(tile.getStackInSlot(0));
        GL11.glPopMatrix();
        GL11.glColor3f(1, 1, 1);
    }

    private void doRender(ItemStack item) {
        if (item == null || item.getItem() == null || item.getItem() instanceof ItemBlock)
            return;
        GL11.glPushMatrix();
        if (ConfigClient.LegacyPedestal)
            GL11.glTranslatef(0.06f, 0.30f, 0.02f);
        else
            GL11.glTranslatef(0.06f, 0.490f, 0.02f);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        if (item.getItem().shouldRotateAroundWhenRendering()) {
            GL11.glTranslatef(0.14f, 0, 0.5f);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        } else
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-200.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);

        if (item.getItem().requiresMultipleRenderPasses()) {
            for (int k = 0; k <= item.getItem().getRenderPasses(item.getItemDamage()); ++k) {
                int i = item.getItem().getColorFromItemStack(item, k);
                float f12 = (float) (i >> 16 & 255) / 255.0F;
                float f4 = (float) (i >> 8 & 255) / 255.0F;
                float f5 = (float) (i & 255) / 255.0F;
                GL11.glColor4f(f12, f4, f5, 1.0F);
                RenderManager.instance.itemRenderer.renderItem(Minecraft.getMinecraft().thePlayer, item, k);
            }
        } else {
            int k = item.getItem().getColorFromItemStack(item, 0);
            float f11 = (float) (k >> 16 & 255) / 255.0F;
            float f12 = (float) (k >> 8 & 255) / 255.0F;
            float f4 = (float) (k & 255) / 255.0F;
            GL11.glColor4f(f11, f12, f4, 1.0F);
            RenderManager.instance.itemRenderer.renderItem(Minecraft.getMinecraft().thePlayer, item, 0);
        }
        GL11.glPopMatrix();

    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
                                     RenderBlocks renderer) {
        GL11.glPushMatrix();

        if (ConfigClient.LegacyPedestal) {
            GL11.glTranslatef(0, 0.44f, 0);
            GL11.glScalef(0.76f, 0.66f, 0.76f);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glRotatef(180, 0, 1, 0);
            setMaterialTexture(metadata);
            GL11.glColor3f(1, 1, 1);
            legacyModel.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        } else {
            GL11.glTranslatef(0, 1.9f, 0);
            GL11.glScalef(1f, 1.4f, 1f);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glRotatef(180, 0, 1, 0);
            setPedestalTexture(metadata);
            GL11.glColor3f(1, 1, 1);
            model.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        }
        GL11.glPopMatrix();
    }

    public void setPedestalTexture(int meta) {
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        if (meta == 1)
            manager.bindTexture(stone);
        else if (meta == 2)
            manager.bindTexture(iron);
        else if (meta == 3)
            manager.bindTexture(gold);
        else if (meta == 4)
            manager.bindTexture(diamond);
        else
            manager.bindTexture(wood);
    }

    @Override
    public int getRenderId() {
        return CustomItems.pedestal.getRenderType();
    }

    public int specialRenderDistance() {
        return 40;
    }
}
