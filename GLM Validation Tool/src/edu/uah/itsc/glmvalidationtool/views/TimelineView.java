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

import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

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
	private CDateTime cdtStart, cdtEnd;
	private DataFilter dataFilter = new DataFilter();
	private WWEvent wwEvent = new WWEvent();
    private Sector selectedSector = null;

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
		

		GridLayout grid = new GridLayout();
	    parent.setLayout(grid);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(dataFilter.getStartTimeMilli());
		dataFilter.registerObject(this);

	    
	    cdtStart = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
//	    cdtStart.setPattern("'Starting Time' EEEE, MMMM d '@' hh:mm:ss 'GMT'");
	    cdtStart.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
	    cdtStart.setPattern("'Starting Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
		cdtStart.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		cdtStart.setSelection(cal.getTime());
	    cdtStart.addSelectionListener(new SelectionListener() {
			@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// set data filter value
					dataFilter.setStartTime(cdtStart.getSelection().getTime());
				}

			@Override
				public void widgetSelected(SelectionEvent event) {
					System.out.println("Start time: " + cdtStart.getSelection());
					dataFilter.setStartTime(cdtStart.getSelection().getTime());
				}

	    });
		cal.setTimeInMillis(dataFilter.getEndTimeMilli());
	    cdtEnd = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.CLOCK_24_HOUR | CDT.COMPACT);
	    cdtEnd.setFormat(CDT.DATE_LONG | CDT.TIME_MEDIUM);
	    cdtEnd.setPattern("'Ending Time:' EEEE, MMMM d yyyy '@' HH:mm:SS Z 'GMT'");
//	    cdtEnd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    cdtEnd.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
	    cdtEnd.setSelection(cal.getTime());
	    cdtEnd.addSelectionListener(new SelectionListener() {
			@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// set data filter value
					dataFilter.setEndTime(cdtEnd.getSelection().getTime());
				}

			@Override
				public void widgetSelected(SelectionEvent event) {
					System.out.println("End time: " + cdtEnd.getSelection());
					dataFilter.setEndTime(cdtEnd.getSelection().getTime());
				}

	    });
	    
	    final Button buttonDraw = new Button(parent, SWT.PUSH);
	    buttonDraw.setText("Draw Bounding Box");
	    buttonDraw.setToolTipText("Press this button then press and drag button 1 on globe");
	    buttonDraw.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent event) {
		    	  wwEvent.enableSelectors();
		    	  selectedSector = null;
		      }

		      public void widgetDefaultSelected(SelectionEvent event) {
		    	  wwEvent.enableSelectors();
		    	  selectedSector = null;
		      }
		    });

	    final Button buttonDrawCancel = new Button(parent, SWT.PUSH);
	    buttonDrawCancel.setText("Cancel Drawn Box");
	    buttonDrawCancel.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent event) {
		    	  wwEvent.disableSelectors();
		      }

		      public void widgetDefaultSelected(SelectionEvent event) {
		    	  wwEvent.disableSelectors();
		      }
		    });
	    
	    
	    final Button buttonApply = new Button(parent, SWT.PUSH);
	    buttonApply.setText("Apply Changes");

	    buttonApply.addSelectionListener(new SelectionListener() {

	      public void widgetSelected(SelectionEvent event) {
	    	if (selectedSector!=null) {	    		
	      	  	dataFilter.setBoundingBox(selectedSector.getMinLongitude().degrees, selectedSector.getMaxLongitude().degrees, selectedSector.getMinLatitude().degrees, selectedSector.getMaxLatitude().degrees);
	    		selectedSector=null;
		    	wwEvent.disableSelectors();
	    	}
	        dataFilter.refreshObjects();
	      }

	      public void widgetDefaultSelected(SelectionEvent event) {
		    	if (selectedSector!=null) {	    		
		      	  	dataFilter.setBoundingBox(selectedSector.getMinLongitude().degrees, selectedSector.getMaxLongitude().degrees, selectedSector.getMinLatitude().degrees, selectedSector.getMaxLatitude().degrees);
		    		selectedSector=null;
			    	wwEvent.disableSelectors();
		    	}
		        dataFilter.refreshObjects();
		  }

	    });
	 
		
//	    cal = new DateChooser(parent, SWT.BORDER | SWT.MULTI);
//	    cal.addSelectionListener(new SelectionListener() {
//			@Override
//				public void widgetDefaultSelected(SelectionEvent event) {
//				}
//
//			@Override
//				public void widgetSelected(SelectionEvent event) {
//					selectedDates.removeAll();
//					for (Iterator it = cal.getSelectedDates().iterator(); it.hasNext(); ) {
//						Date d = (Date) it.next();
//						selectedDates.add(df.format(d));
//					}
//				}
//
//	    });
//
//	    df = DateFormat.getDateInstance(DateFormat.MEDIUM);
//	    selectedDates = new List(parent, SWT.BORDER);
//	    GridData data = new GridData();
//	    data.widthHint  = 100;
//	    data.heightHint = 100;
//	    selectedDates.setLayoutData(data);

		
//		// initialize a parent composite with a grid layout manager
//        GridLayout gridLayout = new GridLayout();
//        gridLayout.numColumns = 1;
//        parent.setLayout(gridLayout);
//        DateTime calendar = new DateTime(parent, SWT.CALENDAR);
//        DateTime date = new DateTime(parent, SWT.DATE);
//        DateTime time = new DateTime(parent, SWT.TIME);
//        // Date Selection as a drop-down
//        DateTime dateD = new DateTime(parent, SWT.DATE | SWT.DROP_DOWN);
		
//		GanttChart ganttChart = new GanttChart(parent, SWT.MULTI);
//		Calendar start = GregorianCalendar.getInstance();
//		Calendar end = GregorianCalendar.getInstance();
//		end.add(Calendar.DATE, 5);
//		new GanttEvent(ganttChart, null, "Event_1", start, end, start, end, 0);
//		start = GregorianCalendar.getInstance();
//		end = GregorianCalendar.getInstance();
//		start.add(Calendar.DATE, 6);
//		end.add(Calendar.DATE, 8);
//		new GanttEvent(ganttChart, null, "Event_2", start, end, start, end, 0);
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
		if ((cdtStart==null)||(cdtEnd==null)||cdtStart.isDisposed() || cdtEnd.isDisposed()) {
			System.err.println("Warning: widget disposed");
			return;
		}
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(dataFilter.getStartTimeMilli());
		cdtStart.setSelection(cal.getTime());		
		cal.setTimeInMillis(dataFilter.getEndTimeMilli());
		cdtEnd.setSelection(cal.getTime());		
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