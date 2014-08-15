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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
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

public class TimelineView extends ViewPart implements DataFilterUpdate, WWEventListener{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "TimelineView";
//	static DateChooser cal;
//	static List selectedDates;
//	static DateFormat df;
	private CDateTime cdtCurrent, cdtDisplayInterval;
	private CDateTime cdtAnimationStart, cdtAnimationEnd;
	private static final DataFilter dataFilter = new DataFilter();
	private WWEvent wwEvent = new WWEvent();
    private Sector selectedSector = null;
    private Config conf = new Config();
    
    private static Text timeRangeText;
    private Scale scale;
    private Animator animator;

    Action playAction, drawBoxAction, stopAction, refreshAction, clearAction;

	/**
	 * The constructor.
	 */
	public TimelineView() {
		WWEvent.registerListener(this);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		


 
//		GridLayout grid = new GridLayout();
//		parent.setLayout(grid);

		parent.setLayout(new FillLayout(SWT.VERTICAL));

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		
		dataFilter.registerObject(this);

        dataFilter.setDisplayInterval(0, 1, 0);
        dataFilter.setBoundingBox(-92.0, -82.5, 32.0, 38.5);

        
		refreshTimeRange();
			
		Group dateAnimationGroup = new Group(parent, SWT.SHADOW_NONE);
		dateAnimationGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// TODO need to make this configurable
		long startTime = dataFilter.getDataEndTimeMilli()-1000*60*5;  // last 5 minutes
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
			System.out.println("Start time: " + cdtAnimationStart.getSelection());
			if ((cdtAnimationStart.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
				cdtAnimationStart.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
			if ((cdtAnimationStart.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
				cdtAnimationStart.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
			if (cdtAnimationStart.getSelection().getTime()>=cdtAnimationEnd.getSelection().getTime())
				cdtAnimationStart.setSelection(new Date (cdtAnimationEnd.getSelection().getTime()-dataFilter.getDisplayIntervalMilli()));
			if (cdtCurrent.getSelection().getTime()<cdtAnimationStart.getSelection().getTime())
				cdtCurrent.setSelection(cdtAnimationStart.getSelection());	
			resetScale();
	    	}

			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Start time: " + cdtAnimationStart.getSelection());
				if ((cdtAnimationStart.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
					cdtAnimationStart.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
				if ((cdtAnimationStart.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
					cdtAnimationStart.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
				if (cdtAnimationStart.getSelection().getTime()>=cdtAnimationEnd.getSelection().getTime())
					cdtAnimationStart.setSelection(new Date (cdtAnimationEnd.getSelection().getTime()-dataFilter.getDisplayIntervalMilli()));
				if (cdtCurrent.getSelection().getTime()<cdtAnimationStart.getSelection().getTime())
					cdtCurrent.setSelection(cdtAnimationStart.getSelection());
				resetScale();
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
				System.out.println("End time: " + cdtAnimationEnd.getSelection());
				if ((cdtAnimationEnd.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
					cdtAnimationEnd.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
				if ((cdtAnimationEnd.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
					cdtAnimationEnd.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
				if (cdtAnimationEnd.getSelection().getTime()<=cdtAnimationStart.getSelection().getTime())
					cdtAnimationEnd.setSelection(new Date (cdtAnimationStart.getSelection().getTime()+dataFilter.getDisplayIntervalMilli()));
				if (cdtCurrent.getSelection().getTime()>cdtAnimationEnd.getSelection().getTime())
					cdtCurrent.setSelection(new Date(cdtAnimationEnd.getSelection().getTime()));
				resetScale();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("End time: " + cdtAnimationEnd.getSelection());
				if ((cdtAnimationEnd.getSelection().getTime()>dataFilter.getDataEndTimeMilli()))
					cdtAnimationEnd.setSelection(new Date(dataFilter.getDataEndTimeMilli()));
				if ((cdtAnimationEnd.getSelection().getTime()<dataFilter.getDataStartTimeMilli()))
					cdtAnimationEnd.setSelection(new Date(dataFilter.getDataStartTimeMilli()));
				if (cdtAnimationEnd.getSelection().getTime()<=cdtAnimationStart.getSelection().getTime())
					cdtAnimationEnd.setSelection(new Date (cdtAnimationStart.getSelection().getTime()+dataFilter.getDisplayIntervalMilli()));
				if (cdtCurrent.getSelection().getTime()>cdtAnimationEnd.getSelection().getTime())
					cdtCurrent.setSelection(new Date(cdtAnimationEnd.getSelection().getTime()));
				resetScale();
			}
	    });
		
		
	    scale = new Scale (parent, SWT.BORDER);
		Rectangle clientArea = parent.getClientArea ();
		scale.setBounds (clientArea.x, clientArea.y, 200, 64);
		scale.setMaximum ((int)(cdtAnimationEnd.getSelection().getTime() - cdtAnimationStart.getSelection().getTime()));
		scale.setPageIncrement ((int)dataFilter.getDisplayIntervalMilli());

		scale.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int position = scale.getSelection();
				cdtCurrent.setSelection(new Date(cdtAnimationStart.getSelection().getTime() + position));
				dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int position = scale.getSelection();
				cdtCurrent.setSelection(new Date(cdtAnimationStart.getSelection().getTime() + position));
				dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
				
			}
			
			
		});
		
		
		
		Group dateGroup = new Group(parent, SWT.SHADOW_NONE);
		dateGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		cal.setTimeInMillis(dataFilter.getCurrentTimeMilli());
	    cdtCurrent = new CDateTime(dateGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
//	    cdtCurrent.setPattern("'Starting Time' EEEE, MMMM d '@' hh:mm:ss 'GMT'");
	    cdtCurrent.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtCurrent.setPattern("'Starting Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtCurrent.setPattern("'Current Time:' MM/dd/yyyy HH:mm:ss ");
//		cdtCurrent.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		cdtCurrent.setSelection(cal.getTime());
	    cdtCurrent.addSelectionListener(new SelectionListener() {
			@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// set data filter value
					dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
				}

			@Override
				public void widgetSelected(SelectionEvent event) {
					System.out.println("Start time: " + cdtCurrent.getSelection());
					dataFilter.setCurrentTime(cdtCurrent.getSelection().getTime());
				}

	    });
		cal.setTimeInMillis(dataFilter.getDisplayIntervalMilli());
	    cdtDisplayInterval = new CDateTime(dateGroup, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
	    cdtDisplayInterval.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
//	    cdtDisplayInterval.setPattern("'Ending Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
	    cdtDisplayInterval.setPattern("'Displayed range:' HH:mm:ss ");
//	    cdtDisplayInterval.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    cdtDisplayInterval.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
	    cdtDisplayInterval.setSelection(cal.getTime());
	    cdtDisplayInterval.addSelectionListener(new SelectionListener() {
			@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// set data filter value
					dataFilter.setDisplayInterval(cdtDisplayInterval.getSelection().getHours(), cdtDisplayInterval.getSelection().getMinutes(), cdtDisplayInterval.getSelection().getSeconds());
				}

			@Override
				public void widgetSelected(SelectionEvent event) {
					System.out.println("DisplayInterval: " + cdtDisplayInterval.getSelection().getHours() + ":" + cdtDisplayInterval.getSelection().getMinutes() + ":" + cdtDisplayInterval.getSelection().getSeconds());
					dataFilter.setDisplayInterval(cdtDisplayInterval.getSelection().getHours(), cdtDisplayInterval.getSelection().getMinutes(), cdtDisplayInterval.getSelection().getSeconds());
				}

	    });

		timeRangeText = new Text(parent, SWT.CENTER);
		Date startDate = new Date(dataFilter.getDataStartTimeMilli());
		Date endDate = new Date(dataFilter.getDataEndTimeMilli());
		
		
		// this will need to be updated if refreshtimerange is called
		timeRangeText.setText("Data Available: "+ startDate.toString() + " -> " + endDate.toString());

		
		createActions();
	    createToolbar();
	    
	    
	    
//	    final Button buttonDraw = new Button(parent, SWT.PUSH);
//	    buttonDraw.setText("Draw Bounding Box");
//	    buttonDraw.setToolTipText("Press this button then press and drag button 1 on globe");
//	    buttonDraw.addSelectionListener(new SelectionListener() {
//
//		      public void widgetSelected(SelectionEvent event) {
//		    	  wwEvent.enableSelectors();
//		    	  selectedSector = null;
//		      }
//
//		      public void widgetDefaultSelected(SelectionEvent event) {
//		    	  wwEvent.enableSelectors();
//		    	  selectedSector = null;
//		      }
//		    });
//
//	    final Button buttonDrawCancel = new Button(parent, SWT.PUSH);
//	    buttonDrawCancel.setText("Cancel Drawn Box");
//	    buttonDrawCancel.addSelectionListener(new SelectionListener() {
//
//		      public void widgetSelected(SelectionEvent event) {
//		    	  wwEvent.disableSelectors();
//		      }
//
//		      public void widgetDefaultSelected(SelectionEvent event) {
//		    	  wwEvent.disableSelectors();
//		      }
//		    });
//	    
//	    
//	    final Button buttonApply = new Button(parent, SWT.PUSH);
//	    buttonApply.setText("Apply Changes");
//
//	    buttonApply.addSelectionListener(new SelectionListener() {
//
//	      public void widgetSelected(SelectionEvent event) {
//	    	if (selectedSector!=null) {	    		
//	      	  	dataFilter.setBoundingBox(selectedSector.getMinLongitude().degrees, selectedSector.getMaxLongitude().degrees, selectedSector.getMinLatitude().degrees, selectedSector.getMaxLatitude().degrees);
//	    		selectedSector=null;
//		    	wwEvent.disableSelectors();
//	    	}
//	        dataFilter.refreshObjects();
//	      }
//
//	      public void widgetDefaultSelected(SelectionEvent event) {
//		    	if (selectedSector!=null) {	    		
//		      	  	dataFilter.setBoundingBox(selectedSector.getMinLongitude().degrees, selectedSector.getMaxLongitude().degrees, selectedSector.getMinLatitude().degrees, selectedSector.getMaxLatitude().degrees);
//		    		selectedSector=null;
//			    	wwEvent.disableSelectors();
//		    	}
//		        dataFilter.refreshObjects();
//		  }
//
//	    });
	 
	}
	private void resetScale()
	{
		scale.setMaximum ((int)(cdtAnimationEnd.getSelection().getTime() - cdtAnimationStart.getSelection().getTime()));
		scale.setPageIncrement ((int)dataFilter.getDisplayIntervalMilli());
		scale.setSelection((int)(cdtCurrent.getSelection().getTime()-cdtAnimationStart.getSelection().getTime()));

	}

    public void createActions() {
           playAction = new Action("Start Animation") {
                public void run() { 
                           StartAnimation();
                   }
           };
           playAction.setImageDescriptor(getImageDescriptor("nav_go.gif"));

           stopAction = new Action("Stop Animation") {
                   public void run() {
                           StopAnimation();
                   }
           };
           stopAction.setImageDescriptor(getImageDescriptor("suspend_co.gif"));

           drawBoxAction = new Action("Draw Bounding Box") {
                   public void run() {
                        DrawBox();
                   }
           };
 //          drawBoxAction.setImageDescriptor(getImageDescriptor("rectangle.jpg"));
           drawBoxAction.setImageDescriptor(getImageDescriptor("draw-rectangle.png"));

           clearAction = new Action("Clear Drawn Bounding Box") {
               public void run() {
                        ClearBox();
              }
           };
//          drawBoxAction.setImageDescriptor(getImageDescriptor("rectangle.jpg"));
           clearAction.setImageDescriptor(getImageDescriptor("delete_edit.gif"));

           
           refreshAction = new Action("Apply bounding box and refresh display using current time") {
               public void run() {
                        DrawCurrent();
              }
           };
           refreshAction.setImageDescriptor(getImageDescriptor("nav_refresh.gif"));
           
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
    private void DrawBox()
    {
		wwEvent.enableSelectors();
		selectedSector = null;    	
		cdtCurrent.setEnabled(false);
		cdtDisplayInterval.setEnabled(false);
		cdtAnimationStart.setEnabled(false);
		cdtAnimationEnd.setEnabled(false);
		scale.setEnabled(false);
		drawBoxAction.setEnabled(false);
    }
    private void ClearBox()
    {
    	wwEvent.disableSelectors();	
   		cdtCurrent.setEnabled(true);
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
        
   		cdtCurrent.setEnabled(true);
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
            mgr.add(playAction);
            mgr.add(stopAction);
            mgr.add(drawBoxAction);
            mgr.add(clearAction);
            mgr.add(refreshAction);
            
            animator = new Animator(1000);
    }

 
  /**
     * Returns the image descriptor with the given relative path.
     */
    private ImageDescriptor getImageDescriptor(String relativePath) {
            String iconPath = "icons/";
//            try {
            	return ImageDescriptor.createFromFile(this.getClass(), "/icons/"+relativePath);
//            	InputStream input = getClass().getResourceAsStream("/icons/histogram-16x16.gif");  
//            	input.
//            	ViewsPlugin plugin = ViewsPlugin.getDefault();
//                URL installURL = plugin..getDescriptor().getInstallURL();
//                URL url = new URL(installURL, iconPath + relativePath);
//                return ImageDescriptor..createFromURL(url);             
//            }
//            catch (MalformedURLException e) {
//                    // should not happen
//                    return ImageDescriptor.getMissingImageDescriptor();
//            }
    }
    void refreshTimeRange()
    {
		MaxMin maxmin = new MaxMin();
		long dateMax, dateMin;
		getDateRange(maxmin, conf.getEntlnDateRangeLayer()); 
		dateMax = maxmin.getMax();
		dateMin = maxmin.getMin();
		getDateRange(maxmin, conf.getNldnDateRangeLayer()); 
		dateMax = Math.max(dateMax, maxmin.getMax());
		dateMin = Math.min(dateMin, maxmin.getMin());
		getDateRange(maxmin, conf.getGld360DateRangeLayer()); 
		dateMax = Math.max(dateMax, maxmin.getMax());
		dateMin = Math.min(dateMin, maxmin.getMin());
		getDateRange(maxmin, conf.getGlmDateRangeLayer()); 
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
    
    void getDateRange(MaxMin maxmin, String layer)
    {
		String httpString = conf.getProtocolHttp() + conf.getServerIP() + ":" + conf.getServerPort() + conf.getServiceStringCsv() + layer; 
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

		    	dataFilter.refreshObjects();
		    	display.timerExec(delay, this);
			}

          };
      }

      private void stop()
      {
    	  // re-enable other controls
    	  
		System.out.println("animator stop");
		cdtCurrent.setEnabled(true);
		cdtDisplayInterval.setEnabled(true);
		cdtAnimationStart.setEnabled(true);
		cdtAnimationEnd.setEnabled(true);
		scale.setEnabled(true);
		drawBoxAction.setEnabled(true);
		clearAction.setEnabled(true);
		refreshAction.setEnabled(true);
		display.timerExec(-1, this.timer);
//		timer.stop();
      }

      private void start()
      {
    	  // disable other controls, only enable stop animation button
    	  
  		System.out.println("animator start");
		cdtCurrent.setEnabled(false);
		cdtDisplayInterval.setEnabled(false);
		cdtAnimationStart.setEnabled(false);
		cdtAnimationEnd.setEnabled(false);
	    scale.setEnabled(false);
		drawBoxAction.setEnabled(false);
		clearAction.setEnabled(false);
		refreshAction.setEnabled(false);
		displayCurrent = true;
    	display.timerExec(delay, this.timer);
		
//        timer.start();
        
      }
  }
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		System.out.println("TimelineView.refresh");
		if ((cdtCurrent==null)||(cdtDisplayInterval==null)||cdtCurrent.isDisposed() || cdtDisplayInterval.isDisposed()) {
			System.err.println("Warning: widget disposed");
			return;
		}
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(dataFilter.getCurrentTimeMilli());
		cdtCurrent.setSelection(cal.getTime());		
		cal.setTimeInMillis(dataFilter.getDisplayIntervalMilli());
		cdtDisplayInterval.setSelection(cal.getTime());	
		resetScale();
		
		Date startDate = new Date(dataFilter.getDataStartTimeMilli());
		Date endDate = new Date(dataFilter.getDataEndTimeMilli());
	}

	@Override
	public void sectorChanged(Sector sector) {
		// TODO Auto-generated method stub
		selectedSector = sector;
		
//  	  	wwEvent.disableSelectors();
//  	  	if (sector==null) return;
//  	  	dataFilter.setBoundingBox(sector.getMinLongitude().degrees, sector.getMaxLongitude().degrees, sector.getMinLatitude().degrees, sector.getMaxLatitude().degrees);
//  	  	dataFilter.refreshObjects();

	}

}