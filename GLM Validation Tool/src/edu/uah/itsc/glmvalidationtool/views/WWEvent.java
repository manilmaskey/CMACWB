package edu.uah.itsc.glmvalidationtool.views;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.uah.itsc.glmvalidationtool.data.DataFilterUpdate;
//import edu.uah.itsc.worldwind.eclipse.ExtendedGliderWorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwindx.examples.util.SectorSelector;

public class WWEvent {

//	private static boolean drawMode = false;
	private static ArrayList <Object> WW_objects = new ArrayList<>();
	private static ArrayList <Object> Listeners = new ArrayList<>();
	private static ArrayList<SectorSelector> Selector = new ArrayList<>();
	private static Sector selectedSector = null;
	
	public static ArrayList<Object> getWWObjects() {
		return WW_objects;
	}
	static public void register(Object obj) 
	{
		System.out.println("World Wind Object registered");
		WW_objects.add(obj);
//		SectorSelector selector = new SectorSelector((ExtendedGliderWorldWindow)obj);
		SectorSelector selector = new SectorSelector((WorldWindowGLCanvas)obj);

        selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
        selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
        selector.setBorderWidth(3);
		Selector.add(selector);
        selector.addPropertyChangeListener(SectorSelector.SECTOR_PROPERTY, new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                Sector sector = (Sector) evt.getNewValue();
                if (null != sector)
                {
                    selectedSector = sector;
            		System.out.println("Sector Changed");
            		for (Object obj:Listeners) {
            			System.out.println("object " + obj.toString());
            			((WWEventListener) obj).sectorChanged(selectedSector);
            		}
//            		

                }
            }
        });

	}
	static public void registerListener(Object obj) 
	{
		System.out.println("Listener Object registered");
		Listeners.add(obj);
	}
	public void notifySectorChanged()
	{
		for (Object obj:Listeners) {
			System.out.println("object " + obj.toString());
			((WWEventListener) obj).sectorChanged(selectedSector);
		}
	}
	public void enableSelectors()
	{
		for (SectorSelector obj:Selector) {
			obj.enable();
		}
	}
	public void disableSelectors()
	{
		for (SectorSelector obj:Selector) {
			obj.disable();
		}		
	}
	static public ArrayList<Object> changedObjects(Object changed)
	{
		ArrayList<Object> objects = new ArrayList<>();
		if (changed==null) return null;
		// return all objects except for one changed
		for (Object obj:WW_objects) {
			if (!obj.equals(changed)) {
				objects.add(obj);
			}
		}
//		System.out.println("Objects updated");
		return objects;
	}
//	public static boolean isDrawMode() {
//		return drawMode;
//	}
//	public static void setDrawMode(boolean drawMode) {
//		WWEvent.drawMode = drawMode;
//		if (drawMode) {
//			for (Object obj:WW_objects) {
//				ExtendedGliderWorldWindow wwd = (ExtendedGliderWorldWindow)obj;
//				
//			}
//			
//		}
//	}
	static public void clear()
	{
//		System.out.println("Objects cleared");
		WW_objects.clear();
	}

}
