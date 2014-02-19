package piworkflow.editors;

import java.io.IOException;

import jsonForSave.DataPOJO;
import jsonForSave.JSONRead;
import jsonForSave.ReCreate;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;

import edu.uah.itsc.workflow.childComposites.ChildCompositeCreator;
import edu.uah.itsc.workflow.menuOptions.WorkFlowSave;
import edu.uah.itsc.workflow.methodDragAndDrop.FavoritesDropTarget;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.variableHolder.t_VariablePoJo;
//import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class MultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	// Global Variable
	String Filename;
	protected boolean dirty = false;

	/**
	 * Creates a editor.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Sets the state of the editor to value entered
	 * 
	 * @param value
	 *            the state of the editor (dirty or not)
	 */
	public void setDirty(boolean value) {
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * returns the state of the editor
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Creates Work Space for the Work Flow
	 */
	@SuppressWarnings("deprecation")
	void PI_WorkFlow() {

		final CopyOfVariablePoJo newpojo = new CopyOfVariablePoJo();

		
		
		/**
		 * Setting up parent composite, using GridLayout
		 */
		CompositeWrapper parentComposite = (CompositeWrapper) newpojo.getParentComposite();
//		final ScrolledComposite parent = newpojo.getParent();		
//		parent = new ScrolledComposite(getContainer(), SWT.H_SCROLL|SWT.V_SCROLL);
		parentComposite = new CompositeWrapper(getContainer(), SWT.NONE);
		parentComposite.setLayout(new FillLayout());
		final ScrolledComposite parent = new ScrolledComposite(parentComposite, SWT.H_SCROLL|SWT.V_SCROLL);
		newpojo.setParent(parent);
		parent.setLayout(new GridLayout(1, false));

		
		
		
//		VariablePoJo.getInstance().setDisplayX(
//				parentComposite.getDisplay().getBounds().width);
//		VariablePoJo.getInstance().setDisplayY(
//				parentComposite.getDisplay().getBounds().height);
		
		
		
		//--------------------------------------------------
			newpojo.setDisplayX(parentComposite.getDisplay().getBounds().width);
			newpojo.setDisplayY(parentComposite.getDisplay().getBounds().height);
		//--------------------------------------------------
			
//			parentComposite.setLayout(new GridLayout(1, false));
			
		

		/**
		 * Creating child composite
		 */
//		VariablePoJo.getInstance().setChildCreatorObject(
//				new ChildCompositeCreator());
//		ChildCompositeCreator childCreatorObject = VariablePoJo.getInstance()
//				.getChildCreatorObject();
			
		
			newpojo.setChildCreatorObject(
					new ChildCompositeCreator());
			ChildCompositeCreator childCreatorObject = newpojo
					.getChildCreatorObject();
			
			t_VariablePoJo.getInstance().setChildCreatorObject(newpojo.getChildCreatorObject());
		
		//*****************************************************
		childCreatorObject.setParentComposite(parentComposite);
		childCreatorObject.setSc(parent);
		childCreatorObject.createChildComposites();
		final CompositeWrapper c_ws = childCreatorObject.getChildComposite_WorkSpace();
		
		// additions for scrolled composite
		c_ws.addListener(SWT.Resize, new Listener() {
			int width = -1;
			int height = -1;
			@Override
			public void handleEvent(Event event) {
				System.out.println("C_WS TOOK A RESIZE EVENT ...");
				 int newWidth = c_ws.getSize().x;
				 int newHeight = c_ws.getSize().y;
				 System.out.println("\n\tnewWidth = " + newWidth + "\n\tnewHiehgt = " + newHeight);
			     // + 10 for a little padding after the composite for convenience  
				 if (newWidth != width) {
			    	  System.out.println("IN NEW WIDTH != WIDTH LOOP");
			        parent.setMinHeight(c_ws.computeSize(newWidth, SWT.DEFAULT).y + 10);
//			        width = newWidth;
			      }
			      if (newHeight != height) {
			    	  System.out.println("IN NEW HEIGHT != HEIGHT LOOP");
				        parent.setMinWidth(c_ws.computeSize(SWT.DEFAULT, newHeight).x + 10);
//				        height = newHeight;
				      }
			}
			
		});
		
		parent.setExpandHorizontal(true);
		parent.setExpandVertical(true);

		parent.setContent(c_ws);
		
//		parent.setMinSize(c_ws.computeSize(parentComposite.getDisplay().getBounds().width, parentComposite.getDisplay().getBounds().height));		
//		parent.setSize(500, 500);
//		parent.setMinSize();
//		//--------------------------------------------------
//			newpojo.setChildCreatorObject(VariablePoJo.getInstance().getChildCreatorObject());
//		//--------------------------------------------------
		
		
		/**
		 * Grab the program objects list from programview plugin
		 */
//		VariablePoJo.getInstance().setProgram_List();
		
		//--------------------------------------------------
			newpojo.setProgram_List();
			t_VariablePoJo.getInstance().setProgram_List();
		//--------------------------------------------------

		 /**
		 * Set the Drop target
		 */
		 setDropTarget();

		/**
		 * Listener for repaint
		 */
		newpojo.getChildCreatorObject()
				.getChildComposite_WorkSpace()
				.addPaintListener(new PaintListener() {

					@Override
					public void paintControl(PaintEvent e) {
						int width = 1;
						int height = 1;
						System.out.println("CHECKING FOR RESIZE FROM PAINT EVENT ...");
						 int newWidth = c_ws.getSize().x;
						 int newHeight = c_ws.getSize().y;
						 System.out.println("\n\tnewWidth = " + newWidth + "\n\tnewHiehgt = " + newHeight);
					     // + 10 for a little padding after the composite for convenience  
						 if (newWidth != width) {
					    	  System.out.println("IN NEW WIDTH != WIDTH LOOP");
					        parent.setMinHeight(c_ws.computeSize(newWidth, SWT.DEFAULT).y + 10);
//					        width = newWidth;
					      }
					      if (newHeight != height) {
					    	  System.out.println("IN NEW HEIGHT != HEIGHT LOOP");
						        parent.setMinWidth(c_ws.computeSize(SWT.DEFAULT, newHeight).x + 10);
//						        height = newHeight;
						      }
						
						String filename = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
						
						RelayComposites object = new RelayComposites(filename);
						System.out.println("In paint event");
						object.reDraw();
					}
				});

		/**
		 * Temp listener to check mouse down event's location and save function
		 */
		final int okw = parent.getMinWidth();
		final int okh = parent.getMinHeight();
		final int cwsw = c_ws.getBounds().width;
		final int cwsh = c_ws.getBounds().height;
		newpojo.getChildCreatorObject()
				.getChildComposite_WorkSpace()
				.addListener(SWT.MouseDown, new Listener() {

					@Override
					public void handleEvent(Event event) {

						// trigger the setDirty and mark the editor dirty
//						setDirty(true);
//						System.out.println("The editor is now dirty");

						// get the event location
						System.out.println("event x " + event.x);
						System.out.println("event y " + event.y);
						System.out.println("SC MIN WIDTH: " +  okw);
						System.out.println("SC MIN HEIGHT: " + okh);
						System.out.println("CWS WIDTH: " + cwsw);
						System.out.println("CWS HEIGHT: " + cwsh);

					}
				});

		/**
		 * Test to see if mouse entered event moves across different focuses
		 */
		final EditorPart editorPart = this;
		newpojo.getChildCreatorObject()
				.getChildComposite_WorkSpace()
				.addListener(SWT.MouseEnter, new Listener() {

					@Override
					public void handleEvent(Event event) {

						System.out.println("mouse entered the editor " + this);
						System.out.println("get part name"
								+ editorPart.getPartName());
						System.out.println("get part title "
								+ editorPart.getTitle());
						
						IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getPages();
						IWorkbenchPage page = pagelist[0];
						IEditorReference[] referencelist = page.getEditorReferences();
						
						String activefilename = page.getActiveEditor().getTitle();
						System.out.println("the active editor file name is " + activefilename);
						
						CompositeWrapper tempcompositewrapper = null;
						for (int i = 0; i < newpojo.getChildcompositewrappers().size(); i++){
							tempcompositewrapper = newpojo.getChildcompositewrappers().get(i);
							
							System.out.println("\nActive filename " + activefilename);
							System.out.println("\ntemp cpmposite filename " + tempcompositewrapper.getFilename());
							
							
							if (tempcompositewrapper.getFilename().equals(activefilename)){
								System.out.println("\n Active file name found " + tempcompositewrapper.getFilename() + "\n");
//								new FavoritesDropTarget(tempcompositewrapper);
							}
							else
							{
								System.out.println("\n the active file is not " + tempcompositewrapper.getFilename() + "\n");
							}
						}
						
					}
				});

		String filename = null;

		// try {
		// filename = getFileName();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		EditorMethods em = new EditorMethods();
		try {
			filename = em.getFileName();
			int index = addPage(parentComposite);
			
			newpojo.getChildCreatorObject().getChildComposite_WorkSpace().setFilename(filename);
			
			setPageText(index, filename);
			setPartName(filename);
			setTitleToolTip(filename);
			
			newpojo.getChildcompositewrappers().add(newpojo.getChildCreatorObject().getChildComposite_WorkSpace());

			//--------------------------------------------------
				POJOHolder.getInstance().getEditorsmap().put(filename, newpojo);
			//--------------------------------------------------
			
			
		} catch (Exception e) {
			System.out.println("custom exception caught");
			// e.printStackTrace();
		}
		
	}
	
	/**
	 * Create Pages
	 */
	protected void createPages() {
		/**
		 * Create work flow editor page
		 */
		PI_WorkFlow();
		
		
//		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
//		CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		

		/**
		 * If programs list is not yet created then close the editor. Programs
		 * list is a must to be created in order for this editor to work
		 */
		if (t_VariablePoJo.getInstance().getProgram_List() == null) {

			/**
			 * Show a message dialogue to tell user that the programs are still
			 * downloading
			 */
			// create dialog with ok and cancel button and info icon
			Shell shell = new Shell();

			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
					| SWT.OK);
			messageBox.setText("Programs not ready !");
			messageBox.setMessage("Programs are still being downloaded ..");
			messageBox.open();

			// close the editor
			IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getPages();
			IWorkbenchPage page = pagelist[0];

			IEditorReference[] referencelist = page.getEditorReferences();
			// IEditorPart editorPart = referencelist[0].getEditor(false);
			// page.closeEditor(editorPart, true);

			for (int i = 0; i < referencelist.length; i++) {
				IEditorPart editorPart = referencelist[i].getEditor(false);
				page.closeEditor(editorPart, true);
			}

		}
//
//		/**
//		 * Check if recreate is possible (if the work flow was save before
//		 * quitting)
//		 */
//		disposeall();
		try {
			checkForReCreate();
//			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		refresh();

//		// set selection listeners for the editor
//		EditorMethods em = new EditorMethods();
//		em.setSelectionListeners();
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
		
//		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
//		CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		
		new FavoritesDropTarget(t_VariablePoJo.getInstance()
				.getChildCreatorObject().getChildComposite_WorkSpace());
	}

	/**
	 * Get the file name
	 * 
	 * @return String filename
	 * @throws IOException
	 */
	public String getFileName() throws IOException {

		/**
		 * $$$$$$$ Need to change $$$$$$$$ this should access page selection
		 * from platform ui and then get the file name from there
		 */

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

		/**
		 * $$$$$$$$ Need to work on $$$$$$$$$$$$ Page.getselection gives us the
		 * current selection ie the current file name on which the double click
		 * event occurred. grab the file name and then get the respective editor
		 * and open it
		 */
		System.out.println("pagelist size " + pagelist.length);
		System.out.println("page selection " + page.getSelection());
		ISelection selection = page.getSelection();
		System.out.println("selection string " + selection.toString());
		/**
		 * ------------------------------------------------------------------
		 */

		EditorMethods em = new EditorMethods();
		String filename = em.getFileName();

		String path = em.getPath(page, filename);

		// IEditorReference[] referencelist = page.getEditorReferences();
		// String fileName = null;
		// System.out.println("test ...");
		// IFile ifile = (IFile) referencelist[0].getEditorInput().getAdapter(
		// IFile.class);
		// System.out.println("absolute path " + ifile.getName());
		// String path = ifile.getRawLocation().toOSString();
		// System.out.println("path: " + path);
		//
		// System.out.println("editor reference list " + referencelist.length);

		// try {
		// System.out.println(referencelist[0].getEditorInput().getName());
		// fileName = referencelist[0].getEditorInput().getName();
		// } catch (PartInitException e1) {
		// e1.printStackTrace();
		// }

		/**
		 * The readJSONFile will return false if the file is not readable of if
		 * there is no such file. If false we do no recreate it.
		 */
		
//		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
//		CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));		
		
		DataPOJO.getInstance().getPrograms_data().clear();
		
		JSONRead reader = new JSONRead(filename);
		boolean isFile = reader.readJSONFile(path);
		t_VariablePoJo.getInstance().setFile(isFile);
		if (t_VariablePoJo.getInstance().isFile() == true) {
			
			ReCreate obj = new ReCreate();
			obj.recreateWorkFlow(filename);
		}

		RelayComposites object = new RelayComposites(filename);
		object.reDraw();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
//		disposeall();
		super.dispose();
	}

//	public void disposeall() {
//		
//		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
//		CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
//		
//		dataobj.getCompositeList()
//				.removeAll(dataobj.getCompositeList());
//		dataobj.getConnectorList()
//				.removeAll(dataobj.getConnectorList());
//		dataobj
//				.getConnectorDetectableList()
//				.removeAll(
//						dataobj.getConnectorDetectableList());
//		DataPOJO.getInstance().getPrograms_data()
//				.removeAll(DataPOJO.getInstance().getPrograms_data());
//	}

	/**
	 * Saves the workflow editor and set the dirty state to false
	 */
	public void doSave(IProgressMonitor monitor) {
		// System.out.println("editor is " + getEditor(0));
		// getEditor(0).doSave(monitor);

		/**
		 * call save & set isDirty to false
		 */
		new WorkFlowSave().save();
		setDirty(false);
	}

	/**
	 * Saves the PI Work Flow's document as another file. See if you need this
	 */
	public void doSaveAs() {
//		IEditorPart editor = getEditor(0);
//		editor.doSaveAs();
//		setPageText(0, editor.getTitle());
//		setInput(editor.getEditorInput());
		setDirty(true);
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
