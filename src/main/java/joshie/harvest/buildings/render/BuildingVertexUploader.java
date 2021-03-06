package joshie.harvest.buildings.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.ByteBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BuildingVertexUploader extends WorldVertexBufferUploader {
    @Override
    public void draw(VertexBuffer vertexBufferIn) {
        if (vertexBufferIn.getVertexCount() > 0) {
            VertexFormat vertexformat = vertexBufferIn.getVertexFormat();
            int i = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = vertexBufferIn.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();

            for (int j = 0; j < list.size(); ++j) {
                bytebuffer.position(vertexformat.getOffset(j));
                list.get(j).getUsage().preDraw(vertexformat, j, i, bytebuffer);
            }

            GlStateManager.glDrawArrays(vertexBufferIn.getDrawMode(), 0, vertexBufferIn.getVertexCount());
            int i1 = 0;

            for (int j1 = list.size(); i1 < j1; ++i1) {
                list.get(i1).getUsage().postDraw(vertexformat, i1, i, bytebuffer);
            }
        }
    }
}
