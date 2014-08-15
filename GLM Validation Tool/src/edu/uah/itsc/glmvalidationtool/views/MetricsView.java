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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
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

	private static DataFilter dataFilter = new DataFilter();
	private static Config conf = new Config();
	private static Composite parent = null;
	private static JFreeChart entlnHist;
	private static JFreeChart nldnHist;
	private static JFreeChart gld360Hist;
	private  static JFreeChart glmHist;
	private static long timeInterval;
	private static String flashesPer = "";

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite comp) {
		
		parent = comp;
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		// need to do the rest in a method that gets called on DataFilterUpdate.refresh
		     
//		XYDataset entlnDataset = readHistogram(conf.getEntlnFlashRateLayer());
//		XYDataset nldnDataset = readHistogram(conf.getNldnFlashRateLayer());
//		XYDataset gld360Dataset = readHistogram(conf.getGld360FlashRateLayer());
//		XYDataset glmDataset = readHistogram(conf.getGlmFlashRateLayer());
		XYDataset entlnDataset = null;
		XYDataset nldnDataset = null;
		XYDataset gld360Dataset = null;
		XYDataset glmDataset = null;
        
//        entlnHist = createChart(entlnDataset, Color.CYAN, "ENTLN Flash Rate" + flashesPer);
//        nldnHist = createChart(nldnDataset, Color.BLUE, "NLDN Flash Rate" + flashesPer);
//        gld360Hist = createChart(gld360Dataset, Color.PINK, "GLD360 Flash Rate" + flashesPer);
//        glmHist = createChart(glmDataset, Color.MAGENTA, "GLM Flash Rate" + flashesPer);

        entlnHist = createChart(entlnDataset, Color.CYAN, "");
        nldnHist = createChart(nldnDataset, Color.BLUE, "");
        gld360Hist = createChart(gld360Dataset, Color.PINK, "");
        glmHist = createChart(glmDataset, Color.MAGENTA, "");
        entlnHist.setTitle("ENTLN Flash Rate " + flashesPer);
        nldnHist.setTitle("NLDN Flash Rate " + flashesPer);
        gld360Hist.setTitle("GLD360 Flash Rate " + flashesPer);
        glmHist.setTitle("GLM Flash Rate " + flashesPer);
       
        ChartComposite chartFrame1 = new ChartComposite(parent, SWT.NONE, entlnHist, true);
        ChartComposite chartFrame2 = new ChartComposite(parent, SWT.NONE, nldnHist, true);
        ChartComposite chartFrame3 = new ChartComposite(parent, SWT.NONE, gld360Hist, true);
        ChartComposite chartFrame4 = new ChartComposite(parent, SWT.NONE, glmHist, true);
        
       //chartFrame.setLayout(new RowLayout(SWT.VERTICAL));
        //chartFrame.setLayoutData(new RowData(512, 512));

        dataFilter.registerObject(this); // register this object with filter update interface
// TODO need to fill in refresh method
        
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

	private XYDataset readHistogram(String layer)
	{
        final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();

        final TimePeriodValues s1 = new TimePeriodValues("Series 1");
 //       long timeInterval = 0; // in milliseconds
		ArrayList <Double> arr = new ArrayList<>();
		// check start time to determine whether to group by secs, mins, hours
		// try to limit to 300 bins for minutes, and seconds, hours is unlimited
		String timeFormat=null;
		String appendTime = "";
		// within 5 minutes, use seconds
//		if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 300000) {
		if (dataFilter.getDisplayIntervalMilli() < 300000) {
			timeFormat = "'YYYY-MM-DD HH24:MI:SS TZ'";
			timeInterval = 1000; // second
			flashesPer = "(/sec)";
//			series = new TimeSeries("Time series data", Second.class);
		}
		// 5 hours
//		else if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 18000000) {
		else if (dataFilter.getDisplayIntervalMilli() < 18000000) {
			timeFormat = "'YYYY-MM-DD HH24:MI TZ'";
			timeInterval = 60000; // minute
			appendTime = ":00";
			flashesPer = "(/min)";
//			series = new TimeSeries("Time series data", Minute.class);
		}
		else  {
			timeFormat = "'YYYY-MM-DD HH24 TZ'";
			timeInterval = 3600000;  // hour
			appendTime = ":00:00";
			flashesPer = "(/hr)";
//			series = new TimeSeries("Time series data", Hour.class);
		}
		
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
//	            System.out.println(inputLine);
	            if (firstTime) { // skip header line then parse out the counts
	            	firstTime=false;
	            	continue; 
	            }
	            String [] fields = inputLine.split(",");
	            count = Double.parseDouble(fields[2]);
	            String binTime = fields[1].trim() + appendTime;
	            
//	            System.out.println("binTime " + binTime);
	            Timestamp ts = Timestamp.valueOf(binTime);
	            
				arr.add(count);
//	    		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));	
	    		Date startDate = new Date(ts.getTime());
//	    		Date endDate = new Date(ts.getTime()+timeInterval-1);
	    		Date endDate = new Date(ts.getTime()+timeInterval);
	    		
	    		s1.add(new SimpleTimePeriod(startDate, endDate), count);
	                
//	            final DateFormat df = DateFormat.getInstance();                       
//	            try {
//	                final Date d0 = df.parse("11/5/2003 0:00:00.000");
//	                final Date d1 = df.parse("11/5/2003 0:15:00.000");
//	                final Date d2 = df.parse("11/5/2003 0:30:00.000");
//	                final Date d3 = df.parse("11/5/2003 0:45:00.000");
//	                final Date d4 = df.parse("11/5/2003 1:00:00.001");
//	                final Date d5 = df.parse("11/5/2003 1:14:59.999");
//	                final Date d6 = df.parse("11/5/2003 1:30:00.000");
//	                final Date d7 = df.parse("11/5/2003 1:45:00.000");
//	                final Date d8 = df.parse("11/5/2003 2:00:00.000");
//	                final Date d9 = df.parse("11/5/2003 2:15:00.000");
//	                    
//	                s1.add(new SimpleTimePeriod(d0, d1), 0.39);
//	                //s1.add(new SimpleTimePeriod(d1, d2), 0.338);
//	                s1.add(new SimpleTimePeriod(d2, d3), 0.225);
//	                s1.add(new SimpleTimePeriod(d3, d4), 0.235);
//	                s1.add(new SimpleTimePeriod(d4, d5), 0.238);
//	                s1.add(new SimpleTimePeriod(d5, d6), 0.236);
//	                s1.add(new SimpleTimePeriod(d6, d7), 0.25);
//	                s1.add(new SimpleTimePeriod(d7, d8), 0.238);
//	                s1.add(new SimpleTimePeriod(d8, d9), 0.215);
//	            }
//	            catch (Exception e) {
//	                System.out.println(e.toString());
//	            }
	        }
	        in.close();			 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
        dataset.addSeries(s1);
        dataset.setDomainIsPointsInTime(false);
		return dataset;

	}
	private JFreeChart createChart(XYDataset histDataset, Color color, String axisLabel)
	{
//		JFreeChart chart = ChartFactory.createHistogram(
//				null, // the chart title is set on the dialog's shell
//				axisLabel, // domain axis label
//				null, //"Frequency", // range axis label
//				histDataset, // data
//				PlotOrientation.VERTICAL, // orientation
//				false, // include legend
//				false, // tooltips?
//				false // URLs?
//				);
		JFreeChart chart = ChartFactory.createXYBarChart(
				null, // the chart title is set on the dialog's shell
				axisLabel, // domain axis label
				true,
				null, //"Frequency", // range axis label
				(IntervalXYDataset) histDataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				false, // tooltips?
				false // URLs?
				);
//		XYBarRenderer renderer = (XYBarRenderer) chart.getXYPlot().getRenderer();
//	    renderer..setItemMargin(.1);
//title, xAxisLabel, dateAxis, yAxisLabel, dataset, orientation, legend, tooltips, urls
		XYPlot xyplot = (XYPlot) chart.getPlot();
		xyplot.setBackgroundPaint(Color.WHITE);
//		plot.setDomainGridlinePaint(Color.white);
//		plot.setDomainGridlinesVisible(false);
//		plot.setRangeGridlinePaint(Color.white);
//		plot.setRangeGridlinesVisible(false);
//		xyplot.setForegroundAlpha(0.85f);
        NumberAxis rangeaxis = (NumberAxis)xyplot.getRangeAxis();
        rangeaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeaxis.setTickLabelsVisible(true);
        rangeaxis.setTickMarksVisible(true);
        
//        NumberAxis binaxis = (NumberAxis) xyplot.getDomainAxis();
//        binaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        binaxis.setTickLabelsVisible(true);
//        binaxis.setTickMarksVisible(true);
//        binaxis.setMinorTickMarksVisible(true);
        DateAxis binaxis = (DateAxis) xyplot.getDomainAxis();

        binaxis.setStandardTickUnits(binaxis.createStandardDateTickUnits(TimeZone.getTimeZone("GMT")));
        binaxis.setTickLabelsVisible(true);
        binaxis.setTickMarksVisible(true);
        binaxis.setMinorTickMarksVisible(true);
        
        
        XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
        xybarrenderer.setDrawBarOutline(false);
        xybarrenderer.setMargin(0.1);
        xybarrenderer.setBarPainter(new StandardXYBarPainter());
        xybarrenderer.setShadowVisible(false);
        
        xybarrenderer.setSeriesPaint(0, color);
        
        return chart;
	}
	
// this version reads directly from database, not geoserver
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
	
	

///**
// * This demo shows a bar chart with time based data where the time periods are slightly
// * irregular.
// *
// */
//public class TimePeriodValuesDemo3 extends ApplicationFrame {
//
//    /**
//     * Creates a new demo instance.
//     *
//     * @param title  the frame title.
//     */
//    public TimePeriodValuesDemo3(final String title) {
//
//        super(title);
//
//        final XYDataset data1 = createDataset();
//        final XYItemRenderer renderer1 = new XYBarRenderer();
//        
//        final DateAxis domainAxis = new DateAxis("Date");
//        final ValueAxis rangeAxis = new NumberAxis("Value");
//        
//        final XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);
//
//        final JFreeChart chart = new JFreeChart("Time Period Values Demo 3", plot);
//        final ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//        chartPanel.setMouseZoomable(true, false);
//        setContentPane(chartPanel);
//
//    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
//    public XYDataset createDataset() {
//
//        final TimePeriodValues s1 = new TimePeriodValues("Series 1");
//        
//        final DateFormat df = DateFormat.getInstance();
//        try {
//            final Date d0 = df.parse("11/5/2003 0:00:00.000");
//            final Date d1 = df.parse("11/5/2003 0:15:00.000");
//            final Date d2 = df.parse("11/5/2003 0:30:00.000");
//            final Date d3 = df.parse("11/5/2003 0:45:00.000");
//            final Date d4 = df.parse("11/5/2003 1:00:00.001");
//            final Date d5 = df.parse("11/5/2003 1:14:59.999");
//            final Date d6 = df.parse("11/5/2003 1:30:00.000");
//            final Date d7 = df.parse("11/5/2003 1:45:00.000");
//            final Date d8 = df.parse("11/5/2003 2:00:00.000");
//            final Date d9 = df.parse("11/5/2003 2:15:00.000");
//                
//            s1.add(new SimpleTimePeriod(d0, d1), 0.39);
//            //s1.add(new SimpleTimePeriod(d1, d2), 0.338);
//            s1.add(new SimpleTimePeriod(d2, d3), 0.225);
//            s1.add(new SimpleTimePeriod(d3, d4), 0.235);
//            s1.add(new SimpleTimePeriod(d4, d5), 0.238);
//            s1.add(new SimpleTimePeriod(d5, d6), 0.236);
//            s1.add(new SimpleTimePeriod(d6, d7), 0.25);
//            s1.add(new SimpleTimePeriod(d7, d8), 0.238);
//            s1.add(new SimpleTimePeriod(d8, d9), 0.215);
//        }
//        catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
//        dataset.addSeries(s1);
//        dataset.setDomainIsPointsInTime(false);
//
//        return dataset;
//
//    }

//    /**
//     * Starting point for the demonstration application.
//     *
//     * @param args  ignored.
//     */
//    public static void main(final String[] args) {
//
//        final TimePeriodValuesDemo3 demo = new TimePeriodValuesDemo3("Time Period Values Demo 3");
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//
//    }
//
//}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		XYDataset entlnDataset = readHistogram(conf.getEntlnFlashRateLayer());
		XYDataset nldnDataset = readHistogram(conf.getNldnFlashRateLayer());
		XYDataset gld360Dataset = readHistogram(conf.getGld360FlashRateLayer());
		XYDataset glmDataset = readHistogram(conf.getGlmFlashRateLayer());
        
		// reset data 
        entlnHist.getXYPlot().setDataset(entlnDataset);
        nldnHist.getXYPlot().setDataset(nldnDataset);
        gld360Hist.getXYPlot().setDataset(gld360Dataset);
        glmHist.getXYPlot().setDataset(glmDataset);
        
        // reset scaling of plots
        entlnHist.getXYPlot().getDomainAxis().setAutoRange(true);
        entlnHist.getXYPlot().getRangeAxis().setAutoRange(true);
        nldnHist.getXYPlot().getDomainAxis().setAutoRange(true);
        nldnHist.getXYPlot().getRangeAxis().setAutoRange(true);
        gld360Hist.getXYPlot().getDomainAxis().setAutoRange(true);
        gld360Hist.getXYPlot().getRangeAxis().setAutoRange(true);
        glmHist.getXYPlot().getDomainAxis().setAutoRange(true);
        glmHist.getXYPlot().getRangeAxis().setAutoRange(true);
        
        // reset titles
        entlnHist.setTitle("ENTLN Flash Rate " + flashesPer);
        nldnHist.setTitle("NLDN Flash Rate " + flashesPer);
        gld360Hist.setTitle("GLD360 Flash Rate " + flashesPer);
        glmHist.setTitle("GLM Flash Rate " + flashesPer);

	}
}