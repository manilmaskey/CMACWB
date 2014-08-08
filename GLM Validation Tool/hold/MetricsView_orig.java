package edu.uah.itsc.glmvalidationtool.views;


import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.*;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


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

public class MetricsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "MetricsView";

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
	    //use LightweightSystem to create the bridge between SWT and draw2D
//		final Canvas canvas= new Canvas(parent,SWT.NONE);
//		final Shell shell = canvas.getShell();
//		final LightweightSystem lws = new LightweightSystem(canvas);
		
		final Canvas canvas= new Canvas(parent,SWT.NONE);
		final LightweightSystem lws = new LightweightSystem(canvas);
//		

//		GridLayout grid = new GridLayout();
//	    grid.numColumns=1;
		RowLayout row = new RowLayout();
	    canvas.setLayout(row);
		
		XYGraph graph1 = newGraph(lws);
		XYGraph graph2 = newGraph(lws);
		XYGraph graph3 = newGraph(lws);
		XYGraph graph4 = newGraph(lws);
		
//		//create the trace
//		Trace trace2 = new Trace("Trace1-XY Plot", 
//				xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider2);			
//		
//		//set trace property
//		trace2.setPointSize(6);
//		trace2.setAreaAlpha(150);
//		trace2.setTraceType(TraceType.AREA);
//		trace2.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_RED) );
//
//		//trace2.setLineWidth(5);
//		//add the trace to xyGraph
//		xyGraph.addTrace(trace2);	

		
	}

	private XYGraph newGraph(LightweightSystem lws)
	{
		//create a new XY Graph.
		XYGraph xyGraph = new XYGraph();
		xyGraph.setTitle("Bar and Area Chart");
		//set it as the content of LightwightSystem
		lws.setContents(xyGraph);
		
		//Configure XYGraph
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		
		
		//create a trace data provider, which will provide the data to the trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(100);		
		traceDataProvider.setCurrentXDataArray(new double[]{0, 20, 30, 40, 50, 60, 70, 80, 100});
		traceDataProvider.setCurrentYDataArray(new double[]{11, 44, 55, 45, 88, 98, 52, 23, 78});	
		
		//create the trace
		Trace trace = new Trace("Trace1-XY Plot", 
				xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
		
		//set trace property
		trace.setTraceType(TraceType.BAR);
		trace.setLineWidth(15);
		trace.setAreaAlpha(200);
		trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE) );
		//add the trace to xyGraph
		xyGraph.addTrace(trace);			
	   
		//create a trace data provider, which will provide the data to the trace.
		CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(false);
		traceDataProvider2.setBufferSize(100);		
		traceDataProvider2.setCurrentXDataArray(new double[]{0, 20, 30, 40, 50, 60, 70, 80, 100});
		traceDataProvider2.setCurrentYDataArray(new double[]{15, 60, 40, 60, 70, 80, 65, 70, 23});	

		return xyGraph;
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}
}