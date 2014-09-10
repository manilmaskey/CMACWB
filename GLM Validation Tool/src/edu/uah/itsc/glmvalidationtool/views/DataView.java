package edu.uah.itsc.glmvalidationtool.views;

import edu.uah.itsc.glmvalidationtool.config.Config;
import edu.uah.itsc.glmvalidationtool.data.DataFilter;
import edu.uah.itsc.glmvalidationtool.data.DataFilterUpdate;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.LayerManagerLayer;
import gov.nasa.worldwindx.examples.util.PowerOfTwoPaddedImage;
import gov.nasa.worldwindx.examples.util.RandomShapeAttributes;
import gov.nasa.worldwindx.examples.util.StatusLayer;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CrosshairLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.Earth.CountryBoundariesLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class DataView extends ViewPart implements DataFilterUpdate{
	public static final String ID = "DataView";
    public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0; // ellipsoid equatorial getRadius, in meters
    protected static final RandomShapeAttributes randomAttrs = new RandomShapeAttributes();
    
	private Composite swtAwtContainer;
	private Frame awtFrame;
	private WorldWindowGLCanvas wwd;
//	private ExtendedGliderWorldWindow wwd;

//	private LayerManagerLayer layerManager;
	private GroundNetworkLayerManager layerManager;
	
	// original Geojson layers, changed to use csv
//	private RenderableLayer entlnFlashLayer=null;
//	private RenderableLayer nldnFlashLayer=null;
//	private RenderableLayer gld360Layer=null;
//	private RenderableLayer glmLayer=null;

	private GroundNetworkLayer entlnFlashLayer=null;
	private GroundNetworkLayer nldnFlashLayer=null;
	private GroundNetworkLayer gld360Layer=null;
	private GroundNetworkLayer glmLayer=null;
	
	private RenderableLayer flashLegendLayer = null;
    private ScreenAnnotation legendAnnotation;

	
	private static Color entlnColor;
	private static Color nldnColor;
	private static Color gld360Color;
	private static Color glmColor;
	
	private Map<Long, ArrayList<Renderable>> entlnFlashBuffer = new HashMap<Long, ArrayList<Renderable>>();
	private Map<Long, ArrayList<Renderable>> nldnFlashBuffer = new HashMap<Long, ArrayList<Renderable>>();
	private Map<Long, ArrayList<Renderable>> gld360Buffer = new HashMap<Long, ArrayList<Renderable>>();
	private Map<Long, ArrayList<Renderable>> glmBuffer = new HashMap<Long, ArrayList<Renderable>>();

	private RenderableLayer boundingBoxLayer=null;
	private CrosshairLayer crosshairLayer=null;
	
    private AnnotationPointPlacemark placeMark;
 //   private PointPlacemark mouseAn, latestAn;
    private GlobeAnnotation tooltipAnnotation;
    private Config conf = new Config();
    
    private ArrayList<GroundNetworkLayer> flashLayers = new ArrayList();

//    private String etln_flash_string = "http://54.83.58.23:8080/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash&maxFeatures=50&outputFormat=application/json";

    //  between '2011-08-03 19:00:04' and '2011-08-04 19:00:04'
 //   private String etln_flash_string = "http://54.83.58.23:8080/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash_view&outputFormat=application/json&cql_filter=datetime between '2011-08-03 19:00:04' and '2011-08-04 19:00:04'";
//	private String queryString;
//    private String etln_flash_string = "http://54.83.58.23:8080/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash&outputFormat=application/json";
//    private String etln_flash_string = "/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash&outputFormat=application/json";
//    private String etln_flash_string = "/geoserver/GLM/ows?service=WFS&version=1.0.0&outputFormat=application/json&request=GetFeature&typeName=GLM:etln_flash";
    DataFilter dataFilter = new DataFilter();
    
    public DataView() {
	}

    @Override
    public void dispose()
    {
    	super.dispose();
    	dataFilter.unregisterObject(this);
    }
	public void createPartControl(final Composite parent) {
		
//		if (true) return;
		
		
		this.swtAwtContainer = new Composite(parent, SWT.EMBEDDED);
		this.swtAwtContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.awtFrame = SWT_AWT.new_Frame(this.swtAwtContainer);
       
        // Adjust configuration values before instantiation
		// make earth display as flat projection
		
        Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());
		Configuration.setValue(AVKey.INITIAL_LONGITUDE,Double.toString(conf.getInitialLongitude()));

		entlnColor = conf.getEntlnColor();
		nldnColor = conf.getNldnColor();
		gld360Color = conf.getGld360Color();
		glmColor = conf.getGlmColor();
		
		// Init tooltip annotation
        tooltipAnnotation = new GlobeAnnotation("", Position.fromDegrees(0, 0, 0));
        Font font = Font.decode("Arial-Plain-16");
        tooltipAnnotation.getAttributes().setFont(font);
        tooltipAnnotation.getAttributes().setSize(new Dimension(400, 0));
        tooltipAnnotation.getAttributes().setDistanceMinScale(1);
        tooltipAnnotation.getAttributes().setDistanceMaxScale(1);
        tooltipAnnotation.getAttributes().setVisible(false);
 //       tooltipAnnotation.setPickEnabled(false);
        tooltipAnnotation.setPickEnabled(true);
        tooltipAnnotation.setAlwaysOnTop(true);
 
        //
////    	String filename = "C:\\Users\\Todd\\Lightning Data\\2013-06-01_ENTLN-flashes.dat";
//        String filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\ENIfls-2011-08-04.dat";
//        entlnFlashes = new LightningData(Config.DataType.ENTLN_FLASH);
////      filename = "C:\\Users\\Todd\\Lightning Data\\2013-06-01_ENTLN-strokes.dat";
//    	filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\ENIstk-2011-08-04.dat";
//        entlnStrokes = new LightningData(Config.DataType.ENTLN_STROKE);
////    	filename = "C:\\Users\\Todd\\Lightning Data\\Nflash2013.152_daily_v3_lit.raw";
//    	filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\Nflash-2011-08-04.dat";
//        nldnFlashes = new LightningData(Config.DataType.NLDN_FLASH);
////    	filename = "C:\\Users\\Todd\\Lightning Data\\Nstroke20130601_daily_v1_lit.raw";
//    	filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\Nstroke20110804_daily_v1_lit.raw";
//        nldnStrokes = new LightningData(Config.DataType.NLDN_STROKE);
////    	filename = "C:\\Users\\Todd\\Lightning Data\\gld360_20130601_daily_v1_lit.raw";
//    	filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\gld360_20110804_daily_v1_lit.raw";
//        gld360 = new LightningData(Config.DataType.GLD360);
//        
//        try {
//			entlnFlashes.ReadFile(filename);
//			entlnStrokes.ReadFile(filename);
//			nldnFlashes.ReadFile(filename);
//			nldnStrokes.ReadFile(filename);
//			gld360.ReadFile(filename);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
//    	try {
//			queryString = URLEncoder.encode("cql_filter=datetime between '2011-08-03 19:00:04' and '2011-08-04 19:00:04'", "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//        // ingest files into database for testing
//        try {
//			entlnFlashes.writeToDatabase();
//			entlnStrokes.writeToDatabase();
//			nldnFlashes.writeToDatabase();
//			nldnStrokes.writeToDatabase();
//			gld360.writeToDatabase();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        // if a wwd has already been created, get the first object and pass it to the constructor
        // to share resources
        
        // TODO: this seems to give an error when closing in eclipse, investigate further
//        if (WWEvent.getWWObjects().size()>0)
//        	this.wwd = new WorldWindowGLCanvas((WorldWindowGLCanvas)WWEvent.getWWObjects().get(0));
//        else 
        	this.wwd = new WorldWindowGLCanvas();

//		this.wwd = new ExtendedGliderWorldWindow();
		
		// set initial view position
				
		WWEvent.register(this.wwd);

		wwd.getView().addPropertyChangeListener("gov.nasa.worldwind.avkey.ViewObject", new PropertyChangeListener() {
		
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				ArrayList <Object> changed = WWEvent.changedObjects(wwd);
				for (Object obj:changed) {
//					ExtendedGliderWorldWindow wwObj =(ExtendedGliderWorldWindow) obj;
					WorldWindowGLCanvas wwObj =(WorldWindowGLCanvas) obj;

//					String state = wwd.getView().getRestorableState();
//					wwObj.getView().restoreState(state);
					
		            FlatOrbitView sourceView = (FlatOrbitView)wwd.getView();
		            FlatOrbitView destView = (FlatOrbitView)wwObj.getView();
		            destView.setCenterPosition(sourceView.getCenterPosition());
		            destView.setZoom(sourceView.getZoom( ));
		            destView.setHeading(sourceView.getHeading());
		            destView.setPitch(sourceView.getPitch());

					
//					String state = wwd.getRestorableState();
//					wwObj.restoreState(state);

//					wwObj.copyViewState(wwd.getView());

//					wwObj.getView().setFieldOfView(wwd.getView().getFieldOfView());
//					wwObj.getView().setEyePosition(wwd.getView().getEyePosition());
//					wwObj.getView().setHeading(wwd.getView().getHeading());
//					wwObj.getView().setPitch(wwd.getView().getPitch());
//					wwObj.getView().setRoll(wwd.getView().getRoll());

					wwObj.redraw();
				}
	//			System.err.println("Data view property changed " + arg0.getPropertyName());
				
			}});

        // Add select listener for picking
	      wwd.addSelectListener(new SelectListener()
	      {
	    	  @Override
	          public void selected(SelectEvent event)
	          {
	              if (event.getEventAction().equals(SelectEvent.ROLLOVER))
	                  highlight(event.getTopObject());
	          }
	      });
	      wwd.addRenderingListener(new RenderingListener(){

			@Override
			public void stageChanged(RenderingEvent event) {
				// TODO Auto-generated method stub
				if (legendAnnotation!=null) {
//					int height=wwd.getHeight();
					int width = wwd.getWidth();
					BufferedImage image = (BufferedImage) legendAnnotation.getAttributes().getImageSource();
//					int imgHeight = image.getHeight();
					int imgWidth = image.getWidth();
				
					legendAnnotation.setScreenPoint(new Point(width-imgWidth-10, 60));
				}
			}
	    	  
	      });
		
//		wwd.addPositionListener(new PositionListener() {
//			
//			@Override
//			public void moved(PositionEvent arg0) {
//				// TODO Auto-generated method stub
//				
//				// TODO Auto-generated method stub
//				ArrayList <Object> changed = WWEvent.changedObjects(wwd);
//				for (Object obj:changed) {
//					ExtendedGliderWorldWindow wwObj =(ExtendedGliderWorldWindow) obj;
//					wwObj.getView().copyViewState(wwd.getView());
//				}
//				System.err.println("Data view position changed ");
//			}
//		});

//		wwd.addRenderingListener(new RenderingListener() {
//			
//			@Override
//			public void stageChanged(RenderingEvent arg0) {
//				// TODO Auto-generated method stub
//				ArrayList <Object> changed = WWEvent.changedObjects(wwd);
//				for (Object obj:changed) {
//					ExtendedGliderWorldWindow wwObj =(ExtendedGliderWorldWindow) obj;
//					wwObj.getView().copyViewState(wwd.getView());
//				}
//				System.err.println("Data view rendering state changed ");
//				
//			}});
		
//		// TODO debug statements - remove later
//		System.err.println("Drawable Realized : " + this.wwd.isDrawableRealized());
//		System.err.println("GL Init Event fired : " + this.wwd.isGLInitEventFired());
		
        
		Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		// use previous wwd as parameter for new frame to share resources (opengl, etc).
//		WWFrame flatFrame = new WWFrame(roundFrame.wwPanel.wwd, flatModel, "Flat Globe", AVKey.RIGHT_OF_CENTER);

		StatusLayer statusLayer = new StatusLayer();
		statusLayer.setEventSource(this.wwd);
		model.getLayers().add(statusLayer);
		
//		this.layerManager = new LayerManagerLayer(this.wwd);
		this.layerManager = new GroundNetworkLayerManager(this.wwd);
		this.layerManager.setLayerDragEnabled(true);
		model.getLayers().add(this.layerManager);

		Layer boundaryLayer = model.getLayers().getLayerByName("Political Boundaries");
		if (boundaryLayer == null)
		{
			model.getLayers().add(new CountryBoundariesLayer());
		}
		else
		{
			System.out.println("Layer named 'Political Boundaries' present already and enabled = "
					+ boundaryLayer.isEnabled());
			// enable political boundaries if not enabled already
			if (!boundaryLayer.isEnabled())
			{
				boundaryLayer.setEnabled(true);
				System.out.println("Enabled the 'Political Boundaries' layer");
			}
		}
		
		// find and remove unneeded layers
		Layer removeLayer = model.getLayers().getLayerByName("Stars");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("Atmosphere");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USDA NAIP");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USDA NAIP USGS");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("MS Virtual Earth Aerial");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("Bing Imagery");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USGS Topographic Maps 1:250K");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USGS Topographic Maps 1:100K");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USGS Topographic Maps 1:24K");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("USGS Urban Area Ortho");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("World Map");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
		removeLayer = model.getLayers().getLayerByName("Compass");
		if (removeLayer!=null) model.getLayers().remove(removeLayer);
	
		this.layerManager.setMinimized(true); // start with layer manager minimized		
		this.wwd.setModel(model);
		//System.out.println("EarthView.createPartControl 1");
		JPanel panel = new JPanel(new BorderLayout());
		//System.out.println("EarthView.createPartControl 2");
		this.awtFrame.add(panel);
		//System.out.println("EarthView.createPartControl 3");
		panel.add(this.wwd, BorderLayout.CENTER);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		//System.out.println("EarthView.createPartControl 4");

		crosshairLayer = new CrosshairLayer();
		ApplicationTemplate.insertBeforeLayerName(getWwd(), crosshairLayer, "Political Boundaries");

		
//		ViewControlsLayer viewControl = new ViewControlsLayer();
//		viewControl.setShowPanControls(true);
//		viewControl.setPosition(AVKey.NORTHEAST);
//		viewControl.setEnabled(true);
//		model.getLayers().add(viewControl);
//		ViewControlsSelectListener viewControlListener = new ViewControlsSelectListener(wwd, viewControl);

		//		WorldMapLayer worldmap = (WorldMapLayer) model.getLayers().getLayerByName("layers.Earth.WorldMapLayer.Name");
//		worldmap.setEnabled(false);
//        dataFilter.setStartTime(2011, 8, 4, 0, 0, 0, 0);
//        dataFilter.setEndTime(2011, 8, 4, 0, 5, 0, 0);

		
//		dataFilter.setCurrentTime(2011, 8, 4, 0, 5, 0, 0);       
//        dataFilter.setDisplayInterval(0, 1, 0);       
//        dataFilter.setBoundingBox(-92.0, -82.5, 32.0, 38.5);
        
        dataFilter.registerObject(this); // register this object with filter update interface
        
//        dataFilter.refreshObjects(); // this will cause the interface refresh to be called
        
//		addLightningLayers();
	}
	
	
	// this stuff is from BasicView, copyViewState in BasicView needs to be  modified/extended to include nearClipDistance and farClipDistance
	// unfortunately, both of those variables are protected in BasicView and cannot be modified
	// using RestorableState methods works, but is very slow compared to copyViewState
	
//	private void copyViewState(View destination, View source) 
//	{
//		
//	}
//	
//    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
//    {
//        this.getViewPropertyLimits().getRestorableState(rs, rs.addStateObject(context, "viewPropertyLimits"));
//
//        rs.addStateValueAsBoolean(context, "detectCollisions", this.isDetectCollisions());
//
//        if (this.getFieldOfView() != null)
//            rs.addStateValueAsDouble(context, "fieldOfView", this.getFieldOfView().getDegrees());
//
//        rs.addStateValueAsDouble(context, "nearClipDistance", this.getNearClipDistance());
//        rs.addStateValueAsDouble(context, "farClipDistance", this.getFarClipDistance());
//
//        if (this.getEyePosition() != null)
//            rs.addStateValueAsPosition(context, "eyePosition", this.getEyePosition());
//
//        if (this.getHeading() != null)
//            rs.addStateValueAsDouble(context, "heading", this.getHeading().getDegrees());
//
//        if (this.getPitch() != null)
//            rs.addStateValueAsDouble(context, "pitch", this.getPitch().getDegrees());
//    }
//
//    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
//    {
//        // Restore the property limits and collision detection flags before restoring the view's position and
//        // orientation. This has the effect of ensuring that the view's position and orientation are consistent with the
//        // current property limits and the current surface collision state.
//
//        RestorableSupport.StateObject so = rs.getStateObject(context, "viewPropertyLimits");
//        if (so != null)
//            this.getViewPropertyLimits().restoreState(rs, so);
//
//        Boolean b = rs.getStateValueAsBoolean(context, "detectCollisions");
//        if (b != null)
//            this.setDetectCollisions(b);
//
//        Double d = rs.getStateValueAsDouble(context, "fieldOfView");
//        if (d != null)
//            this.setFieldOfView(Angle.fromDegrees(d));
//
//        d = rs.getStateValueAsDouble(context, "nearClipDistance");
//        if (d != null)
//            this.setNearClipDistance(d);
//
//        d = rs.getStateValueAsDouble(context, "farClipDistance");
//        if (d != null)
//            this.setFarClipDistance(d);
//
//        Position p = rs.getStateValueAsPosition(context, "eyePosition");
//        if (p != null)
//            this.setEyePosition(p);
//
//        d = rs.getStateValueAsDouble(context, "heading");
//        if (d != null)
//            this.setHeading(Angle.fromDegrees(d));
//
//        d = rs.getStateValueAsDouble(context, "pitch");
//        if (d != null)
//            this.setPitch(Angle.fromDegrees(d));
//    }

	public void setFocus() {
	}
	
//	public void addImage(GliderImage image) throws IOException
//	{
//		//TODO debug statements - remove later
//		System.err.println("Drawable Realized : " + this.wwd.isDrawableRealized());
//		System.err.println("GL Init Event fired : " + this.wwd.isGLInitEventFired());
//		while (!this.wwd.isGLInitEventFired())
//		{
//			try 
//			{
//				Thread.sleep(1000);
//				System.err.println("GL Init Event fired (after waiting 1 sec) : " 
//						+ this.wwd.isGLInitEventFired());
//			}
//			catch (InterruptedException e) 
//			{
//				e.printStackTrace();
//			}
//		}
//
//		this.wwd.addImage(image);
//		//TODO come up with an acceptable alternative
//		// tried releasing image source to free the heap, after image is added to earth view
//		// but when earth view window is closed, opened again and the image is added again
//		// the gliderimage now has no source - exception occurs
//		// fixed this by creating the glider image again when user clicks on earth view option
//		// but that takes about 10 - 15 seconds
//		// so temporarily disabled this resource release
//		//image.releaseImageSource();
//		this.layerManager.update();
//	}
	
	/**
	 * This method allows the caller to add a shapeLayer to the 3D view.
	 * 
	 * @param shapeLayer - reference to a valid shape layer to be added to the 3D view
	 */
	public void addLayer(RenderableLayer shapeLayer)
	{
		try
		{
			ApplicationTemplate.insertBeforeLayerName(getWwd(), shapeLayer, "Political Boundaries");
			getWwd().redraw();
			this.layerManager.update();
		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.WARNING,"An error occurred while adding the provided shape layer to the 3D globe");
		}
	}
	
	/**
	 * This method allows the caller to remove a shapeLayer from the 3D view
	 * 
	 * @param shapeLayer - reference to the currently displayed layer to be removed
	 */
	public void removeLayer(RenderableLayer shapeLayer)
	{
		try
		{
			getWwd().getModel().getLayers().remove(shapeLayer);
		}
		catch(Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.WARNING,"An error occurred while removing the provided shape layer from the 3D globe");
		}
	}
	
//	public void removeImage(GliderImage image)
//	{
//		this.wwd.removeImage(image);
//		this.layerManager.update();
//	}
//    public Set<GliderImage> getImages()
//    {
//        return this.wwd.getImages();
//    }
//
//    public boolean containsImage(GliderImage image)
//    {
//        return this.wwd.containsImage(image);
//    }
//    
//    public void moveToImage(GliderImage image)
//    {
//    	this.moveToSector(image.getSector(), null);
//    }

    public void moveToSector(Sector sector, Double altitude)
    {
        OrbitView view = (OrbitView) this.wwd.getView();
        Globe globe = this.wwd.getModel().getGlobe();

        if (altitude == null || altitude == 0)
        {
        	double t = sector.getDeltaLatRadians();
        	double w = 0.75 * t * 6378137.0;
        	altitude = w / this.wwd.getView().getFieldOfView().tanHalfAngle();
        }

        if (globe != null && view != null)
        {
        	((BasicOrbitView) view).addPanToAnimator(
                    new Position(sector.getCentroid(), 0),
                    Angle.ZERO, Angle.ZERO, altitude);
        }
    }
    
    public WorldWindow getWwd()
    {
    	return this.wwd;
    }

    public LayerManagerLayer getLayerManager()
    {
    	return this.layerManager;
    }
    
    // need to pass in lightning data as a common data structure or json object
    private void refreshLighningLayers() 
    {
        
        // create or redraw lightning layers
    	// ENTLN Flash Data 
        if (entlnFlashLayer==null) {
        	entlnFlashLayer = new GroundNetworkLayer(conf.getEntlnFlashLayer(), "ENTLN Flash", entlnColor, this.tooltipAnnotation);
	        this.addLayer(entlnFlashLayer);
	        flashLayers.add(entlnFlashLayer);
        }
        if (nldnFlashLayer==null) {
        	nldnFlashLayer = new GroundNetworkLayer(conf.getNldnFlashLayer(), "NLDN Flash", nldnColor, this.tooltipAnnotation);
//        	nldnFlashLayer = new GroundNetworkLayer(conf.getNldnFlashLayer(), "NLDN Flash", new Color(75, 75, 255, 0), this.tooltipAnnotation);
	        this.addLayer(nldnFlashLayer);
	        flashLayers.add(nldnFlashLayer);
        }
        if (gld360Layer==null) {
        	gld360Layer = new GroundNetworkLayer(conf.getGld360Layer(), "GLD360", gld360Color, this.tooltipAnnotation);
	        this.addLayer(gld360Layer);
	        flashLayers.add(gld360Layer);
        }
        if (glmLayer==null) {
        	glmLayer = new GroundNetworkLayer(conf.getGlmFlashLayer(), "GLM Flash", glmColor, this.tooltipAnnotation);
	        this.addLayer(glmLayer);
	        flashLayers.add(glmLayer);
       }
       if (flashLegendLayer==null) {
   			flashLegendLayer = createFlashLayerLegend();
   			this.addLayer(flashLegendLayer);
       }
        try {
//        	entlnFlashLayer.clearPoints();
//            entlnFlashLayer.readCsvData(conf.getEntlnFlashTable());
//            entlnFlashLayer.displayPoints();
        	if (entlnFlashBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		entlnFlashLayer.readCsvData();
        		entlnFlashBuffer.put(dataFilter.getCurrentTimeMilli(), entlnFlashLayer.getPoints());
        	}
        	else {
        		entlnFlashLayer.setPoints(entlnFlashBuffer.get(dataFilter.getCurrentTimeMilli()));
        	}
        	if (nldnFlashBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		nldnFlashLayer.readCsvData();
        		nldnFlashBuffer.put(dataFilter.getCurrentTimeMilli(), nldnFlashLayer.getPoints());
        	}
        	else {
        		nldnFlashLayer.setPoints(nldnFlashBuffer.get(dataFilter.getCurrentTimeMilli()));
        	}
        	if (gld360Buffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		gld360Layer.readCsvData();
        		gld360Buffer.put(dataFilter.getCurrentTimeMilli(), gld360Layer.getPoints());
        	}
        	else {
        		gld360Layer.setPoints(gld360Buffer.get(dataFilter.getCurrentTimeMilli()));
        	}
        	if (glmBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		glmLayer.readCsvData();
        		glmBuffer.put(dataFilter.getCurrentTimeMilli(), glmLayer.getPoints());
        	}
        	else {
        		glmLayer.setPoints(glmBuffer.get(dataFilter.getCurrentTimeMilli()));
        	}
//        	nldnFlashLayer.readCsvData();
//        	gld360Layer.readCsvData();
//        	glmLayer.readCsvData();
        	
        	entlnFlashLayer.displayPoints();
        	nldnFlashLayer.displayPoints();
        	gld360Layer.displayPoints();
        	glmLayer.displayPoints();
        }
        catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("error loading layer " + e.getMessage());
		}
        
 		// create or redraw bounding box
		if (boundingBoxLayer==null) {
			boundingBoxLayer = new RenderableLayer();
			boundingBoxLayer.setName("Bounding Box");
		    this.addLayer(boundingBoxLayer);
		}
		else {
			boundingBoxLayer.removeAllRenderables();
		}
		
	    // Create and set an attribute bundle.
	    ShapeAttributes normalAttributes = new BasicShapeAttributes();
	    normalAttributes.setInteriorMaterial(Material.YELLOW);
	    normalAttributes.setOutlineOpacity(0.5);
	    normalAttributes.setInteriorOpacity(0.8);
	    normalAttributes.setOutlineMaterial(Material.GREEN);
	    normalAttributes.setOutlineWidth(2);
	    normalAttributes.setDrawOutline(true);
	    normalAttributes.setDrawInterior(false);
	    normalAttributes.setEnableLighting(true);
	
	    ShapeAttributes highlightAttributes = new BasicShapeAttributes(normalAttributes);
	    highlightAttributes.setOutlineMaterial(Material.WHITE);
	    highlightAttributes.setOutlineOpacity(1);
	    
	    // Create a polygon, set some of its properties and set its attributes.
	    ArrayList<Position> pathPositions = new ArrayList<Position>();
	    pathPositions.add(Position.fromDegrees(DataFilter.getMinLat(), DataFilter.getMinLon(), 10000));
	    pathPositions.add(Position.fromDegrees(DataFilter.getMinLat(), DataFilter.getMaxLon(), 10000));
	    pathPositions.add(Position.fromDegrees(DataFilter.getMaxLat(), DataFilter.getMaxLon(), 10000));
	    pathPositions.add(Position.fromDegrees(DataFilter.getMaxLat(), DataFilter.getMinLon(), 10000));
	    Polygon pgon = new Polygon(pathPositions);
	    pgon.setValue(AVKey.DISPLAY_NAME, "Bounding Box");
	
	    pgon.setAltitudeMode(WorldWind.ABSOLUTE);
	    pgon.setAttributes(normalAttributes);
	    pgon.setHighlightAttributes(highlightAttributes);
	     
	    boundingBoxLayer.addRenderable(pgon);
		      
    }
     
    private void highlight(Object o)
    {
       
        if (this.placeMark == o)
            return; // same thing selected

        if (this.placeMark != null)
        {
 //           this.placeMark.getAttributes().setHighlighted(false);
            this.placeMark.getAttributes().setHighlighted(false);
            this.placeMark = null;
            this.tooltipAnnotation.getAttributes().setVisible(false);
        }
       if (o != null && o instanceof AnnotationPointPlacemark)
        {
        	placeMark = (AnnotationPointPlacemark) o;
 //           this.placeMark.getAttributes().setHighlighted(true);
        	placeMark.getAttributes().setHighlighted(true);
            this.tooltipAnnotation.setText(this.composeAnnotationText(placeMark));
            this.tooltipAnnotation.setPosition(placeMark.getPosition());
            this.tooltipAnnotation.getAttributes().setVisible(true);
            this.getWwd().redraw();
        }

    }
    
    private String composeAnnotationText(AnnotationPointPlacemark annotation)
    {
//        p.setValue("Date", entry.getDate());
//        p.setValue("Time", entry.getTime());
//        p.setValue("Lat", entry.getLat());
//        p.setValue("Lon", entry.getLon());
//        p.setValue("Height", entry.getHeight());
//        p.setValue("Peak Current", entry.getPeakCurrent());
//        p.setValue("Type", entry.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        sb.append("<b>");
        sb.append("Layer: </b>");
        sb.append(annotation.getValue("LayerName") + "<br></br>");
        sb.append("<b>");
        sb.append("Date/Time: </b>");
        sb.append(annotation.getValue("Date") + "<br></br>");
        sb.append("<b>");
//        sb.append("Time: </b>");
//        sb.append(annotation.getValue("Time") + "<br></br>");
//        sb.append("<b>");
        sb.append("Measured Value: </b>");
//        sb.append(annotation.getValue("Current")  + " kA"+ "<br></br>");
        sb.append(annotation.getValue("Value")  + "<br></br>");
        sb.append("</html>");

        return sb.toString();
    }

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		refreshLighningLayers();
	}
	// this original version used GeoJson as input

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		entlnFlashBuffer.clear();
		nldnFlashBuffer.clear();
		gld360Buffer.clear();
		glmBuffer.clear();

	}
    protected RenderableLayer createFlashLayerLegend()
    {
    	RenderableLayer layer = new RenderableLayer();
    	layer.setName("Legend");
    	
        BufferedImage image = new BufferedImage(100, 80, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g2 = image.getGraphics();
        int divisions = flashLayers.size();
        int margin = 2; // space between items in pixels
//        int w = image.getWidth() / 2 - margin;
        int h = (image.getHeight() - margin * (divisions - 1)) / divisions;
        int w = h;
        int cnt=0;
        for (GroundNetworkLayer grnd:flashLayers)
        {
            int x = 0;
            int y = cnt * (image.getHeight() / divisions);
            // Draw color rectangle
            g2.setColor(grnd.getColor());
            g2.fillRect(x, y, w, h);
            // Draw hour label
            x = w + margin + margin;
            y = y + h;
            String label =  grnd.getName();
            g2.setColor(Color.BLACK);
            g2.drawString(label, x + 1, y + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(label, x, y);
            cnt++;
        }
        
        legendAnnotation = new ScreenAnnotation("", new Point((wwd.getWidth()-image.getWidth()-10), 60));
        legendAnnotation.getAttributes().setImageSource(image);
        legendAnnotation.getAttributes().setSize(
            new Dimension(image.getWidth(), image.getHeight()));
        legendAnnotation.getAttributes().setDrawOffset(new Point(image.getWidth() / 2, 0));
        legendAnnotation.getAttributes().setAdjustWidthToText(AVKey.SIZE_FIXED);
        legendAnnotation.getAttributes().setBorderWidth(0);
        legendAnnotation.getAttributes().setCornerRadius(0);
        legendAnnotation.getAttributes().setBackgroundColor(new Color(0f, 0f, 0f, 0f));
        layer.addRenderable(legendAnnotation);
        
        return layer;
    }

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
 	
    // need to pass in lightning data as a common data structure or json object
//    private void addLightningLayers() 
//    {
//        
//        GroundNetworkGeojsonLoader json;
//
//    	// ENTLN Flash Data 
//        if (entlnFlashLayer!=null) {
//        	this.removeLayer(entlnFlashLayer);
//        }
//        try {
//            json = new GroundNetworkGeojsonLoader(Color.CYAN);
//            entlnFlashLayer = new RenderableLayer();
//            entlnFlashLayer.setName("ENTLN Flash");
//            entlnFlashLayer.addRenderable(this.tooltipAnnotation);
//            System.out.println(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getEntlnFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString());
//			json.addSourceGeometryToLayer(new URI(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getEntlnFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString()), entlnFlashLayer);
////			json.addSourceGeometryToLayer(new URI(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getEntlnFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getCqlString()), layer);
//	        this.addLayer(entlnFlashLayer);
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer " + e.getReason());
//		}
//        catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer " + e.getMessage());
//		}
//    	// NLDN Flash Data 
//        if (nldnFlashLayer!=null) {
//        	this.removeLayer(nldnFlashLayer);
//        }
//        try {
//        	json = new GroundNetworkGeojsonLoader(Color.BLUE);
//        	nldnFlashLayer = new RenderableLayer();
//        	nldnFlashLayer.setName("NLDN Flash");
//        	nldnFlashLayer.addRenderable(this.tooltipAnnotation);
//            System.out.println(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getNldnFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString());
//			json.addSourceGeometryToLayer(new URI(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getNldnFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString()), nldnFlashLayer);
//        	this.addLayer(nldnFlashLayer);
//        } catch (URISyntaxException e) {
//        	// TODO Auto-generated catch block
//        	System.err.println("error loading layer " + e.getReason());
//        }
//        catch (Exception e) {
//        	// TODO Auto-generated catch block
//        	System.err.println("error loading layer " + e.getMessage());
//        }
//	        
//
//	    // GLD360 Data 
//        if (gld360Layer!=null) {
//        	this.removeLayer(gld360Layer);
//        }
//	    try {
//	        json = new GroundNetworkGeojsonLoader(Color.PINK);
//	        gld360Layer = new RenderableLayer();
//	        gld360Layer.setName("GLD360");
//	        gld360Layer.addRenderable(this.tooltipAnnotation);
//            System.out.println(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getGld360Table() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString());
//			json.addSourceGeometryToLayer(new URI(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getGld360Table() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString()), gld360Layer);
//	        this.addLayer(gld360Layer);
//	    } catch (URISyntaxException e) {
//	    	// TODO Auto-generated catch block
//	    	System.err.println("error loading layer " + e.getReason());
//	    }
//	    catch (Exception e) {
//	    	// TODO Auto-generated catch block
//	    	System.err.println("error loading layer " + e.getMessage());
//	    }
//
//			// GLM Flash Data 
//        if (glmLayer!=null) {
//        	this.removeLayer(glmLayer);
//        }
//		try {
//		    json = new GroundNetworkGeojsonLoader(Color.MAGENTA);
//		    glmLayer = new RenderableLayer();
//		    glmLayer.setName("GLM Flash");
//		    glmLayer.addRenderable(this.tooltipAnnotation);
//            System.out.println(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getGlmFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString());
//			json.addSourceGeometryToLayer(new URI(conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getGlmFlashTable() + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString()), glmLayer);
//		    this.addLayer(glmLayer);
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer " + e.getReason());
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer " + e.getMessage());
//		}
//		
//		if (boundingBoxLayer!=null) {
//			this.removeLayer(boundingBoxLayer);
//		}
//		boundingBoxLayer = new RenderableLayer();
//		boundingBoxLayer.setName("Bounding Box");
//
//        // Create and set an attribute bundle.
//        ShapeAttributes normalAttributes = new BasicShapeAttributes();
//        normalAttributes.setInteriorMaterial(Material.YELLOW);
//        normalAttributes.setOutlineOpacity(0.5);
//        normalAttributes.setInteriorOpacity(0.8);
//        normalAttributes.setOutlineMaterial(Material.GREEN);
//        normalAttributes.setOutlineWidth(2);
//        normalAttributes.setDrawOutline(true);
//        normalAttributes.setDrawInterior(false);
//        normalAttributes.setEnableLighting(true);
//
//        ShapeAttributes highlightAttributes = new BasicShapeAttributes(normalAttributes);
//        highlightAttributes.setOutlineMaterial(Material.WHITE);
//        highlightAttributes.setOutlineOpacity(1);
//        
//        // Create a polygon, set some of its properties and set its attributes.
//        ArrayList<Position> pathPositions = new ArrayList<Position>();
//        pathPositions.add(Position.fromDegrees(dataFilter.getMinLat(), dataFilter.getMinLon(), 10000));
//        pathPositions.add(Position.fromDegrees(dataFilter.getMinLat(), dataFilter.getMaxLon(), 10000));
//        pathPositions.add(Position.fromDegrees(dataFilter.getMaxLat(), dataFilter.getMaxLon(), 10000));
//        pathPositions.add(Position.fromDegrees(dataFilter.getMaxLat(), dataFilter.getMinLon(), 10000));
//        Polygon pgon = new Polygon(pathPositions);
//        pgon.setValue(AVKey.DISPLAY_NAME, "Bounding Box");
//
//        pgon.setAltitudeMode(WorldWind.ABSOLUTE);
//        pgon.setAttributes(normalAttributes);
//        pgon.setHighlightAttributes(highlightAttributes);
//         
//        boundingBoxLayer.addRenderable(pgon);
//	    this.addLayer(boundingBoxLayer);
//		      
//    }

}