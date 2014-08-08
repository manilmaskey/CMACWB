package edu.uah.itsc.glmvalidationtool.views;

import java.awt.Color;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.formats.geojson.GeoJSONGeometry;
import gov.nasa.worldwind.formats.geojson.GeoJSONMultiPoint;
import gov.nasa.worldwind.formats.geojson.GeoJSONPoint;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwindx.examples.GeoJSONLoader;

public class GlmValidationLoader extends GeoJSONLoader{

	
	// this allows us to set up a shared attribute between all points of layer
	AnnotationAttributes pointAttrs = new AnnotationAttributes();

    //**************************************************************//
    //********************  Primitive Geometry Construction  *******//
    //**************************************************************//

	
    protected Renderable createPoint(GeoJSONGeometry owner, Position pos, AnnotationAttributes attrs,
            AVList properties, String layerName)
        {
            AnnotationPointPlacemark p = new AnnotationPointPlacemark(pos, attrs);
            p.setAttributes(attrs);
            
 //           properties.getEntries();
            if (pos.getAltitude() != 0)
            {
                p.setAltitudeMode(WorldWind.ABSOLUTE);
             }
            else
            {
                p.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
            }

            if (properties != null)
                p.setValue(AVKey.PROPERTIES, properties);
            
            
            p.setValue("Time", properties.getValue("datetime"));
            p.setValue("LayerName", layerName);
            p.setValue("entlncount", properties.getValue("entlncount"));
            p.setValue("nldncount", properties.getValue("nldncount"));
            p.setValue("gldcount", properties.getValue("gldcount"));
           
            double entlnCnt = (double)properties.getValue("entlncount");
            double nldnCnt = (double)properties.getValue("nldncount");
            double gld360Cnt = (double)properties.getValue("gldcount");
//            p.setValue("Date", entry.getDate());
//            p.setValue("Time", entry.getTime());
//            p.setValue("Lat", entry.getLat());
//            p.setValue("Lon", entry.getLon());
//            p.setValue("Current", entry.getValue());
            
            p.getAttributes().setOpacity(1.0);
            p.getAttributes().setScale(1);
            
            int instrumentCnt = (entlnCnt>0?1:0) + (nldnCnt>0?1:0) + (gld360Cnt>0?1:0);

//            System.out.println("instrumentCnt " + instrumentCnt);
            if (instrumentCnt==0) {
            	p.setValue("DisplayColor", Color.RED);
//                p.getAttributes().setTextColor(Color.RED);
            }
            else if(instrumentCnt==1) {
            	p.setValue("DisplayColor", Color.YELLOW);
//                p.getAttributes().setTextColor(Color.YELLOW);  	
            }
            else {
            	p.setValue("DisplayColor", Color.GREEN);
//                p.getAttributes().setTextColor(Color.GREEN);  	
            }
            	
            return p;
        }

	@Override
    protected void addRenderableForPoint(GeoJSONPoint geom, RenderableLayer layer, AVList properties)
    {
//    	AnnotationAttributes attrs = new AnnotationAttributes();
    	// use shared attributes for now, may set individually if needed later

    	layer.addRenderable(this.createPoint(geom, geom.getPosition(), pointAttrs, properties, layer.getName()));
    }

	@Override
    protected void addRenderableForMultiPoint(GeoJSONMultiPoint geom, RenderableLayer layer, AVList properties)
    {
    	//PointPlacemarkAttributes attrs = this.createPointAttributes(geom, layer);

        for (int i = 0; i < geom.getPointCount(); i++)
        {
            layer.addRenderable(this.createPoint(geom, geom.getPosition(i), pointAttrs, properties, layer.getName()));
        }
    }

}
