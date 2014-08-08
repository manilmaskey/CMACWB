package edu.uah.itsc.worldwind.eclipse;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.part.ViewPart;

public class WMSLayerManager extends ViewPart 
{
	public static final String ID = "gov.nasa.worldwind.eclipse.views.WMSLayerManager";

	private static final Map<String, String> wmsServers = new LinkedHashMap<String, String>();
	
	static
	{
		wmsServers.put("NASA Earth Observations (NEO) WMS", 
				"http://neowms.sci.gsfc.nasa.gov/wms/wms");
wmsServers.put("USFWS Wetlands WMS", 
"http://137.227.242.85/ArcGIS/services/FWS_Wetlands_WMS/mapserver/wmsserver");
wmsServers.put("USGS National Land Cover Database 2006 Land Cover","http://imsref.cr.usgs.gov/WMS_Capabilities/USGS_EDC_LandCover_NLCD2006/capabilities_1_3_0.xml");

wmsServers.put("NSIDC ","http://nsidc.org/cgi-bin/atlas_north?SERVICE=WMS&REQUEST=GetCapabilities");
	



/*wmsServers.put("NASA Earth Observations (NEO) WMS", 
			"http://neowms.sci.gsfc.nasa.gov/wms/wms?service=WMS&request=GetCapabilities");
		wmsServers.put("NASA Scientific Visualization Studio (SVS)", 
			"http://svs.gsfc.nasa.gov/cgi-bin/wms?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetCapabilities");
		wmsServers.put("NASA Jet Propulsion Lab WMS Server", 
			"http://wms.jpl.nasa.gov/wms.cgi?service=WMS&request=GetCapabilities");
		wmsServers.put("UMN FlightGear Landcover-DB",  
				"http://mapserver.flightgear.org/cgi-bin/landcover?service=WMS&request=GetCapabilities");
		wmsServers.put("Tsunami_Diasaster_Data", 
				"http://www.mapsherpa.com/cgi-bin/wms_iodra?SERVICE=wms&VERSION=1.1.1&REQUEST=getcapabilities");

		wmsServers.put("USFWS Wetlands WMS", 
		"http://137.227.242.85/ArcGIS/services/FWS_Wetlands_WMS/mapserver/wmsserver?ServiceName=FWS_Wetlands_WMS&Request=GetCapabilities&service=WMS");
		wmsServers.put("wind vector", 
		"http://webapps.datafed.net/ogc_views_GSN.ogc?SERVICE=wms&REQUEST=GetMap&VERSION=1.1.1&SRS=EPSG:4326&STYLES=&LAYERS=NCDC_AVG_WIND_VECTOR_map&BBOX=90.00,0.00,140.00,50.00&TIME=2005-08-01T00:00:00&FORMAT=image/png&EXCEPTIONS=application/vnd.ogc.se_inimage&TRANSPARENT=TRUE&BGCOLOR=0xFFFFFF&WIDTH=1000&HEIGHT=600");

		wmsServers.put("USFWS_WMS_AK_Wetlands", 
				"http://wetlandswms.er.usgs.gov/wmsconnector/com.esri.wms.Esrimap?ServiceName=USFWS_WMS_AK_Wetlands&Request=GetCapabilities&service=WMS");
		wmsServers.put("USFWS_WMS_CONUS_Wetlands", 
				"http://wetlandswms.er.usgs.gov/wmsconnector/com.esri.wms.Esrimap?ServiceName=USFWS_WMS_CONUS_Wetlands&Request=GetCapabilities&service=WMS");
		wmsServers.put("USGS_EDC_National_Atlas", 
				"http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?REQUEST=GetCapabilities&SERVICE=wms");
		wmsServers.put("USGS_WMS_BTS_Roads", 
				"http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?servicename=USGS_WMS_BTS_Roads&request=capabilities&SERVICE=wms");
		wmsServers.put("USGS_WMS_LANDSAT7", 
				"http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?WMTVER=1.1.0&ServiceName=USGS_WMS_LANDSAT7&REQUEST=capabilities&SERVICE=wms");
		wmsServers.put("USGS_WMS_NED", 
				"http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?WMTVER=1.1.0&REQUEST=GetCapabilities&ServiceName=USGS_WMS_NED&Service=WMS");
		wmsServers.put("USGS_WMS_REF", 
				"http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?WMTVER=1.1.0&REQUEST=GetCapabilities&ServiceName=USGS_WMS_REF&Service=WMS");
*/
	};
	
	private Composite swtAwtContainer;
	private Frame awtFrame;
    private final Dimension wmsPanelSize = new Dimension(400, 600);
    private JTabbedPane tabbedPane;
    private int previousTabIndex;

	@Override
	public void createPartControl(Composite parent) 
	{
		// use the SWT_AWT bridge to get an AWT frame, to place swing components
		this.swtAwtContainer = new Composite(parent, SWT.EMBEDDED);
		this.swtAwtContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.awtFrame = SWT_AWT.new_Frame(this.swtAwtContainer);
		
	    try {
	    	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    } catch(Exception e) {
	    	System.out.println("Error setting Java LAF: " + e);
	    }
	    this.tabbedPane = new JTabbedPane();

        this.tabbedPane.add(new JPanel());
        this.tabbedPane.setTitleAt(0, "enter new");
        this.tabbedPane.add(new JPanel());
        this.tabbedPane.setTitleAt(1, "choose existing");
        this.tabbedPane.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent changeEvent)
            {
                if (tabbedPane.getSelectedIndex() > 1)
                {
                    previousTabIndex = tabbedPane.getSelectedIndex();
                    return;
                }

                String serverName = null;
                String serverURL = null;
                switch(tabbedPane.getSelectedIndex())
                {
                case 0: serverURL = JOptionPane.showInputDialog("Enter wms server URL");;
                		break;
                case 1: serverName = (String)JOptionPane.showInputDialog(
	                        null,
	                        "Choose a WMS Server\n",
	                        "WMS Server URL",
	                        JOptionPane.PLAIN_MESSAGE,
	                        null,
	                        wmsServers.keySet().toArray(new String[0]),
	                        wmsServers.keySet().toArray(new String[0])[1]);
                		serverURL = wmsServers.get(serverName);
                		break;
                }
               	if (serverURL == null || serverURL.length() < 1)
                {
                    tabbedPane.setSelectedIndex(previousTabIndex);
                    return;
                }

                // Respond by adding a new WMSLayerPanel to the tabbed pane.
                if (addTab(tabbedPane.getTabCount(), serverURL.trim(), serverName) != null)
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });

        // create a tab for the first server alone - remaing will be opened by user if required
        // start with 2 to place all server tabs to the right of the new Server and choose server tab
        String name = wmsServers.keySet().toArray(new String[0])[0];
        this.addTab(2, wmsServers.get(name), name); 

        // Display the first server pane by default.
        this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() > 0 ? 2 : 0);
        this.previousTabIndex = this.tabbedPane.getSelectedIndex();

//        // Add the tabbed pane to a frame separate from the world window.
//        JFrame controlFrame = new JFrame();
//        controlFrame.getContentPane().add(tabbedPane);
//        controlFrame.pack();
//        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        controlFrame.setVisible(true);

        // In the worldwind example (commented section above), a separate frame was used for the tabbed pane 
        // containing the WMS server management UI. In GLIDER the swing components are embedded 
        // into the workbench page in a SWT ViewPart using the SWT_AWT bridge.
        
        // NOTE: DO NOT add the tabbed pane directly into the AWT frame created by the SWT_AWT bridge.
        // Refer the javadoc for SWT_AWT.new_Frame(Composite) method.
        // Since JDK1.5, it is recommended to add a heavy weight component like java.awt.Panel
        // as the root of all components inside the AWT frame. Otherwise it will not receive mouse events.

        Panel awtRootPanel = new Panel(new GridLayout(1, 1));
        awtRootPanel.add(this.tabbedPane);
		this.awtFrame.add(awtRootPanel);
	}

    private WMSLayersPanel addTab(int position, String serverURL, String serverName)
    {
        // Add a server to the tabbed dialog.
        try
        {
            WMSLayersPanel layersPanel = new WMSLayersPanel(getWwd(), serverURL, wmsPanelSize);
            this.tabbedPane.add(layersPanel, BorderLayout.CENTER);
            
            // add the server name as the title by default
            String title = layersPanel.getServerDisplayString();
            this.tabbedPane.setTitleAt(position, 
            		(serverName != null && serverName.length() > 0 ? 
            				serverName : (title != null && title.length() > 0 ? title : serverURL)));
            
            // add a listener to notice wms layer selections and update the Earth View layer panel
            layersPanel.addPropertyChangeListener("LayersPanelUpdated", new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent)
                {
                    getActiveEarthView().getLayerManager().update();
                }
            });
            
            return layersPanel;
        }
        catch (URISyntaxException e)
        {
            JOptionPane.showMessageDialog(null, "Server URL is invalid", "Invalid Server URL",
                JOptionPane.ERROR_MESSAGE);
            tabbedPane.setSelectedIndex(previousTabIndex);
            return null;
        }
    }
    
    private WorldWindowGLCanvas getWwd()
    {
		System.err.println("*********** inside WMSLayerManager.getWwd() **************");
		EarthView earthView = getActiveEarthView();
		if (earthView != null)
			return (WorldWindowGLCanvas) earthView.getWwd();
    	return null;
    }

    private EarthView getActiveEarthView()
    {
		IViewReference[] viewRefs = this.getSite()
				.getWorkbenchWindow().getActivePage().getViewReferences();
		
		System.err.println("*********** inside WMSLayerManager.getActiveEarthView() **************");
		for (IViewReference viewRef : viewRefs)
		{
			System.err.println("viewRef.getId() : " + viewRef.getId());
			if (viewRef != null
					&& EarthView.ID.equals(viewRef.getId()))
			{
				System.err.println("*** Got an earth view *** " + viewRef.getPartName());
				return ((EarthView) viewRef.getPart(false));
				
			} 
		}
    	return null;
    }

    @Override
	public void setFocus() 
	{
	}
}
