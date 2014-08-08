/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package edu.uah.itsc.worldwind.eclipse.glider;

//import gov.nasa.worldwind.util.GeographicImageInterpolator;
import gov.nasa.worldwind.util.ImageInterpolator;
import gov.nasa.worldwind.util.ImageUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.*;

import java.awt.Dimension;
import java.awt.image.*;
import java.beans.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;


import gov.nasa.worldwind.util.ImageUtil.AlignedImage;

/**
 * @author tag
 * @version $Id: GliderImage.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class GliderImage extends AVListImpl
{
    public static int DEFAULT_TILE_WIDTH = 256;
    public static int DEFAULT_TILE_HEIGHT = 256;

    public static final String GLIDER_REGIONS_OF_INTEREST = "gov.nasa.worldwind.glider.RegionsOfInterest";
    public static final String GLIDER_IMAGE_SOURCE = "gov.nasa.worldwind.glider.ImageSource";
    public static final String GLIDER_IMAGE_OPACITY = "gov.nasa.worldwind.glider.ImageOpacity";

    protected String name;
    protected Sector sector;
    protected List<LatLon> corners;
    protected double altitude;
    protected double opacity = 1;
    protected Object imageSource;
    static int numXTiles=0;
    static int numYTiles=0;
    protected PropertyChangeListener regionListener = new RegionListener();

    private CopyOnWriteArraySet<GliderRegionOfInterest> regionTable
        = new CopyOnWriteArraySet<GliderRegionOfInterest>();

    /**
     * Construct an image from a file.
     *
     * @param imageSource The path to the source image. Images can be any of those supported by {@link
     *                    javax.imageio.ImageIO}, as well as uncompressed TIFF images..
     * @param corners     The lat/lon locations of the region in which to map the image. The image will be stretched as
     *                    necessary to fully fill the region. The locations must be specified in counterclockwise order
     *                    beginning with the lower-left image corner.
     * @param altitude    The altitude at which to display the image. Specify 0 to have the image draped over the
     *                    globe's surface.
     *
     * @throws IllegalArgumentException if any of the first three arguments are null.
     */
    public GliderImage(String imageSource, Iterable<? extends LatLon> corners, double altitude)
    {
        this(imageSource, imageSource, corners, altitude);
    }

    /**
     * Construct an image from a file or {@link java.awt.image.BufferedImage} and an arbitrary bounding region.
     *
     * @param name         A unique name to identify the image. If the image source is a file, the file path can be used
     *                     as the name.
     * @param alignedImage An aligned image containing a {@link BufferedImage} and a {@link Sector} specifying the image
     *                     and the location to place it.The image will be stretched as necessary to fully fill the
     *                     region.
     * @param altitude     The altitude at which to display the image. Specify 0 to have the image draped over the
     *                     globe's surface.
     *
     * @throws IllegalArgumentException if any of the first three arguments are null.
     */
    public GliderImage(String name, ArrayList<ImageUtil.AlignedImage> alignedImageList, double altitude)
    {
        this(name, alignedImageList, null, altitude);    	    	
    }

    /**
     * Construct an image from a file or {@link java.awt.image.BufferedImage} and an arbitrary bounding region.
     *
     * @param name         A unique name to identify the image. If the image source is a file, the file path can be used
     *                     as the name.
     * @param alignedImage An aligned image containing a {@link BufferedImage} and a {@link Sector} specifying the image
     *                     and the location to place it.The image will be stretched as necessary to fully fill the
     *                     region.
     * @param altitude     The altitude at which to display the image. Specify 0 to have the image draped over the
     *                     globe's surface.
     *
     * @throws IllegalArgumentException if any of the first three arguments are null.
     */
    public GliderImage(String name, ImageUtil.AlignedImage alignedImage, double altitude)
    {
        this(name, alignedImage.image, alignedImage.sector, altitude);
    }
    /**
     * Construct an image from a file or {@link java.awt.image.BufferedImage} and an arbitrary bounding region.
     *
     * @param name        A unique name to identify the image. If the image source is a file, the file path can be used
     *                    as the name.
     * @param imageSource Either the file path to the source image or a reference to the {@link
     *                    java.awt.image.BufferedImage} containing it. Images can be any of those supported by {@link
     *                    javax.imageio.ImageIO}, as well as uncompressed TIFF images.
     * @param corners     The lat/lon locations of the region in which to map the image. The image will be stretched as
     *                    necessary to fully fill the region. The locations must be specified in counterclockwise order
     *                    beginning with the lower-left image corner.
     * @param altitude    The altitude at which to display the image. Specify 0 to have the image draped over the
     *                    globe's surface.
     *
     * @throws IllegalArgumentException if any of the first three arguments are null.
     */
    public GliderImage(String name, Object imageSource, Iterable<? extends LatLon> corners, double altitude)
    {
        if (name == null)
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (imageSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (corners == null)
        {
            String message = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
            // now sector can be null if aligned image is tiled
        }

        this.name = name;
        this.imageSource = imageSource;
        // for arraylists, get max/min lat lons and use to set up sector and corners for overall image extent
        if ((this.imageSource instanceof ArrayList) && corners==null) {
        	ArrayList<AlignedImage> alignedImageList = (ArrayList<AlignedImage>) this.imageSource;
    	    double minlat=90, maxlat=-90, minlon=180, maxlon=-180;
    	    for (int ind1=0;ind1<alignedImageList.size();ind1++) {
    		    if (alignedImageList.get(ind1).sector.getMaxLatitude().degrees>maxlat) maxlat=alignedImageList.get(ind1).sector.getMaxLatitude().degrees;
    		    if (alignedImageList.get(ind1).sector.getMaxLongitude().degrees>maxlon) maxlon=alignedImageList.get(ind1).sector.getMaxLongitude().degrees;
    		    if (alignedImageList.get(ind1).sector.getMinLatitude().degrees<minlat) minlat=alignedImageList.get(ind1).sector.getMinLatitude().degrees;
    		    if (alignedImageList.get(ind1).sector.getMinLongitude().degrees<minlon) minlon=alignedImageList.get(ind1).sector.getMinLongitude().degrees;
    	    }
    	    
    	    this.sector = new Sector(Angle.fromDegrees(minlat), Angle.fromDegrees(maxlat), Angle.fromDegrees(
    	            minlon), Angle.fromDegrees(maxlon));
    	    LatLon [] cornerLatLon = this.sector.getCorners();
    	    this.corners = new ArrayList<LatLon>();
    	    for (int ind1=0;ind1<cornerLatLon.length;ind1++) {
    	    	this.corners.add(cornerLatLon[ind1]);
    	    }

        }
        else { //if (corners!=null)
        	this.sector = Sector.boundingSector(corners);
	        this.corners = new ArrayList<LatLon>();
	        for (LatLon c : corners)
	        {
	            this.corners.add(c);
	        }
        }

        this.altitude = altitude;
    }
//
//    /**
//     * Copy constructor. A shallow copy is performed.
//     *
//     * @param image the image to copy from.
//     *
//     * @throws IllegalArgumentException if <code>image</code> is null.
//     */
//    public GliderImage(GliderImage image)
//    {
//        if (image == null)
//        {
//            String message = Logging.getMessage("nullValue.ImageIsNull");
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        this.name = image.getName();
//        this.imageSource = image.getImageSource();
//        this.sector = image.getSector();
//        this.altitude = image.getAltitude();
//        this.corners = image.corners;
//    }

    public void releaseImageSource()
    {
        this.imageSource = null;
    }

    /**
     * Returns the name of the image, as specified at construction. If no name was specified at construction the name is
     * that of the image file path.
     *
     * @return the image name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return the image's location.
     *
     * @return the image's location.
     */
    public Sector getSector()
    {
        return sector;
    }

    public List<LatLon> getCorners()
    {
        return Collections.unmodifiableList(this.corners);
    }

    /**
     * Return the image's altitude.
     *
     * @return the image's altitude.
     */
    public double getAltitude()
    {
        return altitude;
    }

    /**
     * Changes the image source. The allowable sources are those allowed by {@link #GliderImage}
     *
     * @param newSource the new image source.
     *
     * @throws IllegalArgumentException if <code>newSource</code> is null.
     */
    public void setImageSource(String newSource)
    {
        if (newSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setImageSource(newSource, newSource);
    }

    /**
     * Changes the image source and gives the image a new name. The allowable sources are those allowed by {@link
     * #GliderImage}
     *
     * @param newName   the new image name.
     * @param newSource the new image source.
     *
     * @throws IllegalArgumentException if either argument is null.
     */
    public void setImageSource(String newName, Object newSource)
    {
        if (newName == null)
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (newSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

//        GliderImage oldImage = new GliderImage(this);
        this.name = newName;
        this.imageSource = newSource;
        this.firePropertyChange(GLIDER_IMAGE_SOURCE, null, this);
    }

    /**
     * Returns the image source.
     *
     * @return the image source.
     */
    public Object getImageSource()
    {
        return imageSource;
    }

    public double getOpacity()
    {
        return opacity;
    }

    public void setOpacity(double opacity)
    {
        this.opacity = opacity;
        this.firePropertyChange(GLIDER_IMAGE_OPACITY, null, this);
    }

    /**
     * Adds a region of interest to display on the image.
     *
     * @param region the region of interest to add.
     *
     * @throws IllegalArgumentException if <code>region</code> is null.
     */
    public void addRegionOfInterest(GliderRegionOfInterest region)
    {
        if (region == null)
        {
            String message = Logging.getMessage("nullValue.RegionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        region.removePropertyChangeListener(this.regionListener); // prevent duplicate registrations
        region.addPropertyChangeListener(this.regionListener);

        if (this.regionTable.add(region))
            this.firePropertyChange(GLIDER_REGIONS_OF_INTEREST, null, this.getRegionsOfInterest());
    }

    /**
     * Removes a region of interest.
     *
     * @param region the region of interest to remove.
     *
     * @throws IllegalArgumentException if <code>region</code> is null.
     */
    public void removeRegionOfInterest(GliderRegionOfInterest region)
    {
        if (region == null)
        {
            String message = Logging.getMessage("nullValue.RegionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        region.removePropertyChangeListener(this.regionListener);

        if (this.regionTable.remove(region))
            this.firePropertyChange(GLIDER_REGIONS_OF_INTEREST, null, this.getRegionsOfInterest());
    }

    public GliderRegionOfInterest.RegionSet getRegionsOfInterest()
    {
        return new GliderRegionOfInterest.RegionSet(this.regionTable);
    }

    protected class RegionListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            //noinspection StringEquality
            if (evt.getPropertyName() == GliderRegionOfInterest.GLIDER_REGION_OF_INTEREST)
            {
                GliderImage.this.firePropertyChange(GLIDER_REGIONS_OF_INTEREST, null,
                    GliderImage.this.getRegionsOfInterest());
            }
        }
    }

    public static ImageUtil.AlignedImage alignImage(BufferedImage sourceImage, float minLat, float maxLat, float minLon, float maxLon)
    throws InterruptedException
    /*
     * this version assumes buffered image is alread in lat/lon coordinates, just compute grid and return 
     * aligned image
     */
	{
        return new gov.nasa.worldwind.util.ImageUtil.AlignedImage(sourceImage, Sector.fromDegrees(minLat, maxLat, minLon, maxLon));
    	
	}   
    
    /**
     * Reprojects an image into an aligned image, one with edges of constant latitude and longitude.
     *
     * @param sourceImage the image to reproject, typically a non-aligned image
     * @param latitudes   an array identifying the latitude of each pixels if the source image. There must be an entry
     *                    in the array for all pixels. The values are taken to be in row-major order relative to the
     *                    image -- the horizontal component varies fastest.
     * @param longitudes  an array identifying the longitude of each pixels if the source image. There must be an entry
     *                    in the array for all pixels. The values are taken to be in row-major order relative to the
     *                    image -- the horizontal component varies fastest.
     *
     * @return a new image containing the original image but reprojected to align to the sector. Pixels in the new image
     *         that have no correspondence with the source image are transparent.
     *
     * @throws InterruptedException if any thread has interrupted the current thread while alignImage is running. The
     *                              <i>interrupted status</i> of the current thread is cleared when this exception is
     *                              thrown.
     */
    
    // overrides ImageUtil definition
    

    public static ImageUtil.AlignedImage alignImage(BufferedImage sourceImage, float[] latitudes, float[] longitudes)
    throws InterruptedException
    {
    	return alignImage(sourceImage, latitudes, longitudes,1);
    }
    public static ArrayList<ImageUtil.AlignedImage> alignImageTiled(BufferedImage sourceImage, float[] latitudes, float[] longitudes, int sampleFactor)
    throws InterruptedException
	{
		
		System.out.println("GliderImage.alignImage called");
		
		if (latitudes==null || longitudes==null) {
			System.err.println("null lat or lon");
		}
	    if (sourceImage == null)
	    {
	        String message = Logging.getMessage("nullValue.ImageIsNull");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    if (latitudes == null || longitudes == null || latitudes.length != longitudes.length)
	    {
	        String message = Logging.getMessage("ImageUtil.FieldArrayInvalid");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    int sourceWidth = sourceImage.getWidth();
	    int sourceHeight = sourceImage.getHeight();
	
	    if (sourceWidth < 1 || sourceHeight < 1)
	    {
	        String message = Logging.getMessage("ImageUtil.EmptyImage");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    if (longitudes.length < sourceWidth * sourceHeight || latitudes.length < sourceWidth * sourceHeight)
	    {
	        String message = Logging.getMessage("ImageUtil.FieldArrayTooShort");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	    
	    // start here
	    // split up image into smaller subimages and perform interpolation on each sub image then combine returned aligned images
	    // into overall image.  This should cut down on temporary memory required for interpolating subimages, allowing
	    // larger images to be processed without running out of memory
	    
	    // allocate final result image
//	    BufferedImage destImage = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_4BYTE_ABGR);

//		int[] bkgColors = new int[sourceWidth*sourceHeight];
//	    for (int i=0;i<sourceWidth * sourceHeight;i++) bkgColors[i]=0xff0000ff;
//        destImage.setRGB(0,0,sourceWidth,sourceHeight,bkgColors,0,sourceWidth);
//        bkgColors=null;
	    
	    
	    double minlat=90, maxlat=-90, minlon=180, maxlon=-180;
	    for (int ind1=0;ind1<sourceHeight;ind1++) {
	    	for (int ind2=0;ind2<sourceWidth;ind2++) {
    		    if (latitudes[ind1*sourceWidth+ind2]>maxlat) maxlat=latitudes[ind1*sourceWidth+ind2];
    		    if (longitudes[ind1*sourceWidth+ind2]>maxlon) maxlon=longitudes[ind1*sourceWidth+ind2];
    		    if (latitudes[ind1*sourceWidth+ind2]<minlat) minlat=latitudes[ind1*sourceWidth+ind2];
    		    if (longitudes[ind1*sourceWidth+ind2]<minlon) minlon=longitudes[ind1*sourceWidth+ind2];
	    	}
	    }
	    
	    Sector fullSector = new Sector(Angle.fromDegrees(minlat), Angle.fromDegrees(maxlat), Angle.fromDegrees(
	            minlon), Angle.fromDegrees(maxlon));
	    int xTileBlocksize = DEFAULT_TILE_WIDTH ;
	    int yTileBlocksize = DEFAULT_TILE_HEIGHT;
	    	    
//	    	Sector.fromDegrees(minlat, maxlat, minlon, maxlon);        
//	    numXTiles = (sourceWidth%xTileBlocksize==0)?sourceWidth/xTileBlocksize:(sourceWidth/xTileBlocksize)+1;
//	    numYTiles = (sourceHeight%yTileBlocksize==0)?sourceHeight/yTileBlocksize:(sourceHeight/yTileBlocksize)+1;
	    numYTiles = 0;
	    ArrayList<ImageUtil.AlignedImage> tiles = new ArrayList<ImageUtil.AlignedImage>();
	    double scaleFactor=0.0;
	    // overlap by 10 pixels to avoid gaps caused by viewing angle i.e. bowtie effect in MODIS
		for (int ind1=0;ind1<sourceHeight;ind1+=yTileBlocksize-10,numYTiles++) {
    		int currentHeight = (sourceHeight - ind1)<yTileBlocksize?(sourceHeight - ind1):yTileBlocksize;
    	    numXTiles = 0;
	    	for (int ind2=0;ind2<sourceWidth;ind2+=xTileBlocksize-10,numXTiles++) {
	    		// break down into blocksize chunks
	    		int currentWidth = (sourceWidth - ind2)<xTileBlocksize?(sourceWidth - ind2):xTileBlocksize;
	    		   // Get a buffer containing the source image's pixels.
    		    int[] sourceColors = sourceImage.getRGB(ind2, ind1, currentWidth, currentHeight, null, 0, currentWidth);

	    	    // define subregion sizes and split
    			float [] latSub = new float[currentHeight*currentWidth];
    			float [] lonSub = new float[currentHeight*currentWidth];
   			
    			for (int cnt=0,sub1=0;sub1<currentHeight;sub1++) {
    				for (int sub2=0;sub2<currentWidth;sub2++) {
//	    					latSub[sub1*currentWidth+sub2] = latitudes[(ind1+sub1)*sourceWidth+ind2+sub2];
    					latSub[cnt] = latitudes[(ind1+sub1)*sourceWidth+ind2+sub2];
    					lonSub[cnt++] = longitudes[(ind1+sub1)*sourceWidth+ind2+sub2];
    				}
    			}
    			
    			// compute rotation of image and enlarge projected image to account for rotation
    			// look at first line of lat/lon, only do this for first tile
    			
				double lat1=latSub[0];
				double lon1=lonSub[0];
				double lat2=latSub[currentWidth-1];
				double lon2=lonSub[currentWidth-1];
    			
    			double dlat=Math.abs(lat2-lat1);
    			double dlon=Math.abs(lon2-lon1);
    			double rotationAngle = (dlon>0)?Math.atan(dlat/dlon):0.0;
 //   			double dx=currentHeight*
    			
    			// scale final image size for rotation of image due to satellite azimutal angle
    			double d=Math.sqrt(dlat*dlat+dlon*dlon);
    			scaleFactor = (dlat>dlon)?(d/dlat):(d/dlon);
    			System.out.println("angle " + rotationAngle + " scaleFactor " + scaleFactor);
    			if (scaleFactor<1.0) scaleFactor=1.0;
    			
//    			int scaledWidth = (currentWidth<xTileBlocksize)?xTileBlocksize:currentWidth;
    			int scaledWidth = currentWidth;
//    			scaledWidth /= sampleFactor;
    			scaledWidth = (int)((double)scaledWidth*scaleFactor);
//    			int scaledHeight = (currentHeight<yTileBlocksize)?yTileBlocksize:currentHeight;
    			int scaledHeight = currentHeight;
//    			scaledHeight /= sampleFactor;
    			scaledHeight = (int)((double)scaledHeight*scaleFactor);
    			
    			
//    		    GliderImageInterpolator grid = new GliderImageInterpolator(new Dimension(scaledWidth, scaledHeight),
//    		            lonSub, latSub, 100, 1);
 
    		    // If the caller did not specify a Sector, then use the image's bounding sector as computed by
    		    // GeographicImageInterpolator. We let GeographicImageInterpolator perform the computation because it computes
    		    // the correct sector for images which cross the international dateline.
    		    GliderImageInterpolator grid = new GliderImageInterpolator(new Dimension(currentWidth, currentHeight),
    		            lonSub, latSub, 100, 1);
    			
    		    ArrayList <Sector> sectors = grid.getSectorsDateline();
  		        			
    			   		
    		    for (Sector sector:sectors) {
	    		    // Compute the geographic dimensions of the aligned image's pixels. We divide by the dimension instead of
	    		    // dimension-1 because we treat the aligned image pixels as having area.
	    		    double dLon = sector.getDeltaLonDegrees() / (scaledWidth);
	    		    double dLat = sector.getDeltaLatDegrees() / (scaledHeight);
	    		
	    			// scale for resolution relative to entire sector of image coverage, tries to compensate for 
	    			// decreased resolution toward edges, i.e. viewing angle and MODIS bowtie effect
	    			
	    		    double deltaLonScale = dLon/(fullSector.getDeltaLonDegrees()/sourceWidth);
	    		    deltaLonScale = (deltaLonScale<1)?1.0:deltaLonScale;
	    		    double deltaLatScale = dLat/(fullSector.getDeltaLatDegrees()/sourceHeight);
	    		    deltaLatScale = (deltaLatScale<1)?1.0:deltaLatScale;
	    		    
	    		    System.out.println("deltaLatScale " + deltaLatScale + " deltaLonScale " + deltaLonScale);
	    		    scaledHeight *= Math.max(deltaLatScale, deltaLonScale)/sampleFactor;
	    		    scaledWidth *= Math.max(deltaLatScale, deltaLonScale)/sampleFactor;
//	    		    int scaledSectorHeight = (int)((float)scaledHeight*deltaLatScale);
//	    		    int scaledSectorWidth = (int)((float)scaledWidth*deltaLonScale);
	
	    		    // update dLon and Dlat for image resolution rescaling 
	    		    dLon = sector.getDeltaLonDegrees() / (scaledWidth);
	    		    dLat = sector.getDeltaLatDegrees() / (scaledHeight);
//	    		    dLon = sector.getDeltaLonDegrees() / (scaledSectorWidth);
//	    		    dLat = sector.getDeltaLatDegrees() / (scaledSectorHeight);
	
	    		    
	    		    System.out.println(" sector.deltaLonDegrees " + sector.getDeltaLonDegrees() + " sector.deltaLatDegrees " + sector.getDeltaLatDegrees());
	    			System.out.println(" Dimensions x " + scaledWidth + " y  " + scaledHeight);
	    			System.out.println(" sector MinLat" + sector.getMinLatitude() + " maxLat  " + sector.getMaxLatitude() + " minLon "+ sector.getMinLongitude() + " maxLon  " + sector.getMaxLongitude());
	
	    			// allocate sub image
		    	    BufferedImage subImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_4BYTE_ABGR);
	    		    int[] destColors = new int[scaledWidth*scaledHeight];
	    		    
	//	    	    for (int i=0;i<scaledWidth * scaledHeight;i++) destColors[i]=0xff0000ff;
	//	            subImage.setRGB(0,0,scaledWidth,scaledHeight,destColors,0,scaledWidth);
	
	    		    // Create a buffer for the aligned image pixels, initially filled with transparent values.
	   		
	    		    
	    		    // Compute each aligned image pixel's color by mapping its location into the source image. This treats the
	    		    // aligned image pixel's as having area, and the location of each pixel's at its center. This loop begins in the
	    		    // center of the upper left hand pixel and continues in row major order across the image, stepping by a pixels
	    		    // geographic size.
	    		    for (int j = 0; j < scaledHeight; j++)
	    		    {
	    		        // Generate an InterruptedException if the current thread is interrupted. Responding to thread interruptions
	    		        // before processing each image row ensures that this method terminates in a reasonable amount of time after
	    		        // the currently executing thread is interrupted, but without consuming unecessary CPU time. Using either
	    		        // Thread.sleep(1), or executing Thread.sleep() for each pixel would unecessarily increase the this methods
	    		        // total CPU time.
	    		        Thread.sleep(0);
	   		
	    		//        float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLon / 2d);
	    		        float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLat / 2d);
	    		
	    		        for (int i = 0; i < scaledWidth; i++)
	    		        {
	    		//            float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLat / 2d);
	    		            float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLon / 2d);
	    		
	    		            // Search for a cell in the source image which contains this aligned image pixel's location.
	    		            ImageInterpolator.ContainingCell cell = grid.findContainingCell(lon, lat);
	   		
	    		            // If there's a source cell for this location, then write a color to the destination image by linearly
	    		            // interpolating between the four pixels at the cell's corners. Otherwise, don't change the destination
	    		            // image. This ensures pixels which don't correspond to the source image remain transparent.
	    		            if (cell != null)
	    		            {
	    		                int color = interpolateColor(cell.uv[0], cell.uv[1],
	    		                    sourceColors[cell.fieldIndices[0]],
	    		                    sourceColors[cell.fieldIndices[1]],
	    		                    sourceColors[cell.fieldIndices[3]],
	    		                    sourceColors[cell.fieldIndices[2]]
	    		                );
	    		                destColors[j * scaledWidth + i] = color;
	    		    //            System.out.println("destcolors ind" + (j * scaledWidth + i));
	    		            }
	    		        }
	    		    }
	 		        subImage.setRGB(0,0,scaledWidth,scaledHeight,destColors,0,scaledWidth);
			        tiles.add(new ImageUtil.AlignedImage(subImage, sector));
	    		    // Release memory used by source colors and the grid
	    		    //noinspection UnusedAssignment
    		    }
    		    sourceColors = null;
    		    //noinspection UnusedAssignment
    		    grid = null;
    		    
    		    
//    	        g.translate(ind2, sourceHeight - ind1);
//    	        g.scale(0.5, 0.5);
//    	        g.drawImage(subImage, 0, 0, null);


//    		    ImageUtil.mergeImage(fullSector, sector, 1.0, subImage, destImage);
	    	}
	    }
        // move this to alingImageTiled 
        
	    return tiles;
	}
    
//    public static void mergeImage(Sector canvasSector, Sector imageSector, double aspectRatio, BufferedImage image,
//            BufferedImage canvas)
//    {
//        if (canvasSector == null || imageSector == null)
//        {
//            String message = Logging.getMessage("nullValue.SectorIsNull");
//            Logging.logger().severe(message);
//            throw new IllegalStateException(message);
//        }
//
//        if (canvas == null || image == null)
//        {
//            String message = Logging.getMessage("nullValue.ImageSource");
//            Logging.logger().severe(message);
//            throw new IllegalStateException(message);
//        }
//
//        if (aspectRatio <= 0)
//        {
//            String message = Logging.getMessage("Util.AspectRatioInvalid", aspectRatio);
//            Logging.logger().severe(message);
//            throw new IllegalStateException(message);
//        }
//
//        if (!(canvasSector.intersects(imageSector)))
//            return;
//
//        // Create an image with the desired aspect ratio within an enclosing canvas of possibly different aspect ratio.
//        int subWidth = aspectRatio >= 1 ? canvas.getWidth() : (int) Math.ceil((canvas.getWidth() * aspectRatio));
//        int subHeight = aspectRatio >= 1 ? (int) Math.ceil((canvas.getHeight() / aspectRatio)) : canvas.getHeight();
//
//        // yShift shifts image down to change origin from upper-left to lower-left
//        double yShift = aspectRatio >= 1d ? (1d - 1d / aspectRatio) * canvas.getHeight() : 0d;
//
//        double sh = ((double) subHeight / (double) image.getHeight())
//            * (imageSector.getDeltaLat().divide(canvasSector.getDeltaLat()));
//        double sw = ((double) subWidth / (double) image.getWidth())
//            * (imageSector.getDeltaLon().divide(canvasSector.getDeltaLon()));
//
//        double dh = subHeight *
//            (-imageSector.getMaxLatitude().subtract(canvasSector.getMaxLatitude()).degrees
//                / canvasSector.getDeltaLat().degrees);
//        double dw = subWidth *
//            (imageSector.getMinLongitude().subtract(canvasSector.getMinLongitude()).degrees
//                / canvasSector.getDeltaLon().degrees);
//
//        Graphics2D g = canvas.createGraphics();
//        g.translate(dw, dh + yShift);
//        g.scale(sw, sh);
//        g.drawImage(image, 0, 0, null);
//    }


//    public static ImageUtil.AlignedImage alignImage(BufferedImage sourceImage, float[] latitudes, float[] longitudes, int sampleFactor)
//        throws InterruptedException
    public static ImageUtil.AlignedImage alignImage(BufferedImage sourceImage, float[] latitudes, float[] longitudes, int sampleFactor)
        throws InterruptedException
    {
    	
		System.out.println("GliderImage.alignImage called");
		
		if (latitudes==null || longitudes==null) {
			System.err.println("null lat or lon");
		}
        if (sourceImage == null)
        {
            String message = Logging.getMessage("nullValue.ImageIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (latitudes == null || longitudes == null || latitudes.length != longitudes.length)
        {
            String message = Logging.getMessage("ImageUtil.FieldArrayInvalid");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        if (sourceWidth < 1 || sourceHeight < 1)
        {
            String message = Logging.getMessage("ImageUtil.EmptyImage");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (longitudes.length < sourceWidth * sourceHeight || latitudes.length < sourceWidth * sourceHeight)
        {
            String message = Logging.getMessage("ImageUtil.FieldArrayTooShort");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
// this is only change from ImageUtil version, changed tree depth to 100 to ensure pixel level interpolation
 //       GeographicImageInterpolator grid = new GeographicImageInterpolator(new Dimension(sourceWidth, sourceHeight),
 //           longitudes, latitudes, 100, 1);
        // made new extension to GeograpicImageInterpolator to gain access to protected members
        // eventually may may modifications to the interpolation to improve resolution and projection issues
        GliderImageInterpolator grid = new GliderImageInterpolator(new Dimension(sourceWidth, sourceHeight),
                longitudes, latitudes, 100, 1);

        // If the caller did not specify a Sector, then use the image's bounding sector as computed by
        // GeographicImageInterpolator. We let GeographicImageInterpolator perform the computation because it computes
        // the correct sector for images which cross the international dateline.
        Sector sector = grid.getSector();

        // If the caller did not specify a dimension for the aligned image, then compute its dimensions as follows:
        //
        // 1. Assign the output width or height to the source image's maximum dimension, depending on the output
        //    sector's largest delta (latitude or longitude).
        // 2. Compute the remaining output dimension so that it has the same geographic resolution as the dimension
        //    from #1. This computed dimension is always less than or equal to the dimension from #1.
        //
        // This has the effect of allocating resolution where the aligned image needs it most, and gives the aligned
        // image square pixels in geographic coordinates. Without square pixels the aligned image's resolution can be
        // extremely anisotriopic, causing severe aliasing in one dimension.
        double maxDimension = Math.max(sourceWidth, sourceHeight);
        
        if (sector.getMinLatitude().degrees <=180.0 && sector.getMaxLatitude().degrees>=180.0) {
        	System.out.println("sector crossed dateline");
        	// figure out how min and max are set by sector constructor
        }
        double maxSectorDelta = Math.max(sector.getDeltaLonDegrees(), sector.getDeltaLatDegrees());
        double pixelsPerDegree = maxDimension / maxSectorDelta;
        
        int xDim = (int) Math.round(pixelsPerDegree * sector.getDeltaLonDegrees());
        int yDim = (int) Math.round(pixelsPerDegree * sector.getDeltaLatDegrees());
        
        // TAB: changed dimensions of sector to always use a minimum resolution of the original height
        if (xDim < sourceWidth) xDim = sourceWidth;
        if (yDim < sourceHeight) yDim = sourceHeight;
        xDim /= sampleFactor;
        yDim /= sampleFactor;
        Dimension dimension = new Dimension(xDim, yDim);

		System.out.println("GliderImage.alignImage maxDimension " + maxDimension + " maxSectorDelta " + maxSectorDelta + " pixelsPerDegree " + pixelsPerDegree);
		System.out.println(" sector.deltaLonDegrees " + sector.getDeltaLonDegrees() + " sector.deltaLatDegrees " + sector.getDeltaLatDegrees());
		System.out.println(" Dimensions x " + xDim + " y  " + yDim);
		System.out.println(" sector MinLat" + sector.getMinLatitude() + " maxLat  " + sector.getMaxLatitude() + " minLon "+ sector.getMinLongitude() + " maxLon  " + sector.getMaxLongitude());
       // Get a buffer containing the source image's pixels.
        int[] sourceColors = sourceImage.getRGB(0, 0, sourceWidth, sourceHeight, null, 0, sourceWidth);
        // Create a buffer for the aligned image pixels, initially filled with transparent values.
        int[] destColors = new int[dimension.width * dimension.height];
//        for (int i=0;i<dimension.width * dimension.height;i++) destColors[i]=0xffffffff;

        // Compute the geographic dimensions of the aligned image's pixels. We divide by the dimension instead of
        // dimension-1 because we treat the aligned image pixels as having area.
        double dLon = sector.getDeltaLonDegrees() / dimension.width;
        double dLat = sector.getDeltaLatDegrees() / dimension.height;

        // Compute each aligned image pixel's color by mapping its location into the source image. This treats the
        // aligned image pixel's as having area, and the location of each pixel's at its center. This loop begins in the
        // center of the upper left hand pixel and continues in row major order across the image, stepping by a pixels
        // geographic size.
        for (int j = 0; j < dimension.height; j++)
        {
            // Generate an InterruptedException if the current thread is interrupted. Responding to thread interruptions
            // before processing each image row ensures that this method terminates in a reasonable amount of time after
            // the currently executing thread is interrupted, but without consuming unecessary CPU time. Using either
            // Thread.sleep(1), or executing Thread.sleep() for each pixel would unecessarily increase the this methods
            // total CPU time.
            Thread.sleep(0);

//            float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLon / 2d);
	        float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLat / 2d);

            for (int i = 0; i < dimension.width; i++)
            {
//                float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLat / 2d);
	            float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLon / 2d);

                // Search for a cell in the source image which contains this aligned image pixel's location.
                ImageInterpolator.ContainingCell cell = grid.findContainingCell(lon, lat);

                // If there's a source cell for this location, then write a color to the destination image by linearly
                // interpolating between the four pixels at the cell's corners. Otherwise, don't change the destination
                // image. This ensures pixels which don't correspond to the source image remain transparent.
                if (cell != null)
                {
                    int color = interpolateColor(cell.uv[0], cell.uv[1],
                        sourceColors[cell.fieldIndices[0]],
                        sourceColors[cell.fieldIndices[1]],
                        sourceColors[cell.fieldIndices[3]],
                        sourceColors[cell.fieldIndices[2]]
                    );

                    destColors[j * dimension.width + i] = color;
                }
            }
        }

        // Release memory used by source colors and the grid
        //noinspection UnusedAssignment
        sourceColors = null;
        //noinspection UnusedAssignment
        grid = null;

        BufferedImage destImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR);
        destImage.setRGB(0, 0, dimension.width, dimension.height, destColors, 0, dimension.width);

        return new gov.nasa.worldwind.util.ImageUtil.AlignedImage(destImage, sector);
    }
//    public static ImageUtil.AlignedImage alignImage(BufferedImage sourceImage, float[] latitudes, float[] longitudes)
//	    throws InterruptedException
//	{
//	    return ImageUtil.alignImage(sourceImage, latitudes, longitudes, null, null);
//	}

    public static AlignedFloatImage alignFloatImage(float [] sourceImage, float[] latitudes, float[] longitudes, int width, int height, int sampleFactor,boolean [] missingValues)
    throws InterruptedException
	{
		
		System.out.println("GliderImage.alignFloatImage called");
		
		if (latitudes==null || longitudes==null) {
			System.err.println("null lat or lon");
		}
	    if (sourceImage == null)
	    {
	        String message = Logging.getMessage("nullValue.ImageIsNull");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    if (latitudes == null || longitudes == null || latitudes.length != longitudes.length)
	    {
	        String message = Logging.getMessage("ImageUtil.FieldArrayInvalid");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    int sourceWidth = width;
	    int sourceHeight = height;
	
	    if (sourceWidth < 1 || sourceHeight < 1)
	    {
	        String message = Logging.getMessage("ImageUtil.EmptyImage");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }
	
	    if (longitudes.length < sourceWidth * sourceHeight || latitudes.length < sourceWidth * sourceHeight)
	    {
	        String message = Logging.getMessage("ImageUtil.FieldArrayTooShort");
	        Logging.logger().severe(message);
	        throw new IllegalStateException(message);
	    }

	    GliderImageInterpolator grid = new GliderImageInterpolator(new Dimension(sourceWidth, sourceHeight),
	            longitudes, latitudes, 100, 1);
	
	    // If the caller did not specify a Sector, then use the image's bounding sector as computed by
	    // GeographicImageInterpolator. We let GeographicImageInterpolator perform the computation because it computes
	    // the correct sector for images which cross the international dateline.
	    Sector sector = grid.getSector();
	
	    // If the caller did not specify a dimension for the aligned image, then compute its dimensions as follows:
	    //
	    // 1. Assign the output width or height to the source image's maximum dimension, depending on the output
	    //    sector's largest delta (latitude or longitude).
	    // 2. Compute the remaining output dimension so that it has the same geographic resolution as the dimension
	    //    from #1. This computed dimension is always less than or equal to the dimension from #1.
	    //
	    // This has the effect of allocating resolution where the aligned image needs it most, and gives the aligned
	    // image square pixels in geographic coordinates. Without square pixels the aligned image's resolution can be
	    // extremely anisotriopic, causing severe aliasing in one dimension.
	    double maxDimension = Math.max(sourceWidth, sourceHeight);
	    
	    if (sector.getMinLatitude().degrees <=180.0 && sector.getMaxLatitude().degrees>=180.0) {
	    	System.out.println("sector crossed dateline");
	    	// figure out how min and max are set by sector constructor
	    }
	    double maxSectorDelta = Math.max(sector.getDeltaLonDegrees(), sector.getDeltaLatDegrees());
	    double pixelsPerDegree = maxDimension / maxSectorDelta;
	    
	    int xDim = (int) Math.round(pixelsPerDegree * sector.getDeltaLonDegrees());
	    int yDim = (int) Math.round(pixelsPerDegree * sector.getDeltaLatDegrees());
	    
	    // TAB: changed dimensions of sector to always use a minimum resolution of the original height
	    if (xDim < sourceWidth) xDim = sourceWidth;
	    if (yDim < sourceHeight) yDim = sourceHeight;
	    xDim /= sampleFactor;
	    yDim /= sampleFactor;
	    Dimension dimension = new Dimension(xDim, yDim);
	
		System.out.println("GliderImage.alignImage maxDimension " + maxDimension + " maxSectorDelta " + maxSectorDelta + " pixelsPerDegree " + pixelsPerDegree);
		System.out.println(" sector.deltaLonDegrees " + sector.getDeltaLonDegrees() + " sector.deltaLatDegrees " + sector.getDeltaLatDegrees());
		System.out.println(" Dimensions x " + xDim + " y  " + yDim);
		System.out.println(" sector MinLat" + sector.getMinLatitude() + " maxLat  " + sector.getMaxLatitude() + " minLon "+ sector.getMinLongitude() + " maxLon  " + sector.getMaxLongitude());
	   // Get a buffer containing the source image's pixels.
		
	    // Create a buffer for the aligned image pixels, initially filled with transparent values.
	    float[] destImage = new float[dimension.width * dimension.height];
	//    for (int i=0;i<dimension.width * dimension.height;i++) destColors[i]=0xffffffff;
	
	    // Compute the geographic dimensions of the aligned image's pixels. We divide by the dimension instead of
	    // dimension-1 because we treat the aligned image pixels as having area.
	    double dLon = sector.getDeltaLonDegrees() / dimension.width;
	    double dLat = sector.getDeltaLatDegrees() / dimension.height;
	
	    // Compute each aligned image pixel's color by mapping its location into the source image. This treats the
	    // aligned image pixel's as having area, and the location of each pixel's at its center. This loop begins in the
	    // center of the upper left hand pixel and continues in row major order across the image, stepping by a pixels
	    // geographic size.
	    for (int j = 0; j < dimension.height; j++)
	    {
	        // Generate an InterruptedException if the current thread is interrupted. Responding to thread interruptions
	        // before processing each image row ensures that this method terminates in a reasonable amount of time after
	        // the currently executing thread is interrupted, but without consuming unecessary CPU time. Using either
	        // Thread.sleep(1), or executing Thread.sleep() for each pixel would unecessarily increase the this methods
	        // total CPU time.
	        Thread.sleep(0);
	
	//        float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLon / 2d);
	        float lat = (float) (sector.getMaxLatitude().degrees - j * dLat - dLat / 2d);
	
	        for (int i = 0; i < dimension.width; i++)
	        {
	//            float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLat / 2d);
	            float lon = (float) (sector.getMinLongitude().degrees + i * dLon + dLon / 2d);
	
	            // Search for a cell in the source image which contains this aligned image pixel's location.
	            ImageInterpolator.ContainingCell cell = grid.findContainingCell(lon, lat);
	
	            // If there's a source cell for this location, then write a color to the destination image by linearly
	            // interpolating between the four pixels at the cell's corners. Otherwise, don't change the destination
	            // image. This ensures pixels which don't correspond to the source image remain transparent.
	            if (cell != null)
	            {
	            	float value;
	            	if (missingValues!=null) {
		                value = interpolateValue(cell.uv[0], cell.uv[1],
		                    sourceImage[cell.fieldIndices[0]],
		                    sourceImage[cell.fieldIndices[1]],
		                    sourceImage[cell.fieldIndices[3]],
		                    sourceImage[cell.fieldIndices[2]],
		                    missingValues[cell.fieldIndices[0]],
		                    missingValues[cell.fieldIndices[1]],
		                    missingValues[cell.fieldIndices[3]],
		                    missingValues[cell.fieldIndices[2]]
		                    );
	            		
	            	}
	            	else {
		                value = interpolateFloat(cell.uv[0], cell.uv[1],
			                    sourceImage[cell.fieldIndices[0]],
			                    sourceImage[cell.fieldIndices[1]],
			                    sourceImage[cell.fieldIndices[3]],
			                    sourceImage[cell.fieldIndices[2]]
			                                
			                );
	            		
	            	}
	
	//                System.out.println("height value "+ value);
	                destImage[j * dimension.width + i] = value;
	            }
	            else {
	            	destImage[j * dimension.width + i] = 0;
	//                System.out.println("cell is null");
	            	
	            }
	        }
	    }
	
	    // Release memory used by source colors and the grid
	    // grid = null;
		
	    return new AlignedFloatImage(destImage, sector, dimension.width, dimension.height);
	}
    public static class AlignedFloatImage
    {
        public final Sector sector;
        public final float [] image;
        public final int xSize, ySize;

        public AlignedFloatImage(float [] image, Sector sector, int xSize, int ySize)
        {
        	this.xSize = xSize;
        	this.ySize = ySize;
            this.image = image;
            this.sector = sector;
        }
    }
    public static float interpolateFloat(double x, double y, float c0, float c1, float c2, float c3)
    {

        double rx = 1.0d - x;
        double ry = 1.0d - y;

        double x0 = rx * c0 + x * c1;
        double x1 = rx * c2 + x * c3;
        float c = (float) (ry * x0 + y * x1);

        return c;
    }
    public static float interpolateValue(double x, double y, float c0, float c1, float c2, float c3, boolean b0, boolean b1, boolean b2, boolean b3)
    {
 
        double rx = 1.0d - x;
        double dx = x;
        
        double x0,x1;
        if (b0&&b1) {
        	rx=0;
        	dx=0;        	
        }
        else if (b0) {
        	rx=0;
        	dx=1;
        }
        else if (b1) {
        	rx=1;
        	dx=0;
        }
        x0 = rx * c0 + dx * c1;  
        if (b2&&b3) {
        	rx=0;
        	dx=0;        	
        }
        else if (b2) {
        	rx=0;
        	dx=1;
        }
        else if (b3) {
        	rx=1;
        	dx=0;
        }
        x1 = rx * c2 + dx * c3;
        double ry = 1.0d - y;
        double dy = y;
        
        if (x0==0&&x1==0) {
        	return 0;        	
        }
        else if (x0==0) {
        	ry=0;
        	dy=1;
        }
        else if (x1==0) {
        	ry=1;
        	dy=0;
        }
        float value = (float) (ry * x0 + dy * x1);  //final value
 
        return value;
    }
    public static int interpolateColor(double x, double y, int c0, int c1, int c2, int c3)
    {
        //pull out alpha, red, green, blue values for each pixel
        int a0 = (c0 >> 24) & 0xff;
        int r0 = (c0 >> 16) & 0xff;
        int g0 = (c0 >> 8) & 0xff;
        int b0 = c0 & 0xff;

        int a1 = (c1 >> 24) & 0xff;
        int r1 = (c1 >> 16) & 0xff;
        int g1 = (c1 >> 8) & 0xff;
        int b1 = c1 & 0xff;

        int a2 = (c2 >> 24) & 0xff;
        int r2 = (c2 >> 16) & 0xff;
        int g2 = (c2 >> 8) & 0xff;
        int b2 = c2 & 0xff;

        int a3 = (c3 >> 24) & 0xff;
        int r3 = (c3 >> 16) & 0xff;
        int g3 = (c3 >> 8) & 0xff;
        int b3 = c3 & 0xff;

        double rx = 1.0d - x;
        double ry = 1.0d - y;

//        double x0 = rx * a0 + x * a1;
//        double x1 = rx * a2 + x * a3;
//        int a = (int) (ry * x0 + y * x1);  //final alpha value
        boolean m0=false,m1=false,m2=false,m3=false;
        if (a0==0) m0=true;
        if (a1==0) m1=true;
        if (a2==0) m2=true;
        if (a3==0) m3=true;
        
        int a;
        if (m0&&m1&&m2&&m3) {  // missing data, return transparent pixel 
        	return(0);
        }
        a = (int) 255;
        a = a << 24;

//        x0 = rx * r0 + x * r1;
//        x1 = rx * r2 + x * r3;
//        int r = (int) (ry * x0 + y * x1); //final red value
        int r = (int)interpolateValue( x, y, r0, r1, r2, r3, m0,m1,m2,m3);
        r = r << 16;

//        x0 = rx * g0 + x * g1;
//        x1 = rx * g2 + x * g3;
//        int g = (int) (ry * x0 + y * x1); //final green value
        int g = (int)interpolateValue( x, y, g0, g1, g2, g3, m0,m1,m2,m3);
        g = g << 8;

//        x0 = rx * b0 + x * b1;
//        x1 = rx * b2 + x * b3;
//        int b = (int) (ry * x0 + y * x1); //final blue value
        int b = (int)interpolateValue( x, y, b0, b1, b2, b3, m0,m1,m2,m3);
        
        return (a | r | g | b);
    }
   public static void alignImageDump(BufferedImage sourceImage, float[] latitudes, float[] longitudes)
    {
        ImageUtil.alignImageDump(sourceImage, latitudes, longitudes);
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GliderImage that = (GliderImage) o;

        if (Double.compare(that.altitude, altitude) != 0)
            return false;
        if (corners != null ? !corners.equals(that.corners) : that.corners != null)
            return false;
//        if (imageSource != null ? !imageSource.equals(that.imageSource) : that.imageSource != null)
//            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (sector != null ? !sector.equals(that.sector) : that.sector != null)
            return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        long temp;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (sector != null ? sector.hashCode() : 0);
        result = 31 * result + (corners != null ? corners.hashCode() : 0);
        temp = altitude != +0.0d ? Double.doubleToLongBits(altitude) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
//        result = 31 * result + (imageSource != null ? imageSource.hashCode() : 0);
        return result;
    }
}
