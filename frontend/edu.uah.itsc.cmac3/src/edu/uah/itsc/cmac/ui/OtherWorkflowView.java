/**
 * 
 */
package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class OtherWorkflowView extends ViewPart {

	private TreeViewer		viewer;
	private static Image	folderImage;
	// Session directory for other workflows. This directory does not seem to be deleted. Need to delete it properly.
	private static File		sessionOtherWorkflowDir;
	private static Image	newWorkflowImage;
	private Action			newWorkflowAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		createImages();
		if (folderImage == null)
			folderImage = createImage("icons/folder.png");
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new OtherWorkflowContentProvider());
		viewer.setLabelProvider(new OtherWorkflowLabelProvider());
		refreshOtherWorkflows();
		viewer.setInput(sessionOtherWorkflowDir.listFiles());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
				if (obj instanceof File) {
					File file = (File) obj;
					if (file.isDirectory() && file.getParentFile().equals(sessionOtherWorkflowDir)
						&& file.list().length == 0)
						newWorkflowAction.setEnabled(true);
				}

			}
		});

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		setTitleToolTip("Experiments on which you don't have any workflows yet. You have not created any workflows in these experiments.");
	}

	private Object createotherDirectories() {
		if (sessionOtherWorkflowDir == null)
			sessionOtherWorkflowDir = Utilities.createTempDir("otherWorkflowDir", false);
		Set<String> otherBuckets = Utilities.getOtherBuckets();
		File[] files = new File[otherBuckets.size()];
		int i = 0;
		for (String bucket : otherBuckets) {
			File file = new File(sessionOtherWorkflowDir + "/" + bucket);
			if (!file.exists())
				file.mkdirs();
			files[i] = file;
			i++;
		}
		return files;
	}

	class OtherWorkflowContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (File[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof File) {
				File file = (File) parentElement;
				if (!file.exists())
					file.mkdirs();
				return file.listFiles();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof File)
				return ((File) element).getParentFile();
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof File)
				return ((File) element).list().length != 0;
			return false;
		}

	}

	class OtherWorkflowLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			StyledString text = new StyledString();
			File file = (File) cell.getElement();
			cell.setImage(folderImage);
			text.append(file.getName());
			cell.setText(text.toString());
			super.update(cell);
		}

	}

	private Image createImage(String path) {
		Bundle bundle = FrameworkUtil.getBundle(OtherWorkflowLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		return imageDcr.createImage();
	}

	private void createImages() {
		if (newWorkflowImage == null)
			newWorkflowImage = createImage("icons/new_workflow.png");
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				OtherWorkflowView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(newWorkflowAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(newWorkflowAction);
	}

	private void makeActions() {
		newWorkflowAction = new Action() {
			public void run() {
				newWorkflow();
			}
		};
		newWorkflowAction.setText("New Workflow");
		newWorkflowAction.setToolTipText("Create a new workflow");
		newWorkflowAction.setImageDescriptor(new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return newWorkflowImage.getImageData();
			}
		});
		newWorkflowAction.setEnabled(false);
	}

	private void createInNavigator(String bucketName, String workflowName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName);
		try {
			if (!project.exists()) {
				project.create(null);
			}
			project.open(null);
			IFolder folder = project.getFolder(workflowName);
			if (!folder.exists())
				folder.create(true, false, null);
			String folderPath = folder.getLocation().toString();
			File folderFile = new File(folderPath);
			if (!folderFile.exists())
				folderFile.mkdirs();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void newWorkflow() {
		ITreeSelection selection = (ITreeSelection) viewer.getSelection();
		Object obj = selection.getFirstElement();
		if (obj instanceof File) {
			final String bucketName = ((File) obj).getName();

			final Shell shell = new Shell();
			shell.setText("Enter workflow name");
			shell.setLayout(new GridLayout(2, false));
			GridData gridData = new GridData();
			gridData.heightHint = 400;
			gridData.widthHint = 400;

			shell.setLayoutData(gridData);
			Label space0 = new Label(shell, SWT.NONE);
			space0.setLayoutData(gridData);
			Label space01 = new Label(shell, SWT.NONE);
			space01.setLayoutData(gridData);
			Label workflowLabel = new Label(shell, SWT.NONE);
			workflowLabel.setText("Workflow name");
			final Text workflowText = new Text(shell, SWT.BORDER);
			workflowText.setMessage("Enter new workflow name");
			gridData.heightHint = 20;
			gridData.widthHint = 200;
			workflowText.setLayoutData(gridData);
			Label space = new Label(shell, SWT.NONE);
			space.setLayoutData(gridData);
			Label space2 = new Label(shell, SWT.NONE);
			space2.setLayoutData(gridData);
			Button finishButton = new Button(shell, SWT.NONE);
			finishButton.setText("Finish");
			gridData.widthHint = 150;
			finishButton.setLayoutData(gridData);
			finishButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String workflow = workflowText.getText();
					if (workflow.isEmpty()) {
						MessageBox message = new MessageBox(shell);
						message.setMessage("Please provide a valid workflow name");
						message.open();
						return;
					}
					super.widgetSelected(e);
					try {
						createInNavigator(bucketName, workflow);
						String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + "/"
							+ bucketName;
						GITUtility.createLocalRepo(workflow, path);
						refreshOtherWorkflows();
					}
					catch (IOException exception) {
						System.out.println("Yes I caught it");
						MessageDialog.openError(shell, "Error!!", exception.getMessage());
						return;
					}
					shell.pack();
					shell.close();

					try {
						IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName)
							.getFolder(workflow);
						folder.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (CoreException e1) {
						e1.printStackTrace();
					}

				}
			});

			Button cancelButton = new Button(shell, SWT.NONE);
			cancelButton.setLayoutData(gridData);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					shell.close();
				}
			});
			Label space3 = new Label(shell, SWT.NONE);
			space.setLayoutData(gridData);
			Label space4 = new Label(shell, SWT.NONE);
			space2.setLayoutData(gridData);

			shell.pack();
			// shell.setSize(450, 200);
			shell.open();
		}
	}

	public void removeFromOtherWorkflow(String dirName) {
		File[] dirs = sessionOtherWorkflowDir.listFiles();
		for (File dir : dirs) {
			if (dir.getName().equals(dirName)) {
				dir.delete();
				break;
			}
		}

	}

	public void refreshOtherWorkflows() {
		if (sessionOtherWorkflowDir == null)
			sessionOtherWorkflowDir = Utilities.createTempDir("otherWorkflowDir", false);
		Utilities.deleteRecursive(sessionOtherWorkflowDir);
		createotherDirectories();
		viewer.setInput(sessionOtherWorkflowDir.listFiles());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

}
