package noppes.npcs.client.model.util;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import noppes.npcs.entity.data.ModelScalePart;
import org.lwjgl.opengl.GL11;

public class ModelScaleRenderer extends ModelRenderer {

    public boolean compiledModel;

    /**
     * The GL display list rendered by the Tessellator for this model
     */
    public int displayListModel;

    protected ModelScalePart config;

    public float x, y, z;

    public ModelScaleRenderer(ModelBase par1ModelBase) {
        super(par1ModelBase);
    }

    public ModelScaleRenderer(ModelBase par1ModelBase, int par2, int par3) {
        this(par1ModelBase);
        this.setTextureOffset(par2, par3);
    }

    public void setConfig(ModelScalePart config, float x, float y, float z) {
        this.config = config;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void render(float par1) {
        if (!showModel || isHidden)
            return;
        if (!compiledModel)
            compileDisplayListModel(par1);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        if (config != null)
            GL11.glTranslatef(0, 0, 0);
        this.postRender(par1);
        if (config != null)
            GL11.glScalef(config.scaleX, config.scaleY, config.scaleZ);
        GL11.glCallList(this.displayListModel);
        if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); ++i) {
                ((ModelRenderer) this.childModels.get(i)).render(par1);
            }
        }
        GL11.glPopMatrix();
    }

    public void compileDisplayListModel(float par1) {
        this.displayListModel = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayListModel, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); ++i) {
            ((ModelBox) this.cubeList.get(i)).render(tessellator, par1);
        }

        GL11.glEndList();
        this.compiledModel = true;
    }

}
