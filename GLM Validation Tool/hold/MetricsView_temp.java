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
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		
		imagePart = part;
		
		chartFrames = new ChartComposite[charts.length];
		
		for (int i=0; i<charts.length; i++)
		{
			chartFrames[i] = new ChartComposite(shell, SWT.NONE, charts[i], true);
			chartFrames[i].setLayoutData(new RowData(FRAME_WIDTH, FRAME_HEIGHT));
		}

		// add listeners to dispose off the chart composites upon close
		shell.addListener(SWT.Dispose, new Listener()
		{
			public void handleEvent(Event event)
			{
				disposeFrames();
			}
		});
		shell.addShellListener(new ShellListener()
		{
			public void shellActivated(ShellEvent e)
			{
				HistogramDialog.this.imagePart.activatePart(true);
			}
			public void shellDeiconified(ShellEvent e)
			{
				HistogramDialog.this.imagePart.activatePart(true);
			}
			public void shellDeactivated(ShellEvent e)
			{
				HistogramDialog.this.imagePart.activatePart(false);
			}
			public void shellIconified(ShellEvent e)
			{
				HistogramDialog.this.imagePart.activatePart(false);
			}
			public void shellClosed(ShellEvent e)
			{
				HistogramDialog.this.imagePart.removePart();
			}
		});

		
	}

	public void displayHistogram(Rectangle bounds)
	{
		// bounds = canvas.getSourceImage().getBounds(); // DEBUG
		final int size = bounds.width * bounds.height;
//		ImageData imageData = getTargetRegionImageData(bounds);
	
		double [] redArray = new double [size];
		double [] greenArray = new double [size];
		double [] blueArray = new double [size];
		
		// this is to track the pixel values and find out if the histogram
		// is being computed for a grayscale image - if so generate only 1 chart
		boolean similarHistograms = true;
		
		// NOTE: better not to handle RGB info manually. Get pixel value and extract RGB 
		// values using the palette. Refer notes in getTargetRegionImageData()
		RGB rgb = null;
		GliderObject go = ((ImageGlider)mImage).getGliderObject();
		NetcdfFile cdfFile = go.getNcfile();
		GliderChannel rChan = go.getChannels().get(mImage.getRedChan());
		GliderChannel gChan = go.getChannels().get(mImage.getGreenChan());
		GliderChannel bChan = go.getChannels().get(mImage.getBlueChan());		
		for (int j=0, arrayIndex=0; j<bounds.height; j++)
		{
			for (int i=0; i<bounds.width; i++)
			{
				try 
				{
					// tab 5/18/12 line and pixel indices for readScaledPixelValue method were reversed in many locations
//					redArray[arrayIndex] = rChan.readScaledPixelValue(cdfFile, i, j);
//					greenArray[arrayIndex] = gChan.readScaledPixelValue(cdfFile, i, j);
//					blueArray[arrayIndex] = bChan.readScaledPixelValue(cdfFile, i, j);
					redArray[arrayIndex] = rChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
					greenArray[arrayIndex] = gChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
					blueArray[arrayIndex] = bChan.readScaledPixelValue(cdfFile,bounds.y+j, bounds.x+i);
				} 
				catch (Exception e) 
				{
					System.err.println("Error in ImageRGB::displayHistogram(Rectangle)");
				}
				
				if (similarHistograms 
						&& (blueArray[arrayIndex]!=greenArray[arrayIndex] 
						        || greenArray[arrayIndex]!=redArray[arrayIndex]))
				{
					similarHistograms = false;
				}
				arrayIndex++;
			}
		}

		/*
 		System.err.println("Image Size : [" + imageData.width 
				+ "] x [" + imageData.height + "] = " 
				+ imageData.width*imageData.height);
		int redSum=0;
		for (int i=0; i<256; i++)
		{
			redSum += redArray[i];
		}
		System.err.println("Red Count : " + redSum);
		int greenSum=0;
		for (int i=0; i<256; i++)
		{
			greenSum += redArray[i];
		}
		System.err.println("Green Count : " + greenSum);
		int blueSum=0;
		for (int i=0; i<256; i++)
		{
			blueSum += redArray[i];
		}
		System.err.println("Blue Count : " + blueSum);
		*/
		//createDataSets(redArray, greenArray, blueArray);
		
		//TODO the dataset could be created with different min/max and num of bins
		// right now it is only on the scaled values from 0 to 255
		HistogramDataset redDataset = new HistogramDataset();
		HistogramDataset greenDataset = new HistogramDataset();
		HistogramDataset blueDataset = new HistogramDataset();
		if (similarHistograms) {
			redDataset.addSeries("Grayscale", redArray, 100, rChan.getMin(), rChan.getMax());
		} else {
			redDataset.addSeries("Red", redArray, 100, rChan.getMin(), rChan.getMax());
		}
		if (!similarHistograms) {
			greenDataset.addSeries("Green", greenArray, 100, gChan.getMin(), gChan.getMax());
			blueDataset.addSeries("Blue", blueArray, 100, bChan.getMin(), bChan.getMax());
		}

		// create channel lables for the chars
		String redChLabel=null, greenChLabel=null, blueChLabel=null;
		if (mImage instanceof ImageGlider)
		{
			redChLabel = go.getChannels().get(mImage.getRedChan()).getUnits();
			greenChLabel = go.getChannels().get(mImage.getGreenChan()).getUnits();
			blueChLabel = go.getChannels().get(mImage.getBlueChan()).getUnits();
		}

		// create the charts from the datasets
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		JFreeChart redChart = null;
		if (similarHistograms) {
			redChart = createChart(redDataset, Color.white, redChLabel + " in all 3 channels");
		} else {
			redChart = createChart(redDataset, Color.red, redChLabel);
		}
		charts.add(redChart);
		
		if (!similarHistograms) {
			JFreeChart greenChart = createChart(greenDataset, Color.green, greenChLabel);
			charts.add(greenChart);
			JFreeChart blueChart = createChart(blueDataset, Color.blue, blueChLabel);
			charts.add(blueChart);
		}
		
		RegionOfInterest roi = new RegionOfInterest(bounds);
		canvas.addImagePart(roi);
		HistogramDialog histogramDialog = new HistogramDialog(
				Display.getDefault().getActiveShell(), SWT.NONE);
		histogramDialog.open(charts.toArray(new JFreeChart[0]), roi);
		// this is done from the ImagePart.removePart() 
		// canvas.removeImagePart(roi);
	}
	
/*
	private void createDataSets(int[] redArray, int[] greenArray, int[] blueArray)
	{
		HistogramDataset redDataset = new HistogramDataset();
		int INTERVAL = 5;
		int tempR=0, tempG=0, tempB=0;
		// row key will be 'Red' - column keys will the array indices
		for (int i=0; i<256; i+=INTERVAL)
		{
			tempR=0; tempG=0; tempB=0;
			for (int t=0; (t<INTERVAL) && (i+t<256); t++)
			{
				tempB += blueArray[i+t];
				tempG += greenArray[i+t];
				tempR += redArray[i+t];
			}
			//redDataset.addValue(tempR, "Red", String.valueOf(i+INTERVAL));
			//redDataset.addValue(tempG, "Green", String.valueOf(i+INTERVAL));
			//redDataset.addValue(tempB, "Blue", String.valueOf(i+INTERVAL));
		}
		
		redDataset.addSeries("Red", new double[]{1,2,3,1,2,2,2,3}, 5, 1, 5);

		DefaultCategoryDataset greenDataset = new DefaultCategoryDataset();
		// row key will be 'Green' - column keys will the array indices
		for (int i=0; i<256; i++)
		{
			greenDataset.addValue(greenArray[i], "Green", String.valueOf(i));
		}

		DefaultCategoryDataset blueDataset = new DefaultCategoryDataset();
		// row key will be 'Blue' - column keys will the array indices
		for (int i=0; i<256; i++)
		{
			blueDataset.addValue(blueArray[i], "Blue", String.valueOf(i));
		}
		
		JFreeChart redChart = createChart(redDataset, "RGB");
		
		HistogramView histogramView = null;
		try
		{
			histogramView = (HistogramView) 
				getPage().getWorkbenchWindow()
				.getActivePage().showView(
					HistogramView.ID, null, 
					IWorkbenchPage.VIEW_ACTIVATE);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		histogramView.setChart(redChart);
	}
*/
	
	private JFreeChart createChart(HistogramDataset histDataset, Color color, String colorName)
	{
		JFreeChart chart = ChartFactory.createHistogram(
				null, // the chart title is set on the dialog's shell
				colorName, // domain axis label
				null, //"Frequency", // range axis label
				histDataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				false, // tooltips?
				false // URLs?
				);

		XYPlot xyplot = (XYPlot) chart.getPlot();
		xyplot.setBackgroundPaint(Color.black);
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
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}
}