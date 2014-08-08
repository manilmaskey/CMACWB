package hold;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NCdump;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import org.postgresql.geometric.PGpoint;

public class ConcNetcdfExtracter {
	
	
	  Array data2D;
	  Index index;
	  
	  Array dataLat;
	  Index indexLat;
	  Array dataLon;
	  Index indexLon;
	  String rundate;
	  NetClientPost drupalClient;
	  public ConcNetcdfExtracter(String filename,String gridFile){
	  //String filename = "C:/data/my/file.nc";
	  NetcdfFile ncfile = null;
	  
	  try {
		  drupalClient = new NetClientPost();  
		String fname =  new File(filename).getName();
		String datehour = fname.substring(13, 23);
			
		rundate  = datehour.substring(0,4)+"-"+datehour.substring(4,6)+"-"+datehour.substring(6,8);
		String hour = datehour.substring(8,10);  
	    ncfile = NetcdfFile.open(filename);
	    processGridFile(gridFile);
	    process( ncfile, hour);
	  } catch (IOException ioe) {
	    System.out.print("trying to open " + filename+" "+ioe.toString());
	  } finally { 
	    if (null != ncfile) try {
	      ncfile.close();
	    } catch (IOException ioe) {
	    	System.out.println("trying to close " + filename+" "+ioe.toString());
	    }
	  }
	  }
	  
	  
void processGridFile(String gridFile){
	  try{	
		  NetcdfFile ncfile = null;
  	      ncfile = NetcdfFile.open(gridFile);		  
		  Variable vLat = ncfile.findVariable("PLAT");	
		  Variable vLon = ncfile.findVariable("PLON");
		  dataLat = vLat.read();
		 // int[] shapeLat = dataLat.getShape();
		  indexLat = dataLat.getIndex();
		  dataLon = vLon.read();
		 // int[] shapeLon = dataLat.getShape();
		  indexLon = dataLat.getIndex();
	  }
		  catch(Exception e){
			  System.out.println("Exception "+e.toString());
		  }
}

void process(NetcdfFile ncfile, String hour){
  try{	
	
		  java.sql.Connection conn; 
		  Vector<String> uris  = new Vector<String>();
			  
		    Class.forName("org.postgresql.Driver"); 

			String url = "jdbc:postgresql://localhost:5432/noaaaqdev"; 
			conn = DriverManager.getConnection(url, "cm_noaaaqdev", "ungag1!PINION"); 
			conn.setAutoCommit(false);
	        Statement st = conn.createStatement();
		    
            String removeOldConc = "delete from concentration";
            st.executeUpdate(removeOldConc);     
            
            conn.commit();
	        
            String removeOldVis = "delete from visibility";
            st.executeUpdate(removeOldVis);     
            
            conn.commit();
	        
	        Variable v = ncfile.findVariable("PARTICLE");
	        int itHour;
            if (hour.equals("00")){
            	itHour = 0;
            }
            else{
            	itHour = 12;
            }
	        
	        
	        int count=1;
	        
	        
	        ///insert concentration
	        
		  //  for (int k=0; k<9; k++){
	        for (int k=1; k<9; k++){
		    	int[] origin = new int[] {k, 5, 0, 0};
		        int[] size = new int[] {1,1, 400, 250};
		        Array data4D = v.read(origin, size);
		        data2D = data4D.reduce();
		        /*
		        int min = k*30;
		        int iHour = itHour + min/60;
		        		        
		        String sHour = String.valueOf(iHour);
			    if (iHour < 10)
			    	sHour = "0"+ String.valueOf(iHour);
			    int iMin = min % 60;
		        String sMin = String.valueOf(iMin);
			    if (iMin < 10)
			    	sMin = "0"+ String.valueOf(iMin);			    
			    
			    String rundatetime = rundate+" "+sHour+":"+sMin;	   
		        */
		        int iHour = k + 14;
		        		        
		        String sHour = String.valueOf(iHour);
			    
			    String rundatetime = rundate+" "+sHour+":00";	  
			    
			    int[] shape = data2D.getShape();
		        index = data2D.getIndex();
		        //System.out.println(shape[0]+ "  " +shape[1]);
		        
		        for (int i=0; i<shape[0]; i++) {
		      	    for (int j=0; j<shape[1]; j++) {
		      	      count++;	
		      	      float fval = data2D.getFloat(index.set(i,j));	
		      	      float fLat = dataLat.getFloat(indexLat.set(i,j));
		      	      float fLon = dataLon.getFloat(indexLon.set(i,j));
		      	      if (fval > 0.0){
				    	if (count % 1000 == 0){
				    	    st.executeBatch();
				    	    conn.commit();
				    	    st.clearBatch();
				    	    count = 1;
				    	}
				    	else
				    		st.addBatch("INSERT INTO concentration(runtime,altitude,value,location) VALUES"+"(to_timestamp('"+rundatetime+"','YYYY-MM-DD HH24:MI'),"+0.0+","+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
				    	}
		      	    }
		      	  }	
		      String runTime = rundatetime+":00";  
		      
		      
		      
		       uris.add(drupalClient.insertLayersToDrupal("P "+runTime, "http://demanddev.itsc.uah.edu/geoserver/fires/wms/kml?layers=fires:plumes&cql_filter=runtime='"+runTime+"'","881"));  
		       uris.add(drupalClient.insertLayersToDrupal("B "+runTime, "http://demanddev.itsc.uah.edu/geoserver/fires/wms/kml?layers=fires:plume_boundaries&cql_filter=timestep='"+runTime+"'","882"));
     	       uris.add(drupalClient.insertLayersToDrupal("R "+runTime, "http://demanddev.itsc.uah.edu/geoserver/fires/wms/kml?layers=fires:visibility_motorway_intersection&cql_filter=timestep='"+runTime+"'","886"));
     	       uris.add(drupalClient.insertLayersToDrupal("H "+runTime, "http://demanddev.itsc.uah.edu/geoserver/fires/wms/kml?layers=fires:concentration_hosp_intersection&cql_filter=timestep='"+runTime+"'","885"));
     	       uris.add(drupalClient.insertLayersToDrupal("S "+runTime, "http://demanddev.itsc.uah.edu/geoserver/fires/wms/kml?layers=fires:concentration_school_intersection&cql_filter=timestep='"+runTime+"'","887"));
		    }
  	
          st.executeBatch(); 	
          conn.commit();

        System.out.println("Start visibility--------------------------------------------------");  
          //insert visibility
        Variable vis = ncfile.findVariable("VISB_Recon_tot");
        //for (int k=1; k<9; k++){
        for (int k=1; k<9; k++){
    	int[] origin = new int[] {k, 0, 0};
        int[] size = new int[] {1,400, 250};
        Array data4D = vis.read(origin, size);
        data2D = data4D.reduce();
        
        
        /*
        int min = k*30;
        int iHour = itHour + min/60;
        		        
        String sHour = String.valueOf(iHour);
	    if (iHour < 10)
	    	sHour = "0"+ String.valueOf(iHour);
	    int iMin = min % 60;
        String sMin = String.valueOf(iMin);
	    if (iMin < 10)
	    	sMin = "0"+ String.valueOf(iMin);			    
	    
	    String rundatetime = rundate+" "+sHour+":"+sMin;	
	    */
	    
  
        int iHour = k + 14;
        		        
        String sHour = String.valueOf(iHour);

        String rundatetime = rundate+" "+sHour+":00";	
	    
        
        int[] shape = data2D.getShape();
        index = data2D.getIndex();
        //System.out.println(shape[0]+ "  " +shape[1]);
        
        st = conn.createStatement();
        count = 1;
        for (int i=0; i<shape[0]; i++) {
      	    for (int j=0; j<shape[1]; j++) {
      	      count++;	
      	      float fval = data2D.getFloat(index.set(i,j));	
      	      float fLat = dataLat.getFloat(indexLat.set(i,j));
      	      float fLon = dataLon.getFloat(indexLon.set(i,j));
      	      if (fval > 0.0 && fval < 5.0){
		    	if (count % 1000 == 0){
		    	    st.executeBatch();
		    	    conn.commit();
		    	    st.clearBatch();
		    	    count = 1;
		    	}
		    	else
		    		st.addBatch("INSERT INTO visibility(runtime,value,location) VALUES"+"(to_timestamp('"+rundatetime+"','YYYY-MM-DD HH24:MI'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
		    	}
      	    }
      	  }	
      
        }    
      st.executeBatch(); 	
      conn.commit();     
         
          ////// end insert visibility
           System.out.println("Updating concentration");
	       st = conn.createStatement();
	       ResultSet rs = st.executeQuery("SELECT id,AsText(location) as location from plumes");
	       
	       while (rs.next()) {
	    	   
	               int id = rs.getInt("id");
	               String location = rs.getString("location");
	               String sClosest = "select value from concentration where location = (SELECT ST_ClosestPoint(ST_Collect(s.location), ST_GeomFromText(('"+location+"'), 4326)) from (select location from concentration) as s) LIMIT 1";
	               //System.out.println("sClosest="+sClosest);
	               
	               Statement st1 = conn.createStatement();
	               ResultSet rsClosest = st1.executeQuery(sClosest);
	               if(rsClosest.next()){
	            	   
		               double value = rsClosest.getDouble("value");
		               
		               String sUpdate = "update plumes set concentration = "+value+" where id = "+id;
		               Statement st2 = conn.createStatement();
		               st2.executeUpdate(sUpdate);     
		               conn.commit();
		               st2.close();
	               }
	               st1.close();
	                 
	            }
	      st.close(); 
	      
          //update visibility
	       System.out.println("Ready to Update Visibility ");
	       st = conn.createStatement();
	       rs = st.executeQuery("SELECT id,AsText(location) as location from plumes");
	       
	       while (rs.next()) {
	    	   //System.out.println("while start");
	               int id = rs.getInt("id");
	               String location = rs.getString("location");
	               String sClosest = "select value from visibility where location = (SELECT ST_ClosestPoint(ST_Collect(s.location), ST_GeomFromText(('"+location+"'), 4326)) from (select location from visibility) as s) LIMIT 1";
	               //System.out.println("sClosest="+sClosest);
	               
	               Statement st1 = conn.createStatement();
	               ResultSet rsClosest = st1.executeQuery(sClosest);
	               if(rsClosest.next()){
	            	   
		               double value = rsClosest.getDouble("value");
		               //System.out.println("value="+value);
		               String sUpdate = "update plumes set visibility = "+value+" where id = "+id;
		               Statement st2 = conn.createStatement();
		               st2.executeUpdate(sUpdate);     
		               conn.commit();
		               st2.close();
	               }
	               st1.close();
	                  
	            }
	      st.close(); 
	    System.out.println("Done updating visibility");
	      //end update visibility
	      
	      
	    
	    //////
		//public void updateBoundaries(){


	    			Statement st4 = conn.createStatement();
		         //   String plumeBoundariesUpdate = "INSERT into plume_boundaries(select d.name,to_char(d.runtime,'YYYY-MM-DD HH24:MI:SS'), ST_ConvexHull(ST_Collect(d.location)) As the_geom FROM plumes as d GROUP BY d.name,d.runtime)";
		           String plumeBoundariesUpdate = "INSERT into plume_boundaries(select d.name,to_char(d.runtime,'YYYY-MM-DD HH24:MI:SS'), ST_ConvexHull(ST_Collect(d.location)) As the_geom,max(d.concentration),min(d.visibility) FROM plumes as d GROUP BY d.name,d.runtime)";
			        st4.executeUpdate(plumeBoundariesUpdate);  
		            conn.commit();
				     

		//}
	    /////
	    
	    


           String sClosest = "select * from current_layers";
           //System.out.println("sClosest="+sClosest);
           
           Statement st1 = conn.createStatement();
           ResultSet rsClosest = st1.executeQuery(sClosest);
           
           
           System.out.println("Ready to delete Existing Layers: ");
           while (rsClosest.next()){
        	   
               String node = rsClosest.getString("uri");
               
               System.out.println("Existing Layers: "+node);
               
               drupalClient.deleteLayersFromDrupal(node);

           }
           st1.close();
	      
	      
          Statement st2 = conn.createStatement();
          st2.executeUpdate("delete from current_layers");     
          conn.commit();
          st2.close();
	      
          int numURIs = uris.size();
          
          System.out.println("Inserting Current Layers" );
          
          for (int i=0;i<numURIs;i++){
	          Statement st3 = conn.createStatement();
	          System.out.println("Current Layers: "+ uris.get(i));
	          String newLayer = "insert into current_layers(uri) values ('"+ uris.get(i) +"')";
	          st3.executeUpdate(newLayer);     
	          conn.commit();
	          st3.close();
          }
	      conn.close();
	      

  }
  catch(Exception e){
	  System.out.println("Exception "+e.toString());
  }
}


public static void main(String [] args){
	new ConcNetcdfExtracter(args[0],args[1]);
}
}
