package edu.uah.itsc.worldwind.eclipse.glider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.ImageUtil.AlignedImage;

public class GliderSurfaceImageLayer extends SurfaceImageLayer {

	GliderSurfaceImageLayer() {
		
	}
    public void addImage(String name, ArrayList<AlignedImage>tiles)
    {
        if (name == null)
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (tiles == null)
        {
            String message = Logging.getMessage("nullValue.tiles");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.imageTable.contains(name))
            this.removeImage(name);

        final ArrayList<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
        this.imageTable.put(name, surfaceImages);
        for (AlignedImage tile:tiles) {
            try
            {
                File tempFile = File.createTempFile("wwj-", ".png");
                tempFile.deleteOnExit();
                ImageIO.write(tile.image, "png", tempFile);
                SurfaceImage si = new SurfaceImage(tempFile.getPath(), tile.sector);
                surfaceImages.add(si);
                si.setOpacity(GliderSurfaceImageLayer.this.getOpacity());
                GliderSurfaceImageLayer.this.addRenderable(si);
            }
            catch (IOException e)
            {
                String message = Logging.getMessage("generic.ImageReadFailed");
                Logging.logger().severe(message);
            }
        	
        }
        
     }

}
