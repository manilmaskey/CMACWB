package edu.uah.itsc.cmac.glm.views;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwindx.examples.util.LayerManagerLayer;

public class GroundNetworkLayerManager extends LayerManagerLayer {

	public GroundNetworkLayerManager (WorldWindowGLCanvas wwd)
	{
		super (wwd);
			
	}
	
	@Override
    protected String makeAnnotationText(LayerList layers)
    {
        // Compose html text
        StringBuilder text = new StringBuilder();
        Color color;
        int i = 0;
        for (Layer layer : layers)
        {
            if (!this.isMinimized() || layer == this)
            {
            	if (layer instanceof GroundNetworkLayer) {
            		color = ((GroundNetworkLayer)layer).getColor();
 //           		System.out.println(" makeAnnotationText custom color " + color);
            	}
            	else {
            		color = this.getColor();
//            		System.out.println(" makeAnnotationText default " + color);
            	}
//            	if (layerColor.get(layer.getName())==null) color=this.getColor();
//            	else color=layerColor.get(layer.getName());
                color = (i == getSelectedIndex()) ? getHighlightColor() : color;
                color = (i == dragRefIndex) ? dragColor : color;
                text.append("<a href=\"");
                text.append(i);
                text.append("\"><font color=\"");
                text.append(encodeHTMLColor(color));
                text.append("\">");
                text.append((layer.isEnabled() ? getLayerEnabledSymbol() : getLayerDisabledSymbol()));
                text.append(" ");
                text.append((layer.isEnabled() ? "<b>" : "<i>"));
                text.append(layer.getName());
                text.append((layer.isEnabled() ? "</b>" : "</i>"));
                text.append("</a><br />");
            }
            i++;
        }
        return text.toString();
    }
	
}
