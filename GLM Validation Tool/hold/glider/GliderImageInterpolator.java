package edu.uah.itsc.worldwind.eclipse.glider;

import gov.nasa.worldwind.geom.Sector;

import java.awt.Dimension;
import java.util.ArrayList;

import gov.nasa.worldwind.util.GeographicImageInterpolator;


public class GliderImageInterpolator extends GeographicImageInterpolator
{
	
    /**
     * Creates a new GliderImageInterpolator, initializing this interpolator's internal image cell tree with root
     * cell dimensions of <code>(gridSize.width * gridSize.height)</code> and with the specified <code>depth</code>.
     * This extension of GeographicInterpolator provides access to protected data elements.  This may be necessary
     * for fixing some issues with large images and dateline crossing.  This currently doesn't do anything else
     * that GeographicImageInterpolar does, but is here in case modification is needed at a later date.
     *
     * @param gridSize the image's dimensions.
     * @param xs       the x-coordinates of the image's pixels. Must contain at least <code>gridSize.width *
     *                 gridSize.height</code> elements.
     * @param ys       the y-coordinates of the image's pixels. Must contain at least <code>gridSize.width *
     *                 gridSize.height</code> elements.
     * @param depth    the initial depth of this interpolator's image cell tree.
     * @param cellSize the size of a leaf cell in this interpolator's image cell tree, in pixels.
     *
     * @throws IllegalStateException if any of the the gridSize, x-coordinates, or y-coordinates are null, if either the
     *                               x-coordinates or y-coordinates contain less than <code>gridSize.width *
     *                               gridSize.height</code> elements, if the depth is less than zero, or if the cell
     *                               size is less than one.
     */
    public GliderImageInterpolator(Dimension gridSize, float[] xs, float[] ys, int depth, int cellSize)
    {
        super(gridSize, xs, ys, depth, cellSize);
    }
    protected static class GliderCell extends GeographicCell
    {
        /** Denotes if the pixels in this geographic image cell crosses the international dateline. */
        protected boolean crossesDateline;

        /**
         * Constructs a new Geographic Cell, but otherwise does nothing.
         *
         * @param m0 the cell's left image coordinate.
         * @param m1 the cell's right image coordinate.
         * @param n0 the cell's bottom image coordinate.
         * @param n1 the cell's top image coordinate.
         */
       
        public GliderCell(int m0, int m1, int n0, int n1)
        {
            super(m0, m1, n0, n1);
        }
        @Override
        protected Cell makeChildCell(int m0, int m1, int n0, int n1)
        {
            return new GliderCell(m0, m1, n0, n1);
        }
        public float getMiny() 
        {
        	return super.miny;
        }
        public float getMaxy() 
        {
        	return super.maxy;
        }
        public float getMinx() 
        {
        	return super.minx;
        }
        public float getMaxx() 
        {
        	return super.maxx;
        }
    }
    @Override
    protected Cell makeRootCell(int m0, int m1, int n0, int n1)
    {
        return new GliderCell(m0, m1, n0, n1);
    }

    /**
     * Returns the sector containing the image's geographic coordinates. This returns a sector which spans the longitude
     * range [-180, 180] if the image crosses the international dateline.
     *
     * @return the image's bounding sector.
     */
    @Override
    public Sector getSector()
    {
        return ((GeographicCell) this.root).isCrossesDateline() ?
                Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), -180, 180) :
                Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), ((GliderCell) this.root).getMinx(), ((GliderCell) this.root).getMaxx());
//        return (Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), ((GliderCell) this.root).getMinx(), ((GliderCell) this.root).getMaxx()));
    }
    public ArrayList<Sector> getSectorsDateline()
    {
    	ArrayList<Sector> sec=new ArrayList<Sector>();
    	if (((GeographicCell) this.root).isCrossesDateline()) {
    		sec.add(Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), ((GliderCell) this.root).getMinx(), 180));
    		sec.add(Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), -180, ((GliderCell) this.root).getMaxx()));
    		
    	}
    	else {
    		sec.add(Sector.fromDegrees(((GliderCell) this.root).getMiny(), ((GliderCell) this.root).getMaxy(), ((GliderCell) this.root).getMinx(), ((GliderCell) this.root).getMaxx()));
    	}
		return sec;
    }
}
