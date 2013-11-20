package piworkflow.editors;

import java.io.IOException;

import jsonForSave.DataPOJO;
import jsonForSave.JSONRead;
import jsonForSave.ReCreate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;

import edu.uah.itsc.workflow.childComposites.ChildCompositeCreator;
import edu.uah.itsc.workflow.methodDragAndDrop.FavoritesDropTarget;
import edu.uah.itsc.workflow.movementTrackers.MethodCompositeTracker;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

//import edu.uah.itsc.workflow.saveHandler.CopyOfCreateXML;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class MultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	// Global Variable
	// private TextEditor editor;
	// private MultiPageEditor editor;
	String Filename;

	/**
	 * Creates a multi-page editor.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates Work Space for the Work Flow
	 */
	void PI_WorkFlow() {

		
		// setting up parent composite
		// set up the parent composites lay out to grid layout
		CompositeWrapper parentComposite = (CompositeWrapper) VariablePoJo
				.getInstance().getParentComposite();

		parentComposite = new CompositeWrapper(getContainer(), SWT.NONE);
		VariablePoJo.getInstance().setDisplayX(
				parentComposite.getDisplay().getBounds().width);
		VariablePoJo.getInstance().setDisplayY(
				parentComposite.getDisplay().getBounds().height);
		parentComposite.setLayout(new GridLayout(1, false));

		// Create the child composites, pass parentComposite to the
		// ChildCompositeCreator class
		// childComposite_coolBar for the cool bar
		// childComposite_WorkSpace for the Actions (Work Space)
		VariablePoJo.getInstance().setChildCreatorObject(
				new ChildCompositeCreator());
		ChildCompositeCreator childCreatorObject = VariablePoJo.getInstance()
				.getChildCreatorObject();
		childCreatorObject.setParentComposite(parentComposite);
		childCreatorObject.createChildComposites();

		// // Get the child composite for cool bar using the childCreatorObject
		// Composite childComposite_coolBar = childCreatorObject
		// .getChildComposite_coolBar();
		//
		// // Call method to create cool bar
		// CreateCoolBar coolBarCreatorObject = new CreateCoolBar();
		// coolBarCreatorObject.setChildComposite_coolBar(childComposite_coolBar);
		// coolBarCreatorObject.generateCoolBar();

		// Get the Buttons for the create cool bar class using CreateCoolBar
		// class
		// final Button inputButton = coolBarCreatorObject.getInputButton();
		// final Button saveButton = coolBarCreatorObject.getSaveButton();
		// final Button outputButton = coolBarCreatorObject.getOutputButton();

		// ----------------------------------------------------------------------------------------------------------------
		// adding input handler to the button
		// inputButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		//
		// // Get child composite for Action (Work Space)
		// final CompositeWrapper childComposite_WorkSpace = VariablePoJo
		// .getInstance().getChildCreatorObject()
		// .getChildComposite_WorkSpace();
		//
		// // Set the required data
		// InputHandler inputHandlerObj = new InputHandler();
		// inputHandlerObj
		// .setChildComposite_WorkSpace(childComposite_WorkSpace);
		// inputHandlerObj.setCompositeList(VariablePoJo.getInstance()
		// .getCompositeList());
		// // inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
		// // .getConnectorList());
		// inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
		// .getConnectorList());
		//
		// inputHandlerObj.setStartingComposite(VariablePoJo.getInstance()
		// .getStartingComposite());
		//
		// // Call the method
		// inputHandlerObj.inputCompositeCreator();
		//
		// // Get the input composite created when the input button is hit
		// final CompositeWrapper inputComposite = inputHandlerObj
		// .getInputComposite();
		//
		// // add a new mouse listener to enable the new input composite to
		// // move
		// inputComposite.addListener(SWT.MouseDown, new Listener() {
		// public void handleEvent(Event e) {
		// InputCompositeTracker inputTrackerObject = new
		// InputCompositeTracker();
		// inputTrackerObject.setCompositeList(VariablePoJo
		// .getInstance().getCompositeList());
		// inputTrackerObject.setInputComposite(inputComposite);
		// inputTrackerObject
		// .setChildComposite_WorkSpace(childComposite_WorkSpace);
		// inputTrackerObject.setParentComposite(VariablePoJo
		// .getInstance().getParentComposite());
		// // inputTrackerObject.setConnectorList(VariablePoJo
		// // .getInstance().getConnectorList());
		// inputTrackerObject.setConnectorList(VariablePoJo
		// .getInstance().getConnectorList());
		// inputTrackerObject.inputTracker();
		//
		// }
		// });
		// }
		//
		// });
		// //
		// --------------------------------------------------------------------------------------------------------------
		//
		// //
		// ----------------------------------------------------------------------------------------------------------------
		// // adding input handler to the button
		//
		// saveButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		//
		// /**
		// * On the event of button click We get the name of the page Call
		// * the method to create an XML The new XML file's name if of
		// * format filename.xml (filename is the name of .mpe file)
		// */
		// // get the name of the file
		// IWorkbenchPage activePage = getSite().getPage();
		// System.out.println("active editor"
		// + activePage.getActiveEditor());
		// String name = activePage.getActiveEditor().getEditorInput()
		// .getName();
		// System.out.println(name); // for testing the name
		//
		// // Create an object of create XML and call createFile method
		// // CreateXML xmlObj = new CreateXML();
		// // xmlObj.createFile(name);
		// CopyOfCreateXML obj = new CopyOfCreateXML();
		// obj.createFile(name);
		//
		// // JSONWrite writeObject = new JSONWrite();
		// // writeObject.createJSONFile(name);
		// }
		// });
		//
		// //
		// --------------------------------------------------------------------------------------------------------------
		//
		// //
		// ----------------------------------------------------------------------------------------------------------------
		// // adding input handler to the button
		//
		// outputButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		//
		// // Get child composite for Action (Work Space)
		// final CompositeWrapper childComposite_WorkSpace = VariablePoJo
		// .getInstance().getChildCreatorObject()
		// .getChildComposite_WorkSpace();
		//
		// // Get an object of the output handler class
		// final OutputHandler outputHandlerObj = new OutputHandler();
		//
		// // Set all the required data
		// outputHandlerObj
		// .setChildComposite_WorkSpace(childComposite_WorkSpace);
		// outputHandlerObj.setCompositeList(VariablePoJo.getInstance()
		// .getCompositeList());
		// // outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
		// // .getConnectorList());
		// outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
		// .getConnectorList());
		// outputHandlerObj.setConnectorObj(VariablePoJo.getInstance()
		// .getConnectorObj());
		// // outputHandlerObj.setEndingComposite(endingComposite);
		// outputHandlerObj.setParentComposite(VariablePoJo.getInstance()
		// .getParentComposite());
		//
		// // Call the method to create the composite on clicking the
		// // output button
		// outputHandlerObj.outputCompositeCreator();
		//
		// // Get the output composite created by the output handler class
		// final CompositeWrapper outputComposite = outputHandlerObj
		// .getOutputComposite();
		//
		// // adding mouse listener to the output composite to enable the
		// // block to move
		// outputComposite.addListener(SWT.MouseDown, new Listener() {
		// public void handleEvent(Event e) {
		//
		// OutputCompositeTracker outputTrackerObject = new
		// OutputCompositeTracker();
		// outputTrackerObject.setCompositeList(VariablePoJo
		// .getInstance().getCompositeList());
		// outputTrackerObject.setOutputComposite(outputComposite);
		// outputTrackerObject
		// .setChildComposite_WorkSpace(childComposite_WorkSpace);
		// outputTrackerObject.setParentComposite(VariablePoJo
		// .getInstance().getParentComposite());
		// // outputTrackerObject.setConnectorList(VariablePoJo
		// // .getInstance().getConnectorList());
		// outputTrackerObject.setConnectorList(VariablePoJo
		// .getInstance().getConnectorList());
		// outputTrackerObject.outputTracker();
		// }
		// });
		// }
		// });
		//
		// // Adding listener to the refresh button
		// coolBarCreatorObject.getRefreshButton().addSelectionListener(
		// new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		//
		// RefreshButtonHandler refreshHandlerObj = new RefreshButtonHandler();
		// refreshHandlerObj.handleRefreshButton();
		//
		// }
		// });
		// //
		// --------------------------------------------------------------------------------------------------------------
		//
		// childCreatorObject.getChildComposite_WorkSpace().pack();
		// childComposite_coolBar.pack();

		// childCreatorObject.getChildComposite_WorkSpace().addMouseListener(
		// new MouseListener() {
		//
		// @Override
		// public void mouseUp(MouseEvent e) {
		//
		// }
		//
		// @Override
		// public void mouseDown(MouseEvent e) {
		// // x, y of where event occured
		// int click_x = e.x;
		// int click_y = e.y;
		//
		// System.out.println("X" + click_x);
		// System.out.println("Y" + click_y);
		//
		// List<CompositeConnectors> connectorList = VariablePoJo
		// .getInstance().getConnectorList();
		//
		// for (int i = 0; i < connectorList.size(); i++) {
		// CompositeConnectors tempConnector = connectorList
		// .get(i);
		// // equation of a line
		// int dY = tempConnector.getDestination().getParent()
		// .getLocation().y
		// + (tempConnector.getDestination()
		// .getLocation().y)
		// + ((tempConnector.getDestination()
		// .getBounds().height) / 2);
		//
		// int dX = tempConnector.getDestination().getParent()
		// .getLocation().x
		// + (tempConnector.getDestination()
		// .getLocation().x);
		// int sY = tempConnector.getSource().getParent()
		// .getLocation().y
		// + (tempConnector.getSource().getLocation().y)
		// + ((tempConnector.getSource().getBounds().height) / 2);
		// int sX = tempConnector.getSource().getParent()
		// .getLocation().x
		// + (tempConnector.getSource().getLocation().x)
		// + (tempConnector.getSource().getBounds().width);
		//
		// int mY = dY - sY;
		// int mX = dX - sX;
		// double m = mY / mX;
		// // int c =
		// //
		// (tempConnector.getSource().getBounds().y)-m*(tempConnector.getSource().getBounds().x);
		//
		// double result = (click_y - sY) - m * click_x;
		// if (-3 <= result && result <=3) {
		// System.out.println("trial");
		// }
		//
		// }
		// }
		//
		// @Override
		// public void mouseDoubleClick(MouseEvent e) {
		//
		// }
		// });

		// this will get the programs list from the programview plug in and set
		// it in the variablePOJO singleton class
		VariablePoJo.getInstance().setProgram_List();

		// set the drop target
		setDropTarget();
		

		VariablePoJo.getInstance().getChildCreatorObject()
				.getChildComposite_WorkSpace()
				.addPaintListener(new PaintListener() {
					
					@Override
					public void paintControl(PaintEvent e) {
						
//						if (VariablePoJo.getInstance().isCreated() == true) {
							RelayComposites object = new RelayComposites();
							System.out.println("In paint event");
							object.reDraw();
//						}
					}
				});

		int index = addPage(parentComposite);
		setPageText(index, "CMAC Workflow Editor");

	}

	// Create Work flow Pages
	protected void createPages() {
		// create the page
		PI_WorkFlow();
			// check if it can be recreated and redraw 
			disposeall();
			try {
				checkForReCreate();
				refresh();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			refresh();
			
	}
	
	public void refresh (){
		RelayComposites object = new RelayComposites();
		object.reDraw();
	}

	/**
	 * Set Drop Target to current editor.
	 */
	public void setDropTarget() {
		System.out.println("Setting Drop Target ...");
		new FavoritesDropTarget(VariablePoJo.getInstance()
				.getChildCreatorObject().getChildComposite_WorkSpace());
	}

	public String getFileName() throws IOException {

		System.out.println("\n In the getFileName method ...");
		IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
		IWorkbenchPage page = pagelist[0];

		IEditorReference[] referencelist = page.getEditorReferences();

		String fileName = null;
		try {
			System.out.println(referencelist[0].getEditorInput().getName());
			fileName = referencelist[0].getEditorInput().getName();
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}
		return fileName;
	}

	/**
	 * This method will access the name of the file being opened Will check if
	 * there is an JSON file related to this file If yes, will recreate the
	 * workspace using JSON data
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void checkForReCreate() throws Exception {

		/**
		 * First, get the list of pages in the work bench from workbench ->
		 * active workbench window -> get pages For our main page i;e page 0 get
		 * the editor reference list The first item if the reference list is the
		 * reference for our main page. Get that editor's reference -> get
		 * editor's input -> get name This process will give us the name of the
		 * file
		 */
		IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
		IWorkbenchPage page = pagelist[0];

		IEditorReference[] referencelist = page.getEditorReferences();
		String fileName = null;
		System.out.println("test ...");
		IFile ifile = (IFile) referencelist[0].getEditorInput().getAdapter(
				IFile.class);
		System.out.println("absolute path " + ifile.getName());
		String path = ifile.getRawLocation().toOSString();
		System.out.println("path: " + path);

		try {
			System.out.println(referencelist[0].getEditorInput().getName());
			fileName = referencelist[0].getEditorInput().getName();
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}

		/**
		 * The readJSONFile will return false if the file is not readable of if
		 * there is no such file. If false we do no recreate it.
		 */
		JSONRead reader = new JSONRead();
		boolean isFile = reader.readJSONFile(path);
		VariablePoJo.getInstance().setFile(isFile);
		if (VariablePoJo.getInstance().isFile() == true) {
			ReCreate obj = new ReCreate();
			obj.recreateWorkFlow();
		}
		
		RelayComposites object = new RelayComposites();
		object.reDraw();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		disposeall();
		super.dispose();
	}

	public void disposeall() {
		VariablePoJo.getInstance().getCompositeList()
				.removeAll(VariablePoJo.getInstance().getCompositeList());
		VariablePoJo.getInstance().getConnectorList()
				.removeAll(VariablePoJo.getInstance().getConnectorList());
		VariablePoJo
				.getInstance()
				.getConnectorDetectableList()
				.removeAll(
						VariablePoJo.getInstance().getConnectorDetectableList());
		DataPOJO.getInstance().getPrograms_data().removeAll(DataPOJO.getInstance().getPrograms_data());
	}

	/**
	 * Saves the PI Work Flow's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		System.out.println("editor is " + getEditor(0));
		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the PI Work Flow's document as another file.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);

		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

	}

	// /**
	// * Closes all project files on project close.
	// */
	// public void resourceChanged(final IResourceChangeEvent event) {
	// if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
	// .getPages();
	// for (int i = 0; i < pages.length; i++) {
	// if (((FileEditorInput) editor.getEditorInput())
	// .getFile().getProject()
	// .equals(event.getResource())) {
	// IEditorPart editorPart = pages[i].findEditor(editor
	// .getEditorInput());
	// pages[i].closeEditor(editorPart, true);
	// }
	// }
	// }
	// });
	// }
	// }

}
