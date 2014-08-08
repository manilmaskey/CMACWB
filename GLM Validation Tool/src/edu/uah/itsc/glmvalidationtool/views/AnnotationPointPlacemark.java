package edu.uah.itsc.glmvalidationtool.views;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.FrameFactory;
import gov.nasa.worldwind.render.GlobeAnnotation;

import java.awt.Color;
import java.nio.DoubleBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class AnnotationPointPlacemark extends GlobeAnnotation
{
    public AnnotationPointPlacemark(Position position, AnnotationAttributes defaults)
    {
        super("", position, defaults);
    }

    protected void applyScreenTransform(DrawContext dc, int x, int y, int width, int height, double scale)
    {
        double finalScale = scale * this.computeScale(dc);

        GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.
        gl.glTranslated(x, y, 0);
        gl.glScaled(finalScale, finalScale, 1);
    }

    // Override annotation drawing for a simple circle
    private DoubleBuffer shapeBuffer;

    protected void doDraw(DrawContext dc, int width, int height, double opacity, Position pickPosition)
    {
        // Draw colored circle around screen point - use annotation's text color
        if (dc.isPickingMode())
        {
            this.bindPickableObject(dc, pickPosition);
        }

//        this.applyColor(dc, this.getAttributes().getTextColor(), 0.6 * opacity, true);

 //       this.applyColor(dc, this.getAttributes().getTextColor(), opacity, true);
        this.applyColor(dc, (Color)this.getValue("DisplayColor"), opacity, true);

        // Draw 16x16 shape from its bottom left corner
        int size = 8;
        if (this.shapeBuffer == null)
            this.shapeBuffer = FrameFactory.createShapeBuffer(AVKey.SHAPE_ELLIPSE, size, size, 0, null);
        GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.
        gl.glTranslated(-size / 2, -size / 2, 0);
        FrameFactory.drawBuffer(dc, GL.GL_TRIANGLE_FAN, this.shapeBuffer);
    }
}
