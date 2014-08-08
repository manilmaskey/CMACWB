package edu.uah.itsc.glmvalidationtool.views;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import edu.uah.itsc.glmvalidationtool.config.Config;
import edu.uah.itsc.glmvalidationtool.data.DataFilter;
import edu.uah.itsc.glmvalidationtool.data.DataFilterUpdate;
import edu.uah.itsc.glmvalidationtool.data.DataUtil;
import edu.uah.itsc.glmvalidationtool.data.GlmFlashEntry;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.formats.geojson.GeoJSONDoc;
import gov.nasa.worldwind.formats.geojson.GeoJSONObject;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;



/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MetricsView extends ViewPart implements DataFilterUpdate {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "MetricsView";

	private DataFilter dataFilter = new DataFilter();
	private Config conf = new Config();
	private Composite parent = null;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite comp) {
		
		parent = comp;
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		// need to do the rest in a method that gets called on DataFilterUpdate.refresh
		
//		Composite frameComposite = new Composite(parent, SWT.EMBEDDED
//                | SWT.NO_BACKGROUND);
//        frameComposite.setLayout(new FillLayout());
//        Frame frame = SWT_AWT.new_Frame(frameComposite);
//        double [] temp = {1.0,2.0,3.0,4.0,5.0};
        HistogramDataset hist =  new HistogramDataset();
 
        
        ArrayList <Double> arr = readHistogram(conf.getEntlnFlashRateLayer());
        
        
        int index=0;
        double [] entlnHistVal = new double [arr.size()];
        for (Double val:arr) {
        	entlnHistVal[index++]=val;
        }

        //hist.addSeries(1, temp, 5);
        hist.addSeries(0, entlnHistVal,arr.size());

                
//        JFreeChart entlnHist = ChartFactory.createHistogram("ENTLN Flash Frequency", "xAxisLabel", "yAxisLabel", hist, PlotOrientation.VERTICAL, false, false, false);
//        JFreeChart nldnHist = ChartFactory.createHistogram("NLDN Flash Frequency", "xAxisLabel", "yAxisLabel", hist, PlotOrientation.VERTICAL, false, false, false);
//        JFreeChart gld360Hist = ChartFactory.createHistogram("GLD360 Flash Frequency", "xAxisLabel", "yAxisLabel", hist, PlotOrientation.VERTICAL, false, false, false);
//        JFreeChart glmHist = ChartFactory.createHistogram("GLM Flash Frequency", "xAxisLabel", "yAxisLabel", hist, PlotOrientation.VERTICAL, false, false, false);
        JFreeChart entlnHist = createChart(hist, Color.CYAN, "ENTLN Flash Frequency");
        JFreeChart nldnHist = createChart(hist, Color.BLUE, "NLDN Flash Frequency");
        JFreeChart gld360Hist = createChart(hist, Color.PINK, "GLD360 Flash Frequency");
        JFreeChart glmHist = createChart(hist, Color.MAGENTA, "GLM Flash Frequency");
        
        ChartComposite chartFrame1 = new ChartComposite(parent, SWT.NONE, entlnHist, true);
        ChartComposite chartFrame2 = new ChartComposite(parent, SWT.NONE, nldnHist, true);
        ChartComposite chartFrame3 = new ChartComposite(parent, SWT.NONE, gld360Hist, true);
        ChartComposite chartFrame4 = new ChartComposite(parent, SWT.NONE, glmHist, true);
        
       //chartFrame.setLayout(new RowLayout(SWT.VERTICAL));
        //chartFrame.setLayoutData(new RowData(512, 512));
		
		

        dataFilter.registerObject(this); // register this object with filter update interface
// TODO need to fill in refresh method
        
//		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
//		
//		chartFrames = new ChartComposite[charts.length];
//		
//		for (int i=0; i<charts.length; i++)
//		{
//			chartFrames[i] = new ChartComposite(shell, SWT.NONE, charts[i], true);
//			chartFrames[i].setLayoutData(new RowData(FRAME_WIDTH, FRAME_HEIGHT));
//		}
//
//		// add listeners to dispose off the chart composites upon close
//		shell.addListener(SWT.Dispose, new Listener()
//		{
//			public void handleEvent(Event event)
//			{
//				disposeFrames();
//			}
//		});
//		shell.addShellListener(new ShellListener()
//		{
//			public void shellActivated(ShellEvent e)
//			{
//				HistogramDialog.this.imagePart.activatePart(true);
//			}
//			public void shellDeiconified(ShellEvent e)
//			{
//				HistogramDialog.this.imagePart.activatePart(true);
//			}
//			public void shellDeactivated(ShellEvent e)
//			{
//				HistogramDialog.this.imagePart.activatePart(false);
//			}
//			public void shellIconified(ShellEvent e)
//			{
//				HistogramDialog.this.imagePart.activatePart(false);
//			}
//			public void shellClosed(ShellEvent e)
//			{
//				HistogramDialog.this.imagePart.removePart();
//			}
//		});

		
	}

//	public void displayHistogram(Rectangle bounds)
//	{
//		// bounds = canvas.getSourceImage().getBounds(); // DEBUG
//		final int size = bounds.width * bounds.height;
////		ImageData imageData = getTargetRegionImageData(bounds);
//	
//		double [] redArray = new double [size];
//		double [] greenArray = new double [size];
//		double [] blueArray = new double [size];
//		
//		// this is to track the pixel values and find out if the histogram
//		// is being computed for a grayscale image - if so generate only 1 chart
//		boolean similarHistograms = true;
//		
//		// NOTE: better not to handle RGB info manually. Get pixel value and extract RGB 
//		// values using the palette. Refer notes in getTargetRegionImageData()
//		RGB rgb = null;
//		GliderObject go = ((ImageGlider)mImage).getGliderObject();
//		NetcdfFile cdfFile = go.getNcfile();
//		GliderChannel rChan = go.getChannels().get(mImage.getRedChan());
//		GliderChannel gChan = go.getChannels().get(mImage.getGreenChan());
//		GliderChannel bChan = go.getChannels().get(mImage.getBlueChan());		
//		for (int j=0, arrayIndex=0; j<bounds.height; j++)
//		{
//			for (int i=0; i<bounds.width; i++)
//			{
//				try 
//				{
//					// tab 5/18/12 line and pixel indices for readScaledPixelValue method were reversed in many locations
////					redArray[arrayIndex] = rChan.readScaledPixelValue(cdfFile, i, j);
////					greenArray[arrayIndex] = gChan.readScaledPixelValue(cdfFile, i, j);
////					blueArray[arrayIndex] = bChan.readScaledPixelValue(cdfFile, i, j);
//					redArray[arrayIndex] = rChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
//					greenArray[arrayIndex] = gChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
//					blueArray[arrayIndex] = bChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
//				} 
//				catch (Exception e) 
//				{
//					System.err.println("Error in ImageRGB::displayHistogram(Rectangle)");
//				}
//				
//				if (similarHistograms 
//						&& (blueArray[arrayIndex]!=greenArray[arrayIndex] 
//						        || greenArray[arrayIndex]!=redArray[arrayIndex]))
//				{
//					similarHistograms = false;
//				}
//				arrayIndex++;
//			}
//		}
//
//		/*
// 		System.err.println("Image Size : [" + imageData.width 
//				+ "] x [" + imageData.height + "] = " 
//				+ imageData.width*imageData.height);
//		int redSum=0;
//		for (int i=0; i<256; i++)
//		{
//			redSum += redArray[i];
//		}
//		System.err.println("Red Count : " + redSum);
//		int greenSum=0;
//		for (int i=0; i<256; i++)
//		{
//			greenSum += redArray[i];
//		}
//		System.err.println("Green Count : " + greenSum);
//		int blueSum=0;
//		for (int i=0; i<256; i++)
//		{
//			blueSum += redArray[i];
//		}
//		System.err.println("Blue Count : " + blueSum);
//		*/
//		//createDataSets(redArray, greenArray, blueArray);
//		
//		//TODO the dataset could be created with different min/max and num of bins
//		// right now it is only on the scaled values from 0 to 255
//		HistogramDataset redDataset = new HistogramDataset();
//		HistogramDataset greenDataset = new HistogramDataset();
//		HistogramDataset blueDataset = new HistogramDataset();
//		if (similarHistograms) {
//			redDataset.addSeries("Grayscale", redArray, 100, rChan.getMin(), rChan.getMax());
//		} else {
//			redDataset.addSeries("Red", redArray, 100, rChan.getMin(), rChan.getMax());
//		}
//		if (!similarHistograms) {
//			greenDataset.addSeries("Green", greenArray, 100, gChan.getMin(), gChan.getMax());
//			blueDataset.addSeries("Blue", blueArray, 100, bChan.getMin(), bChan.getMax());
//		}
//
//		// create channel lables for the chars
//		String redChLabel=null, greenChLabel=null, blueChLabel=null;
//		if (mImage instanceof ImageGlider)
//		{
//			redChLabel = go.getChannels().get(mImage.getRedChan()).getUnits();
//			greenChLabel = go.getChannels().get(mImage.getGreenChan()).getUnits();
//			blueChLabel = go.getChannels().get(mImage.getBlueChan()).getUnits();
//		}
//
//		// create the charts from the datasets
//		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
//		JFreeChart redChart = null;
//		if (similarHistograms) {
//			redChart = createChart(redDataset, Color.white, redChLabel + " in all 3 channels");
//		} else {
//			redChart = createChart(redDataset, Color.red, redChLabel);
//		}
//		charts.add(redChart);
//		
//		if (!similarHistograms) {
//			JFreeChart greenChart = createChart(greenDataset, Color.green, greenChLabel);
//			charts.add(greenChart);
//			JFreeChart blueChart = createChart(blueDataset, Color.blue, blueChLabel);
//			charts.add(blueChart);
//		}
//		
//		RegionOfInterest roi = new RegionOfInterest(bounds);
//		canvas.addImagePart(roi);
//		HistogramDialog histogramDialog = new HistogramDialog(
//				Display.getDefault().getActiveShell(), SWT.NONE);
//		histogramDialog.open(charts.toArray(new JFreeChart[0]), roi);
//		// this is done from the ImagePart.removePart() 
//		// canvas.removeImagePart(roi);
//	}
//	
///*
//	private void createDataSets(int[] redArray, int[] greenArray, int[] blueArray)
//	{
//		HistogramDataset redDataset = new HistogramDataset();
//		int INTERVAL = 5;
//		int tempR=0, tempG=0, tempB=0;
//		// row key will be 'Red' - column keys will the array indices
//		for (int i=0; i<256; i+=INTERVAL)
//		{
//			tempR=0; tempG=0; tempB=0;
//			for (int t=0; (t<INTERVAL) && (i+t<256); t++)
//			{
//				tempB += blueArray[i+t];
//				tempG += greenArray[i+t];
//				tempR += redArray[i+t];
//			}
//			//redDataset.addValue(tempR, "Red", String.valueOf(i+INTERVAL));
//			//redDataset.addValue(tempG, "Green", String.valueOf(i+INTERVAL));
//			//redDataset.addValue(tempB, "Blue", String.valueOf(i+INTERVAL));
//		}
//		
//		redDataset.addSeries("Red", new double[]{1,2,3,1,2,2,2,3}, 5, 1, 5);
//
//		DefaultCategoryDataset greenDataset = new DefaultCategoryDataset();
//		// row key will be 'Green' - column keys will the array indices
//		for (int i=0; i<256; i++)
//		{
//			greenDataset.addValue(greenArray[i], "Green", String.valueOf(i));
//		}
//
//		DefaultCategoryDataset blueDataset = new DefaultCategoryDataset();
//		// row key will be 'Blue' - column keys will the array indices
//		for (int i=0; i<256; i++)
//		{
//			blueDataset.addValue(blueArray[i], "Blue", String.valueOf(i));
//		}
//		
//		JFreeChart redChart = createChart(redDataset, "RGB");
//		
//		HistogramView histogramView = null;
//		try
//		{
//			histogramView = (HistogramView) 
//				getPage().getWorkbenchWindow()
//				.getActivePage().showView(
//					HistogramView.ID, null, 
//					IWorkbenchPage.VIEW_ACTIVATE);
//		}
//		catch (Exception e1)
//		{
//			e1.printStackTrace();
//		}
//		
//		histogramView.setChart(redChart);
//	}
//*/
//	
	private ArrayList<Double> readHistogram(String layer)
	{
		ArrayList <Double> arr = new ArrayList<>();
		// check start time to determine whether to group by secs, mins, hours
		// try to limit to 300 bins for minutes, and seconds, hours is unlimited
		String timeFormat=null;
		// within 5 minutes, use seconds
		if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 300000)
			timeFormat = "'YYYY-MM-DD HH24:MI:SS TZ'";
		// 5 hours
		else if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 18000000) 
			timeFormat = "'YYYY-MM-DD HH24:MI TZ'";
		else 
			timeFormat = "'YYYY-MM-DD HH24 TZ'";
		
		try {
 //   		String url = "jdbc:postgresql://54.83.58.23/glm_vv"; 
    		 
//			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat , "UTF-8");
			String encodedTimeFormat = URLEncoder.encode(";time_format:" + timeFormat);
//    		String url = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceString() + conf.getEntlnFlashRateLayer() + "&" + dataFilter.getValidationParamString(); 
    		String httpString = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceStringCsv() + layer + "&" + dataFilter.getValidationParamString() + encodedTimeFormat; 
	        System.out.println(httpString);
	        
	        URL url = new URL(httpString);
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

	        String inputLine;
	        int index=0;
	        boolean firstTime=true;
	        double count;
	        while ((inputLine = in.readLine()) != null) {
	            System.out.println(inputLine);
	            if (firstTime) { // skip header line then parse out the counts
	            	firstTime=false;
	            	continue; 
	            }
	            String [] fields = inputLine.split(",");
	            count = Double.parseDouble(fields[2]);
				arr.add(count);
	        }
	        in.close();			 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return arr;

	}
	private JFreeChart createChart(HistogramDataset histDataset, Color color, String axisLabel)
	{
		JFreeChart chart = ChartFactory.createHistogram(
				null, // the chart title is set on the dialog's shell
				axisLabel, // domain axis label
				null, //"Frequency", // range axis label
				histDataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				false, // tooltips?
				false // URLs?
				);

		XYPlot xyplot = (XYPlot) chart.getPlot();
		xyplot.setBackgroundPaint(Color.WHITE);
//		plot.setDomainGridlinePaint(Color.white);
//		plot.setDomainGridlinesVisible(false);
//		plot.setRangeGridlinePaint(Color.white);
//		plot.setRangeGridlinesVisible(false);
//		xyplot.setForegroundAlpha(0.85f);
        NumberAxis rangeaxis = (NumberAxis)xyplot.getRangeAxis();
        rangeaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeaxis.setTickLabelsVisible(false);
        rangeaxis.setTickMarksVisible(true);
        NumberAxis binaxis = (NumberAxis) xyplot.getDomainAxis();
        binaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        binaxis.setTickLabelsVisible(true);
        binaxis.setTickMarksVisible(true);
        binaxis.setMinorTickMarksVisible(true);
        XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
        xybarrenderer.setDrawBarOutline(false);
        xybarrenderer.setBarPainter(new StandardXYBarPainter());
        xybarrenderer.setShadowVisible(false);
        
        xybarrenderer.setSeriesPaint(0, color);
        
        return chart;
	}
//	private ArrayList<Integer> readHistogram(String table)
//	{
//		ArrayList <Integer> arr = new ArrayList<>();
//		// check start time to determine whether to group by secs, mins, hours
//		// try to limit to 300 bins for minutes, and seconds, hours is unlimited
//		String timeFormat=null;
//		// within 5 minutes, use seconds
//		if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 300000)
//			timeFormat = "'YYYY-MM-DD HH24:MI:SS TZ'";
//		// 5 hours
//		else if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 18000000) 
//			timeFormat = "'YYYY-MM-DD HH24:MI TZ'";
//		else 
//			timeFormat = "'YYYY-MM-DD HH24 TZ'";
//		
//		try {
// //   		String url = "jdbc:postgresql://54.83.58.23/glm_vv"; 
//    		 
//    		String url = conf.getProtocolJdbcPostgresql() + conf.getServerIP() + "/" + conf.getDatabaseName(); 
//
//			 
////			Connection con = DataUtil.establishConnection(conf.getProtocolHttp() + conf.getServerIP(), conf.getServerUname(), conf.getServerPw());
//			Connection con = DataUtil.establishConnection(url, conf.getServerUname(), conf.getServerPw());
//			Statement st = con.createStatement();
//			String query = "select  to_char(datetime, " + timeFormat +  ") as time, count (id) as frequency from " + table + " where the_geom && ST_MakeEnvelope(" + dataFilter.getMinLon() + "," + dataFilter.getMinLat() + "," + dataFilter.getMaxLon() + "," + dataFilter.getMaxLat() + ", 4326) and datetime between '"+ DataUtil.millisecondsToSQLTimeStampString(dataFilter.getStartTimeMilli()) + "' and '" + DataUtil.millisecondsToSQLTimeStampString(dataFilter.getEndTimeMilli())+ "' group by time order by time";
//			
//			ResultSet rs = st.executeQuery(query);
//			int count;
//			while (rs.next()) {
//				   
//				count = rs.getInt("frequency");
//				arr.add(count);
//				System.out.println(count);                  
//			}
//			st.close();
//	    } catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		return arr;
//
//	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}