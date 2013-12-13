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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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
	String Filename;

	/**
	 * Creates a editor.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates Work Space for the Work Flow
	 */
	void PI_WorkFlow() {

		/**
		 * Setting up parent composite, using GridLayout
		 */
		CompositeWrapper parentComposite = (CompositeWrapper) VariablePoJo
				.getInstance().getParentComposite();

		parentComposite = new CompositeWrapper(getContainer(), SWT.NONE);
		VariablePoJo.getInstance().setDisplayX(
				parentComposite.getDisplay().getBounds().width);
		VariablePoJo.getInstance().setDisplayY(
				parentComposite.getDisplay().getBounds().height);
		parentComposite.setLayout(new GridLayout(1, false));

		/**
		 * Creating child composite
		 */
		VariablePoJo.getInstance().setChildCreatorObject(
				new ChildCompositeCreator());
		ChildCompositeCreator childCreatorObject = VariablePoJo.getInstance()
				.getChildCreatorObject();
		childCreatorObject.setParentComposite(parentComposite);
		childCreatorObject.createChildComposites();

		/**
		 * Grab the program objects list from programview plugin
		 */
		VariablePoJo.getInstance().setProgram_List();

		/**
		 * Set the Drop target
		 */
		setDropTarget();

		/**
		 * Listener for repaint
		 */
		VariablePoJo.getInstance().getChildCreatorObject()
				.getChildComposite_WorkSpace()
				.addPaintListener(new PaintListener() {

					@Override
					public void paintControl(PaintEvent e) {
						RelayComposites object = new RelayComposites();
						System.out.println("In paint event");
						object.reDraw();
					}
				});

		int index = addPage(parentComposite);
		setPageText(index, "CMAC Workflow Editor");
	}

	/**
	 * Create Pages
	 */
	protected void createPages() {
		/**
		 * Create work flow editor page
		 */
		PI_WorkFlow();

		/**
		 * If programs list is not yet created then close the editor. Programs
		 * list is a must to be created in order for this editor to work
		 */
		if (VariablePoJo.getInstance().getProgram_List() == null) {
			IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getPages();
			IWorkbenchPage page = pagelist[0];

			IEditorReference[] referencelist = page.getEditorReferences();
			IEditorPart editorPart = referencelist[0].getEditor(false);
			page.closeEditor(editorPart, true);
		}

		/**
		 * Check if recreate is possible (if the work flow was save before
		 * quitting)
		 */
		disposeall();
		try {
			checkForReCreate();
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		refresh();
	}

	/*
	 * Refresh the workflow's workspace
	 */
	public void refresh() {
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

	/**
	 * Get the file name
	 * 
	 * @return String filename
	 * @throws IOException
	 */
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
		DataPOJO.getInstance().getPrograms_data()
				.removeAll(DataPOJO.getInstance().getPrograms_data());
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

}
