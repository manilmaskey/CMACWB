package edu.uah.itsc.cmac.glm.ingest;

import java.io.IOException;
import java.sql.SQLException;

import edu.uah.itsc.cmac.glm.config.Config;
import edu.uah.itsc.cmac.glm.config.Config.DataType;
import edu.uah.itsc.cmac.glm.data.GlmEventData;
import edu.uah.itsc.cmac.glm.data.GlmFlashData;
import edu.uah.itsc.cmac.glm.data.LightningData;

public class IngestLightningData {

	private Config conf = new Config();
	
	public IngestLightningData(String filename) 
	{
		// check for filename string patterns to determine type of file
		
		Config.DataType type = DataType.OTHER;
		
		if (filename.indexOf("ENIfls")>=0 || filename.indexOf("ENTLN-flashes")>=0) {
			type = DataType.ENTLN_FLASH;
		}
		if (filename.indexOf("ENIstk")>=0 || filename.indexOf("ENTLN-strokes")>=0) {
			type = DataType.ENTLN_STROKE;
		}
		if (filename.indexOf("Nflash")>=0 ) {
			type = DataType.NLDN_FLASH;
		}
		if (filename.indexOf("Nstroke")>=0 ) {
			type = DataType.NLDN_STROKE;
		}
		if (filename.indexOf("gld360")>=0 ) {
			type = DataType.GLD360;
		}
		if (filename.indexOf("events_out")>=0 ) {
			type = DataType.GLM_EVENT;
		}
		if (filename.indexOf("flashes_out")>=0 ) {
			type = DataType.GLM_FLASH;
		}
		
		try {
			switch (type)
			{
				case ENTLN_FLASH:
				case ENTLN_STROKE:
				case NLDN_FLASH:
				case NLDN_STROKE:
				case GLD360:
					LightningData lightningData = new LightningData(type);
					lightningData.ReadFile(filename);
					lightningData.writeToDatabase();
					break;
				case GLM_EVENT:
					GlmEventData glmEventData = new GlmEventData();
			        glmEventData.ReadFile(filename);
			        glmEventData.writeToDatabase();
					break;
				case GLM_FLASH:
					GlmFlashData glmFlashData = new GlmFlashData();
			        glmFlashData.ReadFile(filename);
			        glmFlashData.writeToDatabase();
					break;
				case OTHER:
					System.err.println("Error: Cannot determine data type from filename");
					System.exit(-1);
					break;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error: I/O exception");
			System.exit(-1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error: SQL exception");
			System.exit(-1);
		}
        		
	}
	public static void main(String [] args){
		if (args.length<1) {
			System.out.println("Usage: java -Xmx512m -Duser.timezone=GMT -jar IngestLightningData.jar filename");
			System.exit(0);
		}
		System.out.println("filename " + args[0]);
		new IngestLightningData(args[0]);
	}

}
