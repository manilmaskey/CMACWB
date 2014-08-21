package edu.uah.itsc.glmvalidationtool.views;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.time.SimpleTimePeriod;

import edu.uah.itsc.glmvalidationtool.config.Config;
import edu.uah.itsc.glmvalidationtool.data.DataFilter;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;

public class GlmValidationLayer extends RenderableLayer{

	
	Color color = Color.YELLOW;
	// this allows us to set up a shared attribute between all points of layer
	AnnotationAttributes pointAttrs = new AnnotationAttributes();
	ArrayList <Renderable> points = new ArrayList<>();
	Config conf = new Config();
	DataFilter dataFilter = new DataFilter();
	String tableName;
	String layerName=null;
	Renderable tooltip;
    //**************************************************************//
    //********************  Primitive Geometry Construction  *******//
    //**************************************************************//

	public GlmValidationLayer(String tableName, String layername, Renderable tooltip)
	{
		layerName = layername;
		this.tableName = tableName;
		this.tooltip=tooltip;
		setName(layername);
		addRenderable(tooltip);
	}
	
	public ArrayList<Renderable> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Renderable> points) {
		this.points = points;
    	removeAllRenderables();
    	addRenderable(tooltip);
	}
    protected Renderable createPoint(Position pos, String dateTime, long entlncount, long nldncount, long gldcount, long glmcount)
    {
        AnnotationPointPlacemark p = new AnnotationPointPlacemark(pos, pointAttrs);
        p.setAttributes(pointAttrs);
        
        if (pos.getAltitude() != 0)
        {
            p.setAltitudeMode(WorldWind.ABSOLUTE);
         }
        else
        {
            p.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        }
//        p.setValue("LayerName", layerName);
        p.setValue("LayerName", layerName);
        p.setValue("Date", dateTime);
        if (entlncount>=0)
        	p.setValue("entlncount", entlncount);
        if (nldncount>=0)
        	p.setValue("nldncount", nldncount);
        if (gldcount>=0)
        	p.setValue("gldcount", gldcount);       
        if (glmcount>=0)
        	p.setValue("glmcount", glmcount);       
        p.getAttributes().setOpacity(1.0);
        p.getAttributes().setScale(1);    
        int instrumentCnt = (entlncount>0?1:0) + (nldncount>0?1:0) + (gldcount>0?1:0) + (glmcount>0?1:0);

//      System.out.println("instrumentCnt " + instrumentCnt);
        if (instrumentCnt==0) {
        	p.setValue("DisplayColor", Color.RED);
//          p.getAttributes().setTextColor(Color.RED);
        }
        else if(instrumentCnt==1) {
        	p.setValue("DisplayColor", Color.YELLOW);
//          p.getAttributes().setTextColor(Color.YELLOW);  	
        }
        else {
        	p.setValue("DisplayColor", Color.GREEN);
//          p.getAttributes().setTextColor(Color.GREEN);  	
        }

        return p;
    }

    public void readCsvDataGlm() throws URISyntaxException, NumberFormatException, IOException 
    {
    	
       	clearPoints();
       	
    	String httpString = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceStringCsv() + tableName + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString();
        System.out.println(httpString);
        
        URL url = new URL(httpString);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        int index=0;
        boolean firstTime=true;
        double lat, lon;
        long entln, nldn, gld360;
        while ((inputLine = in.readLine()) != null) {
//            System.out.println(inputLine);
            if (firstTime) { // skip header line then parse out the counts
            	firstTime=false;
            	continue; 
            }
            String [] fields = inputLine.split(",");
            String latlonStr=fields[2].trim();
            String time = fields[3].trim();
            
            // parse point string
            //POINT (-88.99 33.689)
            
            String [] tempArr = latlonStr.split("\\(");
            String tempStr = tempArr[1];
//            System.out.println(tempStr);
            tempArr = tempStr.split("\\)");
            tempStr = tempArr[0];
//            System.out.println(tempStr);
            String [] latlon = tempStr.split(" ");
            lon = Double.parseDouble(latlon[0]);
            lat = Double.parseDouble(latlon[1]);
//            System.out.println("lat " + lat +  " lon " + lon );
            entln = Long.parseLong(fields[4]);
            nldn = Long.parseLong(fields[5]);
            gld360 = Long.parseLong(fields[6]);
            Position pos = new Position(LatLon.fromDegrees(lat, lon),  0);
             
            
            points.add(this.createPoint(pos,time, entln, nldn, gld360, -1));
  //          addRenderable(this.createPoint(pos,ts.toString(), Double.toString(value)));
            
//            nldn_flash_view.fid-286a9be5_147ea205445_166f	1008335	2011-08-04 23:56:00:005 	5.69999981	POINT (-88.99 33.689)
        }
        in.close();			 
		
    	
    }
    public void readCsvDataGround() throws URISyntaxException, NumberFormatException, IOException 
    {
    	
       	clearPoints();
       	
    	String httpString = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceStringCsv() + tableName + "&" + dataFilter.getBoundingBoxString() + "&" + dataFilter.getViewParamString();
        System.out.println(httpString);
        
        URL url = new URL(httpString);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        int index=0;
        boolean firstTime=true;
        double lat, lon;
        long glm;
        while ((inputLine = in.readLine()) != null) {
//            System.out.println(inputLine);
            if (firstTime) { // skip header line then parse out the counts
            	firstTime=false;
            	continue; 
            }
            String [] fields = inputLine.split(",");
            String latlonStr=fields[2].trim();
            String time = fields[3].trim();
            
            // parse point string
            //POINT (-88.99 33.689)
            
            String [] tempArr = latlonStr.split("\\(");
            String tempStr = tempArr[1];
//            System.out.println(tempStr);
            tempArr = tempStr.split("\\)");
            tempStr = tempArr[0];
//            System.out.println(tempStr);
            String [] latlon = tempStr.split(" ");
            lon = Double.parseDouble(latlon[0]);
            lat = Double.parseDouble(latlon[1]);
//            System.out.println("lat " + lat +  " lon " + lon );
            glm = Long.parseLong(fields[4]);
            Position pos = new Position(LatLon.fromDegrees(lat, lon),  0);
             
            
            points.add(this.createPoint(pos,time, -1, -1, -1, glm));
  //          addRenderable(this.createPoint(pos,ts.toString(), Double.toString(value)));
            
//            nldn_flash_view.fid-286a9be5_147ea205445_166f	1008335	2011-08-04 23:56:00:005 	5.69999981	POINT (-88.99 33.689)
        }
        in.close();			 
		
    	
    }
    public void clearPoints()
    {
    	removeAllRenderables();
    	addRenderable(tooltip);
    	points = new ArrayList<>();
    	//points.clear();
    	
    }
    public void displayPoints()
    {
    	for(Renderable point:points) {
            addRenderable(point);
    	}
    	firePropertyChange(AVKey.LAYER, null, this);
    	
    }
//    public void reload()
//    {
//     	try {
//			readCsvData();
//	    	displayPoints();
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
// //   	firePropertyChange(AVKey.LAYER, null, this);
//    }


}
