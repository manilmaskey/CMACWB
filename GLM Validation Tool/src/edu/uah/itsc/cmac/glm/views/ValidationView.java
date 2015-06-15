package edu.uah.itsc.cmac.glm.views;

import edu.uah.itsc.cmac.glm.config.Config;
import edu.uah.itsc.cmac.glm.data.DataFilter;
import edu.uah.itsc.cmac.glm.data.DataFilterUpdate;
//import edu.uah.itsc.worldwind.eclipse.ExtendedGliderWorldWindow;
//import edu.uah.itsc.worldwind.eclipse.glider.GliderImage;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.LayerManagerLayer;
import gov.nasa.worldwindx.examples.util.SectorSelector;
import gov.nasa.worldwindx.examples.util.StatusLayer;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.EarthFlat;
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
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitView;

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
import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ValidationView extends ViewPart implements DataFilterUpdate {
	public static final String ID = "ValidationView";
    public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0; // ellipsoid equatorial getRadius, in meters
    
	private Composite swtAwtContainer;
	private Frame awtFrame;
//	private ExtendedGliderWorldWindow wwd;
	private WorldWindowGLCanvas wwd;
	private LayerManagerLayer layerManager;
    private AnnotationPointPlacemark placeMark;
//    private Config conf = new Config();

//    private RenderableLayer glmValidationLayer=null;
    private GlmValidationLayer glmValidationLayer=null;
    private GlmValidationLayer groundValidationLayer=null;
//    private GlmValidationLayer entlnValidationLayer=null;
//    private GlmValidationLayer nldnValidationLayer=null;
//    private GlmValidationLayer gld360ValidationLayer=null;
    private RenderableLayer boundingBoxLayer=null;
	private CrosshairLayer crosshairLayer=null;

	private Map<Long, ArrayList<Renderable>> glmValidationBuffer = new HashMap<Long, ArrayList<Renderable>>();
	private Map<Long, ArrayList<Renderable>> groundValidationBuffer = new HashMap<Long, ArrayList<Renderable>>();
	private Map<Long, ArrayList<Integer>> stoplightCounts = new HashMap<Long, ArrayList<Integer>>();
//	private Map<Long, ArrayList<Renderable>> entlnValidationBuffer = new HashMap<>();
//	private Map<Long, ArrayList<Renderable>> nldnValidationBuffer = new HashMap<>();
//	private Map<Long, ArrayList<Renderable>> gld360ValidationBuffer = new HashMap<>();

	private GlobeAnnotation tooltipAnnotation;
//    private String intersection_string = "http://54.83.58.23:8080/geoserver/GLM/wms?service=WMS&version=1.1.0&request=GetMap&layers=GLM:event_proxy_etln_flash_intersection&styles=&bbox=-115.91474,-10.838665,-51.050323,56.14598&width=495&height=512&srs=EPSG:4326&format=application/json&cql_filter=lightningtime=%272011-08-03%2019:00:04%27";
//  private String etln_flash_string = "http://54.83.58.23:8080/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash&outputFormat=application/json&cql_filter=datetime between '2011-08-03 19:00:04.043.00102' and '2011-08-04 19:00:04'";
//  private String etln_flash_string = "http://54.83.58.23:8080/geoserver/GLM/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GLM:etln_flash&outputFormat=application/json";
    DataFilter dataFilter = new DataFilter();

	private RenderableLayer glmLegendLayer = null;
    private ScreenAnnotation legendAnnotation;

  
	public ValidationView() {
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
		Configuration.setValue(AVKey.INITIAL_LONGITUDE,dataFilter.getConfig().getInitialLongitude());

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

 //    	String filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\events_out_proxy-2011-08-04.txt";
//        glmEventData = new GlmEventData();
//        try {
//			glmEventData.ReadFile(filename);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//    	filename = "C:\\Users\\Todd\\Lightning Data\\GLM simulated\\flashes_out_proxy-2011-08-04.txt";
//        glmFlashData = new GlmFlashData();
//        try {
//			glmFlashData.ReadFile(filename);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
        // clear database for testing
        //new DatabaseCleaner().clear();
//        // ingest files into database for testing
//        try {
//			glmEventData.writeToDatabase();
//			glmFlashData.writeToDatabase();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
//		this.wwd = new ExtendedGliderWorldWindow();
        
        // if a wwd has already been created, get the first object and pass it to the constructor
        // to share resources
        
        // TODO: this seems to give an error when closing in eclipse, investigate further
//        if (WWEvent.getWWObjects().size()>0)
//        	this.wwd = new WorldWindowGLCanvas((WorldWindowGLCanvas)WWEvent.getWWObjects().get(0));
//        else 
        	this.wwd = new WorldWindowGLCanvas();
		
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
//				System.err.println("Validation view property changed " + arg0.getPropertyName());
				
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
//							int height=wwd.getHeight();
							int width = wwd.getWidth();
							BufferedImage image = (BufferedImage) legendAnnotation.getAttributes().getImageSource();
//							int imgHeight = image.getHeight();
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
//				System.err.println("Validation view position changed ");
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
//				System.err.println("Validation view rendering state changed ");
//				
//			}});

		// TODO debug statements - remove later
//		System.err.println("Drawable Realized : " + this.wwd.isDrawableRealized());
//		System.err.println("GL Init Event fired : " + this.wwd.isGLInitEventFired());
		
		Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		
		StatusLayer statusLayer = new StatusLayer();
		statusLayer.setEventSource(this.wwd);
		model.getLayers().add(statusLayer);
		
		this.layerManager = new LayerManagerLayer(this.wwd);
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

		dataFilter.registerObject(this); // register this object with filter update interface
//        dataFilter.refreshObjects(); // this will cause the interface refresh to be called

//		refreshGlmLayers();
	}

	public void linkDataView() {
		System.out.println("linkDataView");
		wwd.addPositionListener(new PositionListener() {

			@Override
			public void moved(PositionEvent arg0) {
				// TODO Auto-generated method stub
				DataView dataView = (DataView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("DataView");
				dataView.getWwd().getView().copyViewState(wwd.getView());
//				Position pos = wwd.getCurrentPosition();
				System.err.println("position changed ");
			}});
		
	}
	public void setFocus() {
	}
	
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
    private void refreshGlmLayers() 
    {
                
        if (glmValidationLayer==null) {
        	glmValidationLayer = new GlmValidationLayer(dataFilter.getConfig().getGlmIntersectionLayer(), "GLM Detected", this.tooltipAnnotation);
	        this.addLayer(glmValidationLayer);
        }
        if (groundValidationLayer==null) {
        	groundValidationLayer = new GlmValidationLayer(dataFilter.getConfig().getGroundIntersectionLayer(), "GLM Missing", this.tooltipAnnotation);
	        this.addLayer(groundValidationLayer);
        }
//        if (entlnValidationLayer==null) {
//        	entlnValidationLayer = new GlmValidationLayer(dataFilter.getConfig().getEntlnFlashGlmIntersectionLayer(), "ENTLN Coincidence", this.tooltipAnnotation);
//	        this.addLayer(entlnValidationLayer);
//        }
//        if (nldnValidationLayer==null) {
//        	nldnValidationLayer = new GlmValidationLayer(dataFilter.getConfig().getNldnFlashGlmIntersectionLayer(), "NLDN Coincidence", this.tooltipAnnotation);
//	        this.addLayer(nldnValidationLayer);
//        }
//        if (gld360ValidationLayer==null) {
//        	gld360ValidationLayer = new GlmValidationLayer(dataFilter.getConfig().getGld360GlmIntersectionLayer(), "GLD360 Coincidence", this.tooltipAnnotation);
//	        this.addLayer(gld360ValidationLayer);
//        }
        try {
        	ArrayList<Integer> list=null;
        	if (stoplightCounts.get(dataFilter.getCurrentTimeMilli())==null) {
        		list = new ArrayList<Integer>();
        		stoplightCounts.put(dataFilter.getCurrentTimeMilli(),list);
        	}
        	
	    	// GLM intersection Data 
        	if (glmValidationBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		glmValidationLayer.readCsvDataGlm();
        		glmValidationBuffer.put(dataFilter.getCurrentTimeMilli(), glmValidationLayer.getPoints());
        	}
        	else {
           		glmValidationLayer.setPoints(glmValidationBuffer.get(dataFilter.getCurrentTimeMilli()));        		
        	}
	    	// Ground network missing GLM  
        	if (groundValidationBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
        		groundValidationLayer.readCsvDataGround();
        		groundValidationBuffer.put(dataFilter.getCurrentTimeMilli(), groundValidationLayer.getPoints());
        		list.add(GlmValidationLayer.getCount(0));
        		list.add(GlmValidationLayer.getCount(1));
        		list.add(GlmValidationLayer.getCount(2));
        		list.add(GlmValidationLayer.getCount(3));
        	}
        	else {
           		groundValidationLayer.setPoints(groundValidationBuffer.get(dataFilter.getCurrentTimeMilli()));        		
        	}
        	
//        	if (entlnValidationBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
//        		entlnValidationLayer.readCsvDataGround();
//        		entlnValidationBuffer.put(dataFilter.getCurrentTimeMilli(), entlnValidationLayer.getPoints());
//        	}
//        	else {
//           		entlnValidationLayer.setPoints(entlnValidationBuffer.get(dataFilter.getCurrentTimeMilli()));        		
//        	}
//        	if (nldnValidationBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
//        		nldnValidationLayer.readCsvDataGround();
//        		nldnValidationBuffer.put(dataFilter.getCurrentTimeMilli(), nldnValidationLayer.getPoints());
//        	}
//        	else {
//           		nldnValidationLayer.setPoints(nldnValidationBuffer.get(dataFilter.getCurrentTimeMilli()));        		
//        	}
//        	if (gld360ValidationBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
//        		gld360ValidationLayer.readCsvDataGround();
//        		gld360ValidationBuffer.put(dataFilter.getCurrentTimeMilli(), gld360ValidationLayer.getPoints());
//        	}
//        	else {
//           		gld360ValidationLayer.setPoints(gld360ValidationBuffer.get(dataFilter.getCurrentTimeMilli()));        		
//        	}
            if (glmLegendLayer==null) {
            	glmLegendLayer = createGlmLayerLegend();
        			this.addLayer(glmLegendLayer);
            }
        	glmValidationLayer.displayPoints();
        	groundValidationLayer.displayPoints();
//        	entlnValidationLayer.displayPoints();
//        	nldnValidationLayer.displayPoints();
//        	gld360ValidationLayer.displayPoints();
        }
        catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			System.err.println("error loading layer "+ e.getReason());
	//		e.printStackTrace();
		}
	    catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("error loading layer "+ e.getMessage());
	//		e.printStackTrace();
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
 	    
 	    updateLegendImage();
	    
    
    }
    private void highlight(Object o)
    {
        if (this.placeMark == o)
            return; // same thing selected

        if (this.placeMark != null)
        {
//            this.placeMark.getAttributes().setHighlighted(false);
            this.placeMark.getAttributes().setHighlighted(false);
            this.placeMark = null;
            this.tooltipAnnotation.getAttributes().setVisible(false);
        }
       if (o != null && o instanceof AnnotationPointPlacemark)
        {
        	placeMark = (AnnotationPointPlacemark) o;
//            this.placeMark.getAttributes().setHighlighted(true);
        	placeMark.getAttributes().setHighlighted(true);
            this.tooltipAnnotation.setText(this.composeAnnotationText(placeMark));
            this.tooltipAnnotation.setPosition(placeMark.getPosition());
            this.tooltipAnnotation.getAttributes().setVisible(true);
            this.getWwd().redraw();
        }

    }
   private String composeAnnotationText(AnnotationPointPlacemark annotation)
   {
//       p.setValue("Date", entry.getDate());
//       p.setValue("Time", entry.getTime());
//       p.setValue("Lat", entry.getLat());
//       p.setValue("Lon", entry.getLon());
//       p.setValue("Height", entry.getHeight());
//       p.setValue("Peak Current", entry.getPeakCurrent());
//       p.setValue("Type", entry.getType());
       StringBuilder sb = new StringBuilder();
       sb.append("<html>");

       sb.append("<b>");
       sb.append("Layer: </b>");
       sb.append(annotation.getValue("LayerName") + "<br></br>");
       sb.append("<b>");
       sb.append("Date/Time: </b>");
       sb.append(annotation.getValue("Date") + "<br></br>");
       sb.append("<b>");
       if (annotation.getValue("entlncount")!=null) {
	       sb.append("ENTLN count: </b>");
	       sb.append(annotation.getValue("entlncount")+ "<br></br>");
	       sb.append("<b>");
       }
       if (annotation.getValue("nldncount")!=null) {
	       sb.append("NLDN count: </b>");
	       sb.append(annotation.getValue("nldncount")+ "<br></br>");
	       sb.append("<b>");
       }
       if (annotation.getValue("gldcount")!=null) {
	       sb.append("GLD360 count: </b>");
	       sb.append(annotation.getValue("gldcount")+ "<br></br>");
	       sb.append("</html>");
       }
       if (annotation.getValue("Sensor")!=null) {
	       sb.append("Detected by: </b>");
	       sb.append(annotation.getValue("Sensor")+ "<br></br>");
	       sb.append("</html>");
       }
//       if (annotation.getValue("glm_missing")!=null) {
//	       sb.append("GLM Missing: </b>");
//	       sb.append("</html>");
//       }

       return sb.toString();
   }
   protected RenderableLayer createGlmLayerLegend()
   {
   	RenderableLayer layer = new RenderableLayer();
   	layer.setName("Legend");
   	
    BufferedImage image = renderLegendImage();
        
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
   BufferedImage renderLegendImage()
   {
	       BufferedImage image = new BufferedImage(180, 80, BufferedImage.TYPE_4BYTE_ABGR);
	       Graphics g2 = image.getGraphics();
	       int divisions = GlmValidationLayer.labels.length;
	       int margin = 2; // space between items in pixels
//	       int w = image.getWidth() / 2 - margin;
	       int h = (image.getHeight() - margin * (divisions - 1)) / divisions;
	       int w = h;
	       int cnt=0;
	       for (int ind1=0;ind1<divisions;ind1++)
	       {
	           int x = 0;
	           int y = cnt * (image.getHeight() / divisions);
	           // Draw color rectangle
	           g2.setColor(GlmValidationLayer.colors[ind1]);
	           g2.fillRect(x, y, w, h);
	           // Draw hour label
	           x = w + margin + margin;
	           y = y + h;
	           String label =  GlmValidationLayer.labels[ind1] + " (" +stoplightCounts.get(dataFilter.getCurrentTimeMilli()).get(ind1) + ")";
	           g2.setColor(Color.BLACK);
	           g2.drawString(label, x + 1, y + 1);
	           g2.setColor(Color.WHITE);
	           g2.drawString(label, x, y);
	           cnt++;
	       }
	       return image;
   }
   void updateLegendImage()
   {
   	 BufferedImage image = renderLegendImage();
   	 legendAnnotation.getAttributes().setImageSource(image);

   }

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		refreshGlmLayers();
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		glmValidationBuffer.clear();
		groundValidationBuffer.clear();
		stoplightCounts.clear();
//		entlnValidationBuffer.clear();
//		nldnValidationBuffer.clear();
//		gld360ValidationBuffer.clear();
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	// original geojson version 
	
//    // need to pass in lightning data as a common data structure or json object
//    private void addGlmLayers() 
//    {
//                
//        if (glmValidationLayer!=null) {
//        	this.removeLayer(glmValidationLayer);
//        }
//        try {
//	    	// GLM intersection Data 
//	        GlmValidationGeojsonLoader json = new GlmValidationGeojsonLoader();
//	        glmValidationLayer = new RenderableLayer();
//	        glmValidationLayer.setName("GLM Coincidence");
//	        glmValidationLayer.addRenderable(this.tooltipAnnotation);
//	        System.out.println(dataFilter.getConfig().getProtocolHttp() + dataFilter.getConfig().getServerIP() + ":" + dataFilter.getConfig().getServerPort() + dataFilter.getConfig().getServiceString() + dataFilter.getConfig().getEntlnFlashGlmIntersectionLayer() + "&" + dataFilter.getValidationParamString());
//			json.addSourceGeometryToLayer(new URI(dataFilter.getConfig().getProtocolHttp() + dataFilter.getConfig().getServerIP() + ":" + dataFilter.getConfig().getServerPort() + dataFilter.getConfig().getServiceString() + dataFilter.getConfig().getEntlnFlashGlmIntersectionLayer() + "&" + dataFilter.getValidationParamString()), glmValidationLayer);
//	        this.addLayer(glmValidationLayer);
//        }
//        catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer "+ e.getReason());
//	//		e.printStackTrace();
//		}
//	    catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.err.println("error loading layer "+ e.getMessage());
//	//		e.printStackTrace();
//		}
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
//    
//    }
 
}