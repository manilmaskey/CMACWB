package hold;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.DoubleBuffer;
import java.util.logging.Level;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.formats.geojson.GeoJSONDoc;
import gov.nasa.worldwind.formats.geojson.GeoJSONFeature;
import gov.nasa.worldwind.formats.geojson.GeoJSONFeatureCollection;
import gov.nasa.worldwind.formats.geojson.GeoJSONGeometry;
import gov.nasa.worldwind.formats.geojson.GeoJSONGeometryCollection;
import gov.nasa.worldwind.formats.geojson.GeoJSONLineString;
import gov.nasa.worldwind.formats.geojson.GeoJSONMultiLineString;
import gov.nasa.worldwind.formats.geojson.GeoJSONMultiPoint;
import gov.nasa.worldwind.formats.geojson.GeoJSONMultiPolygon;
import gov.nasa.worldwind.formats.geojson.GeoJSONObject;
import gov.nasa.worldwind.formats.geojson.GeoJSONPoint;
import gov.nasa.worldwind.formats.geojson.GeoJSONPolygon;
import gov.nasa.worldwind.formats.geojson.GeoJSONPositionArray;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.FrameFactory;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.GeoJSONLoader;
import gov.nasa.worldwindx.examples.util.RandomShapeAttributes;

public class GeoJSONLoaderAnnotation {
	
	Color color = Color.YELLOW;
//    private GlobeAnnotation tooltipAnnotation;
	AnnotationAttributes pointAttrs = new AnnotationAttributes();

	
    protected static final RandomShapeAttributes randomAttrs = new RandomShapeAttributes();

    /** Create a new loader. */
    public GeoJSONLoaderAnnotation()
    {
    }
    public GeoJSONLoaderAnnotation(Color col)
    {
    	color = col;
    }

    /**
     * Parse a GeoJSON document and add it to a layer.
     *
     * @param docSource GeoJSON document. May be a file path {@link String}, {@link java.io.File}, {@link java.net.URL},
     *                  or {@link java.net.URI}.
     * @param layer     layer to receive the new Renderable.
     */
    public void addSourceGeometryToLayer(Object docSource, RenderableLayer layer) throws Exception
    {
        if (WWUtil.isEmpty(docSource))
        {
   //         String message = Logging.getMessage("nullValue.SourceIsNull");
   //         Logging.logger().severe(message);
            System.err.println("Empty GeoJSON document");
            throw new Exception("Empty GeoJSON document layer: " + layer.getName());
        }

        if (layer == null)
        {
            String message = Logging.getMessage("nullValue.LayerIsNull");
            Logging.logger().severe(message);
            throw new Exception(message + ": " + layer.getName());
        }

//        // Init tooltip annotation
//        tooltipAnnotation = new GlobeAnnotation("", Position.fromDegrees(0, 0, 0));
//        Font font = Font.decode("Arial-Plain-16");
//        tooltipAnnotation.getAttributes().setFont(font);
//        tooltipAnnotation.getAttributes().setSize(new Dimension(400, 0));
//        tooltipAnnotation.getAttributes().setDistanceMinScale(1);
//        tooltipAnnotation.getAttributes().setDistanceMaxScale(1);
//        tooltipAnnotation.getAttributes().setVisible(false);
// //       tooltipAnnotation.setPickEnabled(false);
//        tooltipAnnotation.setPickEnabled(true);
//        tooltipAnnotation.setAlwaysOnTop(true);
//
//        layer.addRenderable(this.tooltipAnnotation);

        GeoJSONDoc doc = null;
        try
        {
            doc = new GeoJSONDoc(docSource);
            doc.parse();

            if (doc.getRootObject() instanceof GeoJSONObject)
            {
                this.addGeoJSONGeometryToLayer((GeoJSONObject) doc.getRootObject(), layer);
            }
            else if (doc.getRootObject() instanceof Object[])
            {
                for (Object o : (Object[]) doc.getRootObject())
                {
                    if (o instanceof GeoJSONObject)
                    {
                        this.addGeoJSONGeometryToLayer((GeoJSONObject) o, layer);
                    }
                    else
                    {
                        this.handleUnrecognizedObject(o);
                    }
                }
            }
            else
            {
                this.handleUnrecognizedObject(doc.getRootObject());
            }
        }
        catch (ConnectException e) {
            String message = Logging.getMessage("ConnectException.ExceptionAttemptingToReadGeoJSON", docSource);
//          Logging.logger().log(Level.SEVERE, message, e);
//           throw new WWRuntimeException(message, e);
          throw new Exception(message);
        	
        }
        catch (IOException e)
        {
            String message = Logging.getMessage("IOException.ExceptionAttemptingToReadGeoJSON", docSource);
//            Logging.logger().log(Level.SEVERE, message, e);
 //           throw new WWRuntimeException(message, e);
            throw new Exception(message);
        }
        finally
        {
            WWIO.closeStream(doc, docSource.toString());
        }
    }
    /**
     * Create a layer from a GeoJSON document.
     *
     * @param object GeoJSON object to be added to the layer.
     * @param layer layer to receive the new GeoJSON renderable.
     */
    public void addGeoJSONGeometryToLayer(GeoJSONObject object, RenderableLayer layer)
    {
        if (object == null)
        {
            String message = Logging.getMessage("nullValue.ObjectIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (layer == null)
        {
            String message = Logging.getMessage("nullValue.LayerIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (object.isGeometry())
            this.addRenderableForGeometry(object.asGeometry(), layer, null);

        else if (object.isFeature())
            this.addRenderableForFeature(object.asFeature(), layer);

        else if (object.isFeatureCollection())
            this.addRenderableForFeatureCollection(object.asFeatureCollection(), layer);

        else
            this.handleUnrecognizedObject(object);
    }

    /**
     * Create a layer from a GeoJSON document.
     *
     * @param docSource GeoJSON document. May be a file path {@link String}, {@link java.io.File}, {@link java.net.URL},
     *                  or {@link java.net.URI}.
     *
     * @return the new layer.
     * @throws Exception 
     */
    public Layer createLayerFromSource(Object docSource) throws Exception
    {
        if (WWUtil.isEmpty(docSource))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RenderableLayer layer = new RenderableLayer();
        addSourceGeometryToLayer(docSource, layer);

        return layer;
    }

    /**
     * Create a layer from a GeoJSON object.
     *
     * @param object GeoJSON object to use to create a Renderable, which will be added to the new layer.
     *
     * @return the new layer.
     */
    public Layer createLayerFromGeoJSON(GeoJSONObject object)
    {
        if (object == null)
        {
            String message = Logging.getMessage("nullValue.ObjectIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RenderableLayer layer = new RenderableLayer();
        addGeoJSONGeometryToLayer(object, layer);

        return layer;
    }

    protected void handleUnrecognizedObject(Object o)
    {
        Logging.logger().warning(Logging.getMessage("generic.UnrecognizedObjectType", o));
    }

    //**************************************************************//
    //********************  Geometry Conversion  *******************//
    //**************************************************************//

    protected void addRenderableForGeometry(GeoJSONGeometry geom, RenderableLayer layer, AVList properties)
    {
        if (geom.isPoint())
            this.addRenderableForPoint(geom.asPoint(), layer, properties);

        else if (geom.isMultiPoint())
            this.addRenderableForMultiPoint(geom.asMultiPoint(), layer, properties);

        else if (geom.isLineString())
            this.addRenderableForLineString(geom.asLineString(), layer, properties);

        else if (geom.isMultiLineString())
            this.addRenderableForMutiLineString(geom.asMultiLineString(), layer, properties);

        else if (geom.isPolygon())
            this.addRenderableForPolygon(geom.asPolygon(), layer, properties);

        else if (geom.isMultiPolygon())
            this.addRenderableForMultiPolygon(geom.asMultiPolygon(), layer, properties);

        else if (geom.isGeometryCollection())
            this.addRenderableForGeometryCollection(geom.asGeometryCollection(), layer, properties);

        else
            this.handleUnrecognizedObject(geom);
    }

    protected void addRenderableForGeometryCollection(GeoJSONGeometryCollection c, RenderableLayer layer,
        AVList properties)
    {
        if (c.getGeometries() == null || c.getGeometries().length == 0)
            return;

        for (GeoJSONGeometry geom : c.getGeometries())
        {
            this.addRenderableForGeometry(geom, layer, properties);
        }
    }

    protected void addRenderableForFeature(GeoJSONFeature feature, RenderableLayer layer)
    {
        if (feature.getGeometry() == null)
        {
            Logging.logger().warning(Logging.getMessage("nullValue.GeometryIsNull"));
            return;
        }

        this.addRenderableForGeometry(feature.getGeometry(), layer, feature.getProperties());
    }

    protected void addRenderableForFeatureCollection(GeoJSONFeatureCollection c, RenderableLayer layer)
    {
        if (c.getFeatures() != null && c.getFeatures().length == 0)
            return;

        for (GeoJSONFeature feat : c.getFeatures())
        {
            this.addRenderableForFeature(feat, layer);
        }
    }

    protected void addRenderableForPoint(GeoJSONPoint geom, RenderableLayer layer, AVList properties)
    {
//    	AnnotationAttributes attrs = new AnnotationAttributes();
    	// use shared attributes for now, may set individually if needed later

    	layer.addRenderable(this.createPoint(geom, geom.getPosition(), pointAttrs, properties, layer.getName()));
    }

    protected void addRenderableForMultiPoint(GeoJSONMultiPoint geom, RenderableLayer layer, AVList properties)
    {
    	//PointPlacemarkAttributes attrs = this.createPointAttributes(geom, layer);

        for (int i = 0; i < geom.getPointCount(); i++)
        {
            layer.addRenderable(this.createPoint(geom, geom.getPosition(i), pointAttrs, properties, layer.getName()));
        }
    }

    protected void addRenderableForLineString(GeoJSONLineString geom, RenderableLayer layer, AVList properties)
    {
        ShapeAttributes attrs = this.createPolylineAttributes(geom, layer);

        layer.addRenderable(this.createPolyline(geom, geom.getCoordinates(), attrs, properties));
    }

    protected void addRenderableForMutiLineString(GeoJSONMultiLineString geom, RenderableLayer layer, AVList properties)
    {
        ShapeAttributes attrs = this.createPolylineAttributes(geom, layer);

        for (GeoJSONPositionArray coords : geom.getCoordinates())
        {
            layer.addRenderable(this.createPolyline(geom, coords, attrs, properties));
        }
    }

    protected void addRenderableForPolygon(GeoJSONPolygon geom, RenderableLayer layer, AVList properties)
    {
        ShapeAttributes attrs = this.createPolygonAttributes(geom, layer);

        layer.addRenderable(this.createPolygon(geom, geom.getExteriorRing(), geom.getInteriorRings(), attrs,
            properties));
    }

    protected void addRenderableForMultiPolygon(GeoJSONMultiPolygon geom, RenderableLayer layer, AVList properties)
    {
        ShapeAttributes attrs = this.createPolygonAttributes(geom, layer);

        for (int i = 0; i < geom.getPolygonCount(); i++)
        {
            layer.addRenderable(
                this.createPolygon(geom, geom.getExteriorRing(i), geom.getInteriorRings(i), attrs, properties));
        }
    }

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
            
            
            p.setValue("LayerName", layerName);
            p.setValue("Date", properties.getValue("datetime"));
            p.setValue("Current", properties.getValue("measured_value"));
//            p.setValue("Date", entry.getDate());
//            p.setValue("Time", entry.getTime());
//            p.setValue("Lat", entry.getLat());
//            p.setValue("Lon", entry.getLon());
//            p.setValue("Current", entry.getValue());
            
            p.getAttributes().setOpacity(1.0);
            p.getAttributes().setTextColor(color);
            p.getAttributes().setScale(1);
            

            return p;
        }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected Renderable createPolyline(GeoJSONGeometry owner, Iterable<? extends Position> positions,
        ShapeAttributes attrs, AVList properties)
    {
        if (positionsHaveNonzeroAltitude(positions))
        {
            Path p = new Path();
            p.setPositions(positions);
            p.setAltitudeMode(WorldWind.ABSOLUTE);
            p.setAttributes(attrs);

            if (properties != null)
                p.setValue(AVKey.PROPERTIES, properties);

            return p;
        }
        else
        {
            SurfacePolyline sp = new SurfacePolyline(attrs, positions);

            if (properties != null)
                sp.setValue(AVKey.PROPERTIES, properties);

            return sp;
        }
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected Renderable createPolygon(GeoJSONGeometry owner, Iterable<? extends Position> outerBoundary,
        Iterable<? extends Position>[] innerBoundaries, ShapeAttributes attrs, AVList properties)
    {
        if (positionsHaveNonzeroAltitude(outerBoundary))
        {
            Polygon poly = new Polygon(outerBoundary);
            poly.setAttributes(attrs);

            if (innerBoundaries != null)
            {
                for (Iterable<? extends Position> iter : innerBoundaries)
                {
                    poly.addInnerBoundary(iter);
                }
            }

            if (properties != null)
                poly.setValue(AVKey.PROPERTIES, properties);

            return poly;
        }
        else
        {
            SurfacePolygon poly = new SurfacePolygon(attrs, outerBoundary);

            if (innerBoundaries != null)
            {
                for (Iterable<? extends Position> iter : innerBoundaries)
                {
                    poly.addInnerBoundary(iter);
                }
            }

            if (properties != null)
                poly.setValue(AVKey.PROPERTIES, properties);

            return poly;
        }
    }

    protected static boolean positionsHaveNonzeroAltitude(Iterable<? extends Position> positions)
    {
        for (Position pos : positions)
        {
            if (pos.getAltitude() != 0)
                return true;
        }

        return false;
    }

    //**************************************************************//
    //********************  Attribute Construction  ****************//
    //**************************************************************//

    @SuppressWarnings( {"UnusedDeclaration"})
    protected ShapeAttributes createPolylineAttributes(GeoJSONGeometry geom, Layer layer)
    {
        if (layer == null)
            return randomAttrs.nextPolylineAttributes();

        String key = this.getClass().getName() + ".PolylineAttributes";
        ShapeAttributes attrs = (ShapeAttributes) layer.getValue(key);
        if (attrs == null)
        {
            attrs = randomAttrs.nextPolylineAttributes();
            layer.setValue(key, attrs);
        }

        return attrs;
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected ShapeAttributes createPolygonAttributes(GeoJSONGeometry geom, Layer layer)
    {
        if (layer == null)
            return randomAttrs.nextPolygonAttributes();

        String key = this.getClass().getName() + ".PolygonAttributes";
        ShapeAttributes attrs = (ShapeAttributes) layer.getValue(key);
        if (attrs == null)
        {
            attrs = randomAttrs.nextPolygonAttributes();
            layer.setValue(key, attrs);
        }

        return attrs;
    }

    
    
    
    
    
    
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

//            this.applyColor(dc, this.getAttributes().getTextColor(), 0.6 * opacity, true);
            this.applyColor(dc, this.getAttributes().getTextColor(), opacity, true);

            // Draw 16x16 shape from its bottom left corner
            int size = 8;
            if (this.shapeBuffer == null)
                this.shapeBuffer = FrameFactory.createShapeBuffer(AVKey.SHAPE_ELLIPSE, size, size, 0, null);
            GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.
            gl.glTranslated(-size / 2, -size / 2, 0);
            FrameFactory.drawBuffer(dc, GL.GL_TRIANGLE_FAN, this.shapeBuffer);
        }
    }

}
