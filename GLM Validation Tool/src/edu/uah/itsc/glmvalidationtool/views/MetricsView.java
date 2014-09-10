package edu.uah.itsc.glmvalidationtool.views;


import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.jfree.data.Range;
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
import gov.nasa.worldwind.render.Renderable;
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
	private static boolean enableFlag=true;
	
	private Map<Long, XYDataset> entlnBuffer = new HashMap<Long, XYDataset>();
	private Map<Long, XYDataset> nldnBuffer = new HashMap<Long, XYDataset>();
	private Map<Long, XYDataset> gld360Buffer = new HashMap<Long, XYDataset>();
	private Map<Long, XYDataset> glmBuffer = new HashMap<Long, XYDataset>();
	
	private static Color entlnColor;
	private static Color nldnColor;
	private static Color gld360Color;
	private static Color glmColor;

	private ChartComposite chartFrame1;
	private ChartComposite chartFrame2;
	private ChartComposite chartFrame3;
	private ChartComposite chartFrame4;
	
	private int entlnMaxFrequency=0;
	private int nldnMaxFrequency=0;
	private int gld360MaxFrequency=0;
	private int glmMaxFrequency=0;
	
	private boolean updateRangeFlag = true;

	Action enableAction;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
    @Override
    public void dispose()
    {
    	super.dispose();
    	dataFilter.unregisterObject(this);
    }

	public void createPartControl(Composite comp) {
		
//		if (true) return;

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
		
		entlnColor = conf.getEntlnColor();
		nldnColor = conf.getNldnColor();
		gld360Color = conf.getGld360Color();
		glmColor = conf.getGlmColor();
        
//        entlnHist = createChart(entlnDataset, Color.CYAN, "ENTLN Flash Rate" + flashesPer);
//        nldnHist = createChart(nldnDataset, Color.BLUE, "NLDN Flash Rate" + flashesPer);
//        gld360Hist = createChart(gld360Dataset, Color.PINK, "GLD360 Flash Rate" + flashesPer);
//        glmHist = createChart(glmDataset, Color.MAGENTA, "GLM Flash Rate" + flashesPer);

        entlnHist = createChart(entlnDataset, entlnColor, "");
        nldnHist = createChart(nldnDataset, nldnColor, "");
        gld360Hist = createChart(gld360Dataset, gld360Color, "");
        glmHist = createChart(glmDataset, glmColor, "");
        entlnHist.setTitle("ENTLN Flash Rate " + flashesPer);
        nldnHist.setTitle("NLDN Flash Rate " + flashesPer);
        gld360Hist.setTitle("GLD360 Flash Rate " + flashesPer);
        glmHist.setTitle("GLM Flash Rate " + flashesPer);
       
        chartFrame1 = new ChartComposite(parent, SWT.NONE, entlnHist, true);
        chartFrame2 = new ChartComposite(parent, SWT.NONE, nldnHist, true);
        chartFrame3 = new ChartComposite(parent, SWT.NONE, gld360Hist, true);
        chartFrame4 = new ChartComposite(parent, SWT.NONE, glmHist, true);
        
       //chartFrame.setLayout(new RowLayout(SWT.VERTICAL));
        //chartFrame.setLayoutData(new RowData(512, 512));

        enableAction = new Action("Disable plot display") {
            public void run() {
                if (enableFlag) { // action is to disable
             		enableAction.setToolTipText("Enable plot display");
             		enableAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/icon_reg_disable_16x16.gif"));
                }
                else {
             		enableAction.setToolTipText("Disable plot display");
             		enableAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/icon_reg_enable_16x16.gif"));                	
                }
                enableFlag = !enableFlag;
           }
        };
//       drawBoxAction.setImageDescriptor(getImageDescriptor("rectangle.jpg"));
        enableAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/icon_reg_enable_16x16.gif"));
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(enableAction);

        
        dataFilter.registerObject(this); // register this object with filter update interface
        
        updateRange();
        refresh();
        
//		chartFrame1.addDisposeListener(new DisposeListener()
//		{
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				// TODO Auto-generated method stub
//				EventQueue.invokeLater(new Runnable () {
//					public void run () {
//						chartFrame1.dispose();
//					}
//				});
//			};
//		});
//		chartFrame2.addDisposeListener(new DisposeListener()
//		{
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				// TODO Auto-generated method stub
//				EventQueue.invokeLater(new Runnable () {
//					public void run () {
//						chartFrame2.dispose();
//					}
//				});
//			};
//		});
//		chartFrame3.addDisposeListener(new DisposeListener()
//		{
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				// TODO Auto-generated method stub
//				EventQueue.invokeLater(new Runnable () {
//					public void run () {
//						chartFrame3.dispose();
//					}
//				});
//			};
//		});
//		chartFrame4.addDisposeListener(new DisposeListener()
//		{
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				// TODO Auto-generated method stub
//				EventQueue.invokeLater(new Runnable () {
//					public void run () {
//						chartFrame4.dispose();
//					}
//				});
//			};
//		});

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
		ArrayList <Double> arr = new ArrayList<Double>();
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
	
	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	@Override
	public void refresh() {
//		// TODO Auto-generated method stub
		XYDataset entlnDataset = null;
		XYDataset nldnDataset = null;
		XYDataset gld360Dataset = null;
		XYDataset glmDataset = null;
		
		if (updateRangeFlag) {
			updateRange();
		}
	
		if (enableFlag) {
			if (entlnBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
				entlnDataset = readHistogram(conf.getEntlnFlashRateLayer());
				entlnBuffer.put(dataFilter.getCurrentTimeMilli(), entlnDataset);
			}
			else {
				entlnDataset = entlnBuffer.get(dataFilter.getCurrentTimeMilli());
			}
			if (nldnBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
				nldnDataset = readHistogram(conf.getNldnFlashRateLayer());
				nldnBuffer.put(dataFilter.getCurrentTimeMilli(), nldnDataset);
			}
			else {
				nldnDataset = nldnBuffer.get(dataFilter.getCurrentTimeMilli());
			}
			if (gld360Buffer.get(dataFilter.getCurrentTimeMilli())==null) {
				gld360Dataset = readHistogram(conf.getGld360FlashRateLayer());
				gld360Buffer.put(dataFilter.getCurrentTimeMilli(), gld360Dataset);
			}
			else {
				gld360Dataset = gld360Buffer.get(dataFilter.getCurrentTimeMilli());
			}
			if (glmBuffer.get(dataFilter.getCurrentTimeMilli())==null) {
				glmDataset = readHistogram(conf.getGlmFlashRateLayer());
				glmBuffer.put(dataFilter.getCurrentTimeMilli(), glmDataset);
			}
			else {
				glmDataset = glmBuffer.get(dataFilter.getCurrentTimeMilli());
//				TimePeriodValuesCollection timeVal = (TimePeriodValuesCollection) glmDataset;
//				timeVal.getItemCount(series)
			}
//			nldnDataset = readHistogram(conf.getNldnFlashRateLayer());
//			gld360Dataset = readHistogram(conf.getGld360FlashRateLayer());
//			glmDataset = readHistogram(conf.getGlmFlashRateLayer());
	        
		}
		
		// reset data 
        entlnHist.getXYPlot().setDataset(entlnDataset);
        nldnHist.getXYPlot().setDataset(nldnDataset);
        gld360Hist.getXYPlot().setDataset(gld360Dataset);
        glmHist.getXYPlot().setDataset(glmDataset);
        
        // TODO update this based on precomputed  max frequencies
        // reset scaling of plots
        
        
//		entlnMaxFrequency=0;
//		nldnMaxFrequency=0;
//		gld360MaxFrequency=0;
//		glmMaxFrequency=0;

        entlnHist.getXYPlot().getDomainAxis().setAutoRange(true);      
        entlnHist.getXYPlot().getDomainAxis().setRange(new Range(0, entlnMaxFrequency));
        if (entlnMaxFrequency>0)
        	entlnHist.getXYPlot().getRangeAxis().setRange(new Range(0, entlnMaxFrequency));
        else
        	entlnHist.getXYPlot().getRangeAxis().setAutoRange(true);
        nldnHist.getXYPlot().getDomainAxis().setAutoRange(true);
        if (nldnMaxFrequency>0)
        	nldnHist.getXYPlot().getRangeAxis().setRange(new Range(0, nldnMaxFrequency));
        else
        	nldnHist.getXYPlot().getRangeAxis().setAutoRange(true);
        gld360Hist.getXYPlot().getDomainAxis().setAutoRange(true);
        if (gld360MaxFrequency>0)
        	gld360Hist.getXYPlot().getRangeAxis().setRange(new Range(0, gld360MaxFrequency));
        else
        	gld360Hist.getXYPlot().getRangeAxis().setAutoRange(true);
        glmHist.getXYPlot().getDomainAxis().setAutoRange(true);
        if (glmMaxFrequency>0)
        	glmHist.getXYPlot().getRangeAxis().setRange(new Range(0, glmMaxFrequency));
        else
        	glmHist.getXYPlot().getRangeAxis().setAutoRange(true);
        
        // reset titles
        entlnHist.setTitle("ENTLN Flash Rate " + flashesPer);
        nldnHist.setTitle("NLDN Flash Rate " + flashesPer);
        gld360Hist.setTitle("GLD360 Flash Rate " + flashesPer);
        glmHist.setTitle("GLM Flash Rate " + flashesPer);

	}

	public void updateRange()
	{
		entlnMaxFrequency=readMaxFrequency(conf.getEntlnMaxFlashRateLayer());
		nldnMaxFrequency=readMaxFrequency(conf.getNldnMaxFlashRateLayer());
		gld360MaxFrequency=readMaxFrequency(conf.getGld360MaxFlashRateLayer());
		glmMaxFrequency=readMaxFrequency(conf.getGlmMaxFlashRateLayer());
	
		updateRangeFlag=false;
	}
	int readMaxFrequency(String layer) 
	{
		String timeFormat=null;
		String appendTime = "";
		int maxFreq=0;
		// within 5 minutes, use seconds
//		if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 300000) {
		if (dataFilter.getDisplayIntervalMilli() < 300000) {
			timeFormat = "'YYYY-MM-DD HH24:MI:SS TZ'";
		}
		// 5 hours 
//		else if ((dataFilter.getEndTimeMilli() - dataFilter.getStartTimeMilli()) < 18000000) {
		else if (dataFilter.getDisplayIntervalMilli() < 18000000) {
			timeFormat = "'YYYY-MM-DD HH24:MI TZ'";
		}
		else  {
			timeFormat = "'YYYY-MM-DD HH24 TZ'";
		}
		
		// need new protocol string for start - end time intervals
		
		String encodedTimeFormat = URLEncoder.encode(";time_format:" + timeFormat);
		String httpString = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceStringCsv() + layer + "&" + dataFilter.getValidationParamString(dataFilter.getAnimationStartTimeMilli(), dataFilter.getAnimationEndTimeMilli()) + encodedTimeFormat; 
        System.out.println(httpString);
        
        URL url=null;
		try {
			url = new URL(httpString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        BufferedReader in=null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        String inputLine;
        try {
			if ((inputLine = in.readLine()) != null) {
				inputLine = in.readLine();
				String [] strBuf = inputLine.split(",");
				System.out.println("layer " + layer + " max freq line:  " + inputLine);
				maxFreq = Integer.parseInt(strBuf[1]);               
			}
		    in.close();			 
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return maxFreq;
	}
	
	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		entlnBuffer.clear();
		nldnBuffer.clear();
		gld360Buffer.clear();
		glmBuffer.clear();
		
		entlnMaxFrequency=0;
		nldnMaxFrequency=0;
		gld360MaxFrequency=0;
		glmMaxFrequency=0;
		
		updateRangeFlag = true;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
		// use this to reset max frequency ranges, call from timeline view
		updateRangeFlag = true;
		
	}
}