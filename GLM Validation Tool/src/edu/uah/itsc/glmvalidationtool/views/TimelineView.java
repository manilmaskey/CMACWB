package edu.uah.itsc.glmvalidationtool.views;


//import java.text.DateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.Iterator;
//import java.util.List;
//
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.DateTime;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.part.*;
//import org.eclipse.jface.viewers.*;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.RowLayout;
//import org.eclipse.jface.action.*;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.ui.*;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.SWT;
//import org.eclipse.nebula.widgets.ganttchart.GanttChart;
//import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
//import org.eclipse.nebula.widgets.datechooser.DateChooser;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Format;
import javax.media.MediaLocator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.views.ViewsPlugin;
import org.eclipse.ui.part.ViewPart;
import org.jfree.data.time.SimpleTimePeriod;

import edu.uah.itsc.glmvalidationtool.config.Config;
import edu.uah.itsc.glmvalidationtool.data.DataFilter;
import edu.uah.itsc.glmvalidationtool.data.DataFilterUpdate;
import gov.nasa.worldwind.geom.Sector;


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

//public class TimelineView extends ViewPart implements DataFilterUpdate, WWEventListener{
	public class TimelineView extends ViewPart implements WWEventListener{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "TimelineView";
//	static DateChooser cal;
//	static List selectedDates;
//	static DateFormat df;
	private CDateTime cdtCurrent, cdtDisplayInterval;
	private CDateTime cdtAnimationStart, cdtAnimationEnd;
	private static DataFilter dataFilter = new DataFilter();
	private WWEvent wwEvent = new WWEvent();
    private Sector selectedSector = null;
//    private Config conf = new Config();
    
    private static Text timeRangeText;
    private Scale scale;
    private Animator animator;

    Action recordAction, playAction, drawBoxAction, refreshAction, clearAction;
    boolean playFlag=false;
    boolean recordFlag=false;
    
    Dimension screenSize;
    Dimension movieSize;
    Robot robot;
    JpegImagesToMovie movie;
    Vector <BufferedImage> imgBuffer = new Vector<BufferedImage>();
    
    URL saveFile;
	private ScrolledComposite scrolledComposite = null;
	private Composite composite = null;
    
    //stopAction, 
	/**
	 * The constructor.
	 */
	public TimelineView() {
		WWEvent.registerListener(this);
	}
    @Override
    public void dispose()
    {
    	super.dispose();
    	dataFilter.unregisterObject(this);
    }

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
 
//		parent.setLayout(new FillLayout(SWT.VERTICAL));

		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		true, true, 1, 1));
		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
//		scrolledComposite.setLayoutData(new FillLayout(SWT.VERTICAL));
		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(new Point(500, 200));
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		
//		dataFilter.registerObject(this);

        dataFilter.setDisplayInterval(0, 1, 0);
        dataFilter.setBoundingBox(-92.0, -82.5, 32.0, 38.5);

        
		refreshTimeRange();
			
		Group dateAnimationGroup = new Group(composite, SWT.SHADOW_NONE);
		dateAnimationGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// TODO need to make this configurable
//		long startTime = dataFilter.getDataEndTimeMilli()-1000*60*5;  // last 5 minutes
		long startTime = dataFilter.getDataEndTimeMilli()-Long.parseLong(dataFilter.getConfig().getAnimationTimePeriod());  
		dataFilter.setCurrentTime(startTime);
		
		cal.setTimeInMillis(startTime);
		cdtAnimationStart = new CDateTime(dateAnimationGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
//	    cdtAnimationStart.setPattern("'Starting Time' EEEE, MMMM d '@' hh:mm:ss 'GMT'");
	    cdtAnimationStart.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtAnimationStart.setPattern("'Starting Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtAnimationStart.setPattern("'Start Time:' MM/dd/yyyy HH:mm:ss ");
//		cdtAnimationStart.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		cdtAnimationStart.setSelection(cal.getTime());
	    cdtAnimationStart.addSelectionListener(new SelectionListener() {
	    	@Override
			public void widgetDefaultSelected(SelectionEvent event) {
	    		cdtAnimationStartSelected();
	    	}
			@Override
			public void widgetSelected(SelectionEvent event) {
				cdtAnimationStartSelected();
			}
	    });
	    cal.setTimeInMillis(dataFilter.getDataEndTimeMilli());
	    cdtAnimationEnd = new CDateTime(dateAnimationGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
//	    cdtAnimationEnd.setPattern("'Starting Time' EEEE, MMMM d '@' hh:mm:ss 'GMT'");
	    cdtAnimationEnd.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtAnimationEnd.setPattern("'Starting Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtAnimationEnd.setPattern("'End Time:' MM/dd/yyyy HH:mm:ss ");
//		cdtAnimationEnd.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		cdtAnimationEnd.setSelection(cal.getTime());
	    cdtAnimationEnd.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				cdtAnimationEndSelected();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				cdtAnimationEndSelected();
			}
	    });
		
		
//	    scale = new Scale (parent, SWT.BORDER);
	    scale = new Scale (composite, SWT.NONE);
		Rectangle clientArea = composite.getClientArea ();
		scale.setBounds (clientArea.x, clientArea.y, 200, 64);
		scale.setMaximum ((int)(cdtAnimationEnd.getSelection().getTime() - cdtAnimationStart.getSelection().getTime()));
		scale.setPageIncrement ((int)dataFilter.getDisplayIntervalMilli());

		scale.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				scaleSelected();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				scaleSelected();				
			}
		});
		scale.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				dataFilter.refreshObjects();
				
			}
			
		} );
		
		
		
		Group dateGroup = new Group(composite, SWT.SHADOW_NONE);
		dateGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		cal.setTimeInMillis(dataFilter.getCurrentTimeMilli());
	    cdtCurrent = new CDateTime(dateGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
//	    cdtCurrent.setPattern("'Starting Time' EEEE, MMMM d '@' hh:mm:ss 'GMT'");
	    cdtCurrent.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtCurrent.setPattern("'Starting Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtCurrent.setPattern("'Current Time:' MM/dd/yyyy HH:mm:ss ");
//		cdtCurrent.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		cdtCurrent.setSelection(cal.getTime());
		cdtCurrent.setEnabled(false);
//	    cdtCurrent.addSelectionListener(new SelectionListener() {
//			@Override
//				public void widgetDefaultSelected(SelectionEvent event) {
//					// set data filter value
//					dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
//				}
//
//			@Override
//				public void widgetSelected(SelectionEvent event) {
//					System.out.println("Start time: " + cdtCurrent.getSelection());
//					dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
//				}
//
//	    });
		cal.setTimeInMillis(dataFilter.getDisplayIntervalMilli());
	    cdtDisplayInterval = new CDateTime(dateGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
	    cdtDisplayInterval.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtDisplayInterval.setPattern("'Ending Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtDisplayInterval.setPattern("'Interval:' HH:mm:ss ");
//	    cdtDisplayInterval.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    cdtDisplayInterval.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
	    cdtDisplayInterval.setSelection(cal.getTime());
	    cdtDisplayInterval.addSelectionListener(new SelectionListener() {
			@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// set data filter value
					dataFilter.setDisplayInterval(cdtDisplayInterval.getSelection().getHours(), cdtDisplayInterval.getSelection().getMinutes(), cdtDisplayInterval.getSelection().getSeconds());
					dataFilter.clearCache(); // clear cache if interval changes, any data in cache will be invalid
					resetScale();
				}

			@Override
				public void widgetSelected(SelectionEvent event) {
					System.out.println("DisplayInterval: " + cdtDisplayInterval.getSelection().getHours() + ":" + cdtDisplayInterval.getSelection().getMinutes() + ":" + cdtDisplayInterval.getSelection().getSeconds());
					dataFilter.setDisplayInterval(cdtDisplayInterval.getSelection().getHours(), cdtDisplayInterval.getSelection().getMinutes(), cdtDisplayInterval.getSelection().getSeconds());
					dataFilter.clearCache(); // clear cache if interval changes, any data in cache will be invalid
					resetScale();
				}

	    });

		timeRangeText = new Text(composite, SWT.CENTER|SWT.MULTI|SWT.WRAP);
		Date startDate = new Date(dataFilter.getDataStartTimeMilli());
		Date endDate = new Date(dataFilter.getDataEndTimeMilli());
		
		
		// this will need to be updated if refreshtimerange is called
		timeRangeText.setText("Data Available: "+ startDate.toString() + " -> " + endDate.toString());

		
		createActions();
	    createToolbar();
	    
	    resetAnimationDateRange();

	    dataFilter.refreshObjects();
	    
	    try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//	    screenSize = new Dimension(640,480);
	    	   	 
	}
	private void cdtAnimationStartSelected()
	{
		System.out.println("Start time: " + cdtAnimationStart.getSelection());
		if ((cdtAnimationStart.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
			cdtAnimationStart.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
		if ((cdtAnimationStart.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
			cdtAnimationStart.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
		if (cdtAnimationStart.getSelection().getTime()>=cdtAnimationEnd.getSelection().getTime())
			cdtAnimationStart.setSelection(new Date (cdtAnimationEnd.getSelection().getTime()-dataFilter.getDisplayIntervalMilli()));
//		if (cdtCurrent.getSelection().getTime()<cdtAnimationStart.getSelection().getTime())
//			cdtCurrent.setSelection(cdtAnimationStart.getSelection());
		resetScale();
		resetAnimationDateRange();
	}
	private void cdtAnimationEndSelected()
	{
		System.out.println("End time: " + cdtAnimationEnd.getSelection());
		if ((cdtAnimationEnd.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
			cdtAnimationEnd.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
		if ((cdtAnimationEnd.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
			cdtAnimationEnd.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
		if (cdtAnimationEnd.getSelection().getTime()<=cdtAnimationStart.getSelection().getTime())
			cdtAnimationEnd.setSelection(new Date (cdtAnimationStart.getSelection().getTime()+dataFilter.getDisplayIntervalMilli()));
//		if (cdtCurrent.getSelection().getTime()>cdtAnimationEnd.getSelection().getTime())
//			cdtCurrent.setSelection(new Date(cdtAnimationEnd.getSelection().getTime()));
		resetScale();	
		resetAnimationDateRange();
	}
	private void scaleSelected()
	{
		// set selection to nearest interval
		int position = scale.getSelection();

		int nearestTick = (position + (int)dataFilter.getDisplayIntervalMilli()/2) / (int)dataFilter.getDisplayIntervalMilli();
		int nearestTickPosition = nearestTick * (int)dataFilter.getDisplayIntervalMilli();
		scale.setSelection(nearestTickPosition);
		
//		cdtCurrent.setSelection(new Date(cdtAnimationStart.getSelection().getTime() + position));
		cdtCurrent.setSelection(new Date(cdtAnimationStart.getSelection().getTime() + nearestTickPosition));
		dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());

	}
	private void resetScale()
	{
		scale.setMaximum ((int)(cdtAnimationEnd.getSelection().getTime() - cdtAnimationStart.getSelection().getTime()));
		scale.setPageIncrement ((int)dataFilter.getDisplayIntervalMilli());
		scale.setSelection((int)(cdtCurrent.getSelection().getTime()-cdtAnimationStart.getSelection().getTime()));    

	}
	private void resetAnimationDateRange()
	{
		dataFilter.setAnimationStartTime(cdtAnimationStart.getSelection().getTime());
		dataFilter.setAnimationEndTime(cdtAnimationEnd.getSelection().getTime());
		dataFilter.reset();
		
	}
	

    public void createActions() {
           playAction = new Action("Play Animation") {
                public void run() { 
                		if (playFlag) {
                			playAction.setToolTipText("Play Animation");
                			playAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/nav_go.gif"));
                			StopAnimation();
                		}
                		else {
                			playAction.setToolTipText("Stop Animation");
                			playAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/suspend_co.gif"));
                			StartAnimation();               			
                		}
                		playFlag=!playFlag;
                    }
           };
           playAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/nav_go.gif"));
//           playAction.setImageDescriptor(getImageDescriptor("nav_go.gif"));

           recordAction = new Action("Record Animation") {
               public void run() { 
               		if (recordFlag) {
               			recordAction.setToolTipText("Record Animation");
               			recordAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/button_play_red_16x16.png"));
               			StopRecordAnimation();
               		}
               		else {
               			recordAction.setToolTipText("Stop Recording Animation");
               			recordAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/button_stop_red_16x16.png"));
               			RecordAnimation();               			
               		}
               		recordFlag=!recordFlag;
                   }
          };
          recordAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/button_play_red_16x16.png"));
 //         recordAction.setImageDescriptor(getImageDescriptor("button_play_red.png"));

//           stopAction = new Action("Stop Animation") {
//                   public void run() {
//                           StopAnimation();
//                   }
//           };
//           stopAction.setImageDescriptor(getImageDescriptor("suspend_co.gif"));

           drawBoxAction = new Action("Draw Bounding Box") {
                   public void run() {
                        DrawBox();
                   }
           };
 //          drawBoxAction.setImageDescriptor(getImageDescriptor("rectangle.jpg"));
           
           drawBoxAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/draw-rectangle.png"));
 //          drawBoxAction.setImageDescriptor(getImageDescriptor("draw-rectangle.png"));

           clearAction = new Action("Clear Drawn Bounding Box") {
               public void run() {
                        ClearBox();
              }
           };
//          drawBoxAction.setImageDescriptor(getImageDescriptor("rectangle.jpg"));
 
          clearAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/delete_edit.gif"));
//          clearAction.setImageDescriptor(getImageDescriptor("delete_edit.gif"));

           
           refreshAction = new Action("Apply bounding box and refresh display using current time") {
               public void run() {
                        DrawCurrent();
              }
           };
           refreshAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/nav_refresh.gif"));
//           refreshAction.setImageDescriptor(getImageDescriptor("nav_refresh.gif"));
           
   }
    private void StartAnimation()
    {
    	// check current time, if between start and end, start animation
    	
    	
    	animator.start();
    	
    }
    private void StopAnimation()
    {
    	animator.stop();
    }
    
    private void RecordAnimation()
    {
    	// check current time, if between start and end, start animation
    	
    	
    	// create new MovieEncoder
//		BufferedImage img = robot.createScreenCapture(new java.awt.Rectangle(screenSize));
//		BufferedImage img = robot.createScreenCapture(new java.awt.Rectangle(new Dimension(640,480)));
		
//		try {
//			ImageIO.write(img, "JPG", new File("screen.jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
////			saveFile=new URL("movie.mov");
//			saveFile=new URL("file://Users/Todd/movie.mov");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
    	// look for navigator window
    	
    	
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		FileDialog fd = new  FileDialog(win.getShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[]{"*.mov"});
		fd.setFilterNames(new String[]{"Quicktime movie files"});
		String movieFilename = fd.open();
		if (movieFilename == null) {
			recordFlag=!recordFlag;
   			recordAction.setToolTipText("Record Animation");
   			recordAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/button_play_red_16x16.png"));
			return;
		}
		System.out.println("movie filename " + movieFilename);
		
		if (!movieFilename.endsWith(".mov")) {
			movieFilename.concat(".mov");
		}
    	try {
			saveFile = new URL( "file://" + movieFilename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		System.out.println("Url " + saveFile);
    	
    	movie = new JpegImagesToMovie();

		// reset to start time
        dataFilter.setCurrentTime(cdtAnimationStart.getSelection().getTime());
        refreshWidgets();
    	dataFilter.refreshObjects();

		animator.start();
    	
    }
    private void StopRecordAnimation()
    {
    	animator.stop();
    	// write out buffered movie
    	
    }
    
    private void DrawBox()
    {
		wwEvent.enableSelectors();
		dataFilter.clearCache();
		selectedSector = null;    	
//		cdtCurrent.setEnabled(false);
		cdtDisplayInterval.setEnabled(false);
		cdtAnimationStart.setEnabled(false);
		cdtAnimationEnd.setEnabled(false);
		scale.setEnabled(false);
		drawBoxAction.setEnabled(false);
    }
    private void ClearBox()
    {
    	wwEvent.disableSelectors();	
//   		cdtCurrent.setEnabled(true);
		cdtDisplayInterval.setEnabled(true);
		cdtAnimationStart.setEnabled(true);
		cdtAnimationEnd.setEnabled(true);
	    scale.setEnabled(true);
    }
    private void DrawCurrent()
    {
    	if (selectedSector!=null) {	    		
      	  	dataFilter.setBoundingBox(selectedSector.getMinLongitude().degrees, selectedSector.getMaxLongitude().degrees, selectedSector.getMinLatitude().degrees, selectedSector.getMaxLatitude().degrees);
    		selectedSector=null;
	    	wwEvent.disableSelectors();
    	}
        dataFilter.refreshObjects();
        
//   		cdtCurrent.setEnabled(true);
		cdtDisplayInterval.setEnabled(true);
		cdtAnimationStart.setEnabled(true);
		cdtAnimationEnd.setEnabled(true);
	    scale.setEnabled(true);
   }
    /**
     * Create toolbar.
     */
    private void createToolbar() {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            mgr.add(recordAction);
            mgr.add(playAction);
 //           mgr.add(stopAction);
            mgr.add(drawBoxAction);
            mgr.add(clearAction);
            mgr.add(refreshAction);
            
            animator = new Animator(1000);
    }

 
  /**
     * Returns the image descriptor with the given relative path.
     */
//    private ImageDescriptor getImageDescriptor(String relativePath) {
//            String iconPath = "icons/";
////            try {
//            	return ImageDescriptor.createFromFile(this.getClass(), "/icons/"+relativePath);
////            	InputStream input = getClass().getResourceAsStream("/icons/histogram-16x16.gif");  
////            	input.
////            	ViewsPlugin plugin = ViewsPlugin.getDefault();
////                URL installURL = plugin..getDescriptor().getInstallURL();
////                URL url = new URL(installURL, iconPath + relativePath);
////                return ImageDescriptor..createFromURL(url);             
////            }
////            catch (MalformedURLException e) {
////                    // should not happen
////                    return ImageDescriptor.getMissingImageDescriptor();
////            }
//    }
    void refreshTimeRange()
    {
		MaxMin maxmin = new MaxMin();
		long dateMax, dateMin;
		getDataDateRange(maxmin, dataFilter.getConfig().getEntlnDateRangeLayer()); 
		dateMax = maxmin.getMax();
		dateMin = maxmin.getMin();
		getDataDateRange(maxmin, dataFilter.getConfig().getNldnDateRangeLayer()); 
		dateMax = Math.max(dateMax, maxmin.getMax());
		dateMin = Math.min(dateMin, maxmin.getMin());
		getDataDateRange(maxmin, dataFilter.getConfig().getGld360DateRangeLayer()); 
		dateMax = Math.max(dateMax, maxmin.getMax());
		dateMin = Math.min(dateMin, maxmin.getMin());
		getDataDateRange(maxmin, dataFilter.getConfig().getGlmDateRangeLayer()); 
		dateMax = Math.max(dateMax, maxmin.getMax());
		dateMin = Math.min(dateMin, maxmin.getMin());

		dataFilter.setDataStartTime(dateMin);
		dataFilter.setDataEndTime(dateMax);
		Calendar start = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		start.setTimeInMillis(dateMin);
		end.setTimeInMillis(dateMax);
		System.out.println("Data from " + new Date(start.getTimeInMillis()).toString() + " to " + new Date(end.getTimeInMillis()).toString());
    }
    private class MaxMin 
    {
		long max,min;
    	
    	public long getMax() {
			return max;
		}

		public void setMax(long max) {
			this.max = max;
		}

		public long getMin() {
			return min;
		}

		public void setMin(long min) {
			this.min = min;
		}

    }
    
    void getDataDateRange(MaxMin maxmin, String layer)
    {
		String httpString = dataFilter.getConfig().getProtocolHttp() + dataFilter.getConfig().getServerIP() + ":" + dataFilter.getConfig().getServerPort() + dataFilter.getConfig().getServiceStringCsv() + layer; 
        System.out.println(httpString);
        
        try {
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
			    String minTime = fields[1].trim();
			    String maxTime = fields[2].trim();
			    
			    
			    System.out.println("minTime " + minTime + " maxTime " + maxTime);
//	            DateFormat df = DateFormat.getInstance();                       
//	            Date d0 = df.parse(minTime);
//	            min = d0.getTime();
//	            d0 = df.parse(maxTime);
//	            max = d0.getTime();
	            
			    Date minDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(minTime);
			    Date maxDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(maxTime);
			    maxmin.setMin(minDate.getTime());
			    maxmin.setMax(maxDate.getTime());
			    System.out.println("parsed minTime " + minDate.toString() + " maxTime " + maxDate.toString());
	            
//			    Timestamp ts = Timestamp.valueOf(minTime);
//			    min = ts.getTime();
//			    ts = Timestamp.valueOf(maxTime);
//			    max = ts.getTime();
			    
			}
			in.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		
    }
    //
    private void takeScreenShot()
    {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		Rectangle rect = win.getShell().getBounds();
		System.out.println("rectangle " + rect);
		BufferedImage img = robot.createScreenCapture(new java.awt.Rectangle(screenSize));
//		imgBuffer.add(img);
		BufferedImage subImg = img.getSubimage(rect.x, rect.y, rect.width, rect.height);
		imgBuffer.add(subImg);

		
//		movieSize = new Dimension(640,480);
////		movieSize = new Dimension(1280,720);
//
//		float xscale = (float)movieSize.width/(float)subImg.getWidth();
//		float yscale = (float)movieSize.height/(float)subImg.getHeight();
//		float scale = Math.min(xscale, yscale);
//		BufferedImage after = new BufferedImage(movieSize.width, movieSize.height, subImg.getType());
//		AffineTransform at = new AffineTransform();
//		
//		at.scale(scale, scale);
//		AffineTransformOp scaleOp = 
//		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//		after = scaleOp.filter(subImg, after);
////		
//////		Image scaledImg=subImg.getScaledInstance(640, -1, Image.SCALE_SMOOTH);
//		imgBuffer.add(after);
    	
    }
  private class Animator
  {
      int delay = 1000;
      Runnable timer;
      boolean displayCurrent=false;
      final Display display = Display.getCurrent();

      private Animator(int interval)
      {
    	  this.delay = interval;
          this.timer = new Runnable()
          {
			@Override
			public void run() {
				System.out.println("animator run");
				// TODO Auto-generated method stub
				   // display current time on first timer tick
			    if (displayCurrent) {
			    	dataFilter.refreshObjects();
			    	displayCurrent=false;
			    	if (recordFlag) {
			    		takeScreenShot();
			    	}
			    	display.timerExec(delay, this);
			    }

			   // increment by interval
		        dataFilter.setCurrentTime(dataFilter.getCurrentTimeMilli()+dataFilter.getDisplayIntervalMilli());
		        // wrap around to start and loop if greater than animation end time
		        if (dataFilter.getCurrentTimeMilli()>cdtAnimationEnd.getSelection().getTime()) {
		        	dataFilter.setCurrentTime(cdtAnimationStart.getSelection().getTime());
		        }
		        // set scale and current cdt 
//				scale.setSelection((int)(dataFilter.getCurrentTimeMilli()-cdtAnimationStart.getSelection().getTime()));
//				cdtCurrent.setSelection(new Date(dataFilter.getCurrentTimeMilli()));

		        refreshWidgets();
		    	dataFilter.refreshObjects();
		    	
		    	if (recordFlag) {
		    		takeScreenShot();
		    	}
		    	display.timerExec(delay, this);
			}

          };
      }

      private void stop()
      {
    	  // re-enable other controls
    	  
		System.out.println("animator stop");
//		cdtCurrent.setEnabled(true);
		cdtDisplayInterval.setEnabled(true);
		cdtAnimationStart.setEnabled(true);
		cdtAnimationEnd.setEnabled(true);
		scale.setEnabled(true);
		drawBoxAction.setEnabled(true);
		clearAction.setEnabled(true);
		refreshAction.setEnabled(true);
		display.timerExec(-1, this.timer);
		if (recordFlag) {
			
			movie.doIt(imgBuffer.get(0).getWidth(), imgBuffer.get(0).getHeight(), 1, imgBuffer, new MediaLocator(saveFile));
//			movie.doIt(640,480, 1, imgBuffer, new MediaLocator(saveFile));
		}
		
//		timer.stop();
      }

      private void start()
      {
    	  // disable other controls, only enable stop animation button
    	  
  		System.out.println("animator start");

    	//		cdtCurrent.setEnabled(false);
		cdtDisplayInterval.setEnabled(false);
		cdtAnimationStart.setEnabled(false);
		cdtAnimationEnd.setEnabled(false);
	    scale.setEnabled(false);
		drawBoxAction.setEnabled(false);
		clearAction.setEnabled(false);
		refreshAction.setEnabled(false);
		displayCurrent = true;
    	imgBuffer.clear();
    	
    	display.timerExec(delay, this.timer);
		
//        timer.start();
        
      }
  }
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	public void refreshWidgets() {
		// TODO Auto-generated method stub
		System.out.println("TimelineView.refresh");
		if ((cdtCurrent==null)||(cdtDisplayInterval==null)||cdtCurrent.isDisposed() || cdtDisplayInterval.isDisposed()) {
			System.err.println("Warning: widget disposed");
			return;
		}
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(dataFilter.getCurrentTimeMilli());
		cdtCurrent.setSelection(cal.getTime());		
//		cal.setTimeInMillis(dataFilter.getDisplayIntervalMilli());
//		cdtDisplayInterval.setSelection(cal.getTime());	
		resetScale();
		
//		Date startDate = new Date(dataFilter.getDataStartTimeMilli());
//		Date endDate = new Date(dataFilter.getDataEndTimeMilli());
	}

//	@Override
//	public void refresh() {
//		// TODO Auto-generated method stub
//		System.out.println("TimelineView.refresh");
//		if ((cdtCurrent==null)||(cdtDisplayInterval==null)||cdtCurrent.isDisposed() || cdtDisplayInterval.isDisposed()) {
//			System.err.println("Warning: widget disposed");
//			return;
//		}
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//		cal.setTimeInMillis(dataFilter.getCurrentTimeMilli());
//		cdtCurrent.setSelection(cal.getTime());		
//		cal.setTimeInMillis(dataFilter.getDisplayIntervalMilli());
//		cdtDisplayInterval.setSelection(cal.getTime());	
//		resetScale();
//		
////		Date startDate = new Date(dataFilter.getDataStartTimeMilli());
////		Date endDate = new Date(dataFilter.getDataEndTimeMilli());
//	}

	@Override
	public void sectorChanged(Sector sector) {
		// TODO Auto-generated method stub
		selectedSector = sector;
		
//  	  	wwEvent.disableSelectors();
//  	  	if (sector==null) return;
//  	  	dataFilter.setBoundingBox(sector.getMinLongitude().degrees, sector.getMaxLongitude().degrees, sector.getMinLatitude().degrees, sector.getMaxLatitude().degrees);
//  	  	dataFilter.refreshObjects();

	}

//	@Override
//	public void clearCache() {
//		// TODO Auto-generated method stub
//		
//	}

}