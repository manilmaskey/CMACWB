package edu.uah.itsc.worldwind.eclipse;

import edu.uah.itsc.worldwind.eclipse.glider.GliderImage;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.LayerManagerLayer;
import gov.nasa.worldwindx.examples.util.StatusLayer;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.Earth.CountryBoundariesLayer;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitView;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class EarthView extends ViewPart {
	public static final String ID = "gov.nasa.worldwind.eclipse.views.EarthView";
    public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0; // ellipsoid equatorial getRadius, in meters
    
	private Composite swtAwtContainer;
	private Frame awtFrame;
	private ExtendedGliderWorldWindow wwd;
	private LayerManagerLayer layerManager;

	public EarthView() {
	}

	public void createPartControl(final Composite parent) {
		this.swtAwtContainer = new Composite(parent, SWT.EMBEDDED);
		this.swtAwtContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.awtFrame = SWT_AWT.new_Frame(this.swtAwtContainer);
		this.wwd = new ExtendedGliderWorldWindow();
		
		// TODO debug statements - remove later
		System.err.println("Drawable Realized : " + this.wwd.isDrawableRealized());
		System.err.println("GL Init Event fired : " + this.wwd.isGLInitEventFired());
		
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
		this.wwd.setModel(model);
		//System.out.println("EarthView.createPartControl 1");
		JPanel panel = new JPanel(new BorderLayout());
		//System.out.println("EarthView.createPartControl 2");
		this.awtFrame.add(panel);
		//System.out.println("EarthView.createPartControl 3");
		panel.add(this.wwd, BorderLayout.CENTER);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		//System.out.println("EarthView.createPartControl 4");
	}

	public void setFocus() {
	}
	
	public void addImage(GliderImage image) throws IOException
	{
		//TODO debug statements - remove later
		System.err.println("Drawable Realized : " + this.wwd.isDrawableRealized());
		System.err.println("GL Init Event fired : " + this.wwd.isGLInitEventFired());
		while (!this.wwd.isGLInitEventFired())
		{
			try 
			{
				Thread.sleep(1000);
				System.err.println("GL Init Event fired (after waiting 1 sec) : " 
						+ this.wwd.isGLInitEventFired());
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}

		this.wwd.addImage(image);
		//TODO come up with an acceptable alternative
		// tried releasing image source to free the heap, after image is added to earth view
		// but when earth view window is closed, opened again and the image is added again
		// the gliderimage now has no source - exception occurs
		// fixed this by creating the glider image again when user clicks on earth view option
		// but that takes about 10 - 15 seconds
		// so temporarily disabled this resource release
		//image.releaseImageSource();
		this.layerManager.update();
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
	
	public void removeImage(GliderImage image)
	{
		this.wwd.removeImage(image);
		this.layerManager.update();
	}
    public Set<GliderImage> getImages()
    {
        return this.wwd.getImages();
    }

    public boolean containsImage(GliderImage image)
    {
        return this.wwd.containsImage(image);
    }
    
    public void moveToImage(GliderImage image)
    {
    	this.moveToSector(image.getSector(), null);
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
}
