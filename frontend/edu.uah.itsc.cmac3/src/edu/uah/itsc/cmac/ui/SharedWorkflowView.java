/**
 * 
 */
package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.util.FileUtility;
import edu.uah.itsc.cmac.util.GITUtility;
import edu.uah.itsc.cmac.util.PropertyUtility;

/**
 * @author sshrestha
 * 
 */
public class SharedWorkflowView extends ViewPart {

	private TreeViewer		viewer;
	private static Image	sharedImage;
	private static Image	folderImage;
	private static Image	userImage;
	private static Image	refreshImage;
	private static Image	importImage;
	private static File		sessionSharedWorkflowDir;
	private Action			refreshCommunityAction;
	private Action			importWorkflowAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		if (sessionSharedWorkflowDir == null)
			sessionSharedWorkflowDir = Utilities.createTempDir("sharedWorkflowDir");
		sessionSharedWorkflowDir.deleteOnExit();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new SharedWorkflowContentProvider());
		viewer.setLabelProvider(new SharedWorkflowLabelProvider());
		createSharedDirectories();
		viewer.setInput(sessionSharedWorkflowDir.listFiles());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
				if (obj instanceof File) {
					File file = (File) obj;
					if (file.getParentFile().getName().equalsIgnoreCase(sessionSharedWorkflowDir.getName()))
						refreshCommunityAction.setEnabled(true);
					else
						refreshCommunityAction.setEnabled(false);
					if (file.list().length == 0)
						importWorkflowAction.setEnabled(true);
					else
						importWorkflowAction.setEnabled(false);
				}
			}
		});
		createImages();
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void createImages() {
		if (sharedImage == null)
			sharedImage = createImage("icons/shared.png");
		if (folderImage == null)
			folderImage = createImage("icons/folder.png");
		if (userImage == null)
			userImage = createImage("icons/user.png");
		if (refreshImage == null)
			refreshImage = createImage("icons/refresh-16x16.png");
		if (importImage == null)
			importImage = createImage("icons/import.png");
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SharedWorkflowView.this.fillContextMenu(manager);
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
		manager.add(refreshCommunityAction);
		manager.add(importWorkflowAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshCommunityAction);
		manager.add(importWorkflowAction);
	}

	private void makeActions() {
		refreshCommunityAction = new Action() {
			public void run() {
				refreshCommunityResource();
			}
		};
		refreshCommunityAction.setText("Refresh");
		refreshCommunityAction.setToolTipText("Refresh shared experiments");
		refreshCommunityAction.setImageDescriptor(new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return refreshImage.getImageData();
			}
		});

		importWorkflowAction = new Action() {
			public void run() {
				importWorkflow();
			}
		};
		importWorkflowAction.setText("Import");
		importWorkflowAction.setToolTipText("Import selected workflow");
		importWorkflowAction.setImageDescriptor(new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return importImage.getImageData();
			}
		});
		refreshCommunityAction.setEnabled(false);
		importWorkflowAction.setEnabled(false);

	}

	private void importWorkflow() {
		ITreeSelection selection = (ITreeSelection) viewer.getSelection();
		Object obj = selection.getFirstElement();
		if (obj instanceof File) {
			File file = (File) obj;
			if (file.list().length > 0) {
				showMessage("Cannot import " + file.getName(), "error");
				return;
			}
			String workflowName = file.getName();
			final String creator = file.getParentFile().getName();
			final String bucketName = file.getParentFile().getParentFile().getName();

			final String remotePath = "amazon-s3://.jgit@cmac-community/" + bucketName + "/" + creator + "/"
				+ workflowName + ".git";
			final String localPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/"
				+ bucketName + "/" + User.username + "/" + workflowName;

			createInNavigator(bucketName, workflowName);
			Job job = new Job("Importing..") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						GITUtility.cloneRepository(localPath, remotePath);
						setOwnerProperty(localPath, creator);

						IFolder userFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName)
							.getFolder(User.username);
						userFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (Exception e) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
									"Error while importing");
							}
						});
					}
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	private void setOwnerProperty(final String localPath, final String repoOwner) throws IOException {
		String workflowPropertyFileName = localPath + "/.cmacworkflow";
		String gitIgnoreFileName = localPath + "/.gitignore";
		File propFile = new File(workflowPropertyFileName);
		if (!propFile.exists())
			propFile.createNewFile();

		File gitIgnoreFile = new File(gitIgnoreFileName);
		if (!gitIgnoreFile.exists()) {
			gitIgnoreFile.createNewFile();
			FileUtility.writeTextFile(gitIgnoreFileName, ".cmacworkflow");
		}

		PropertyUtility propUtil = new PropertyUtility(workflowPropertyFileName);
		propUtil.setValue("owner", repoOwner);
	}

	private void createInNavigator(String bucketName, String workflowName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName);
		try {
			if (!project.exists()) {
				project.create(null);
			}
			project.open(null);
			IFolder folder = project.getFolder(User.username);
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

	private void showMessage(String message, String type) {
		if (type.equalsIgnoreCase("info"))
			MessageDialog.openInformation(viewer.getControl().getShell(), "Shared Experiments", message);
		else if (type.equalsIgnoreCase("error"))
			MessageDialog.openError(viewer.getControl().getShell(), "Shared Experiments", message);
	}

	private Object createSharedDirectories() {
		HashMap<String, Workflow> sharedWorkflows = Utilities.getSharedWorkflows();
		File[] files = new File[sharedWorkflows.size()];
		int i = 0;
		for (String key : sharedWorkflows.keySet()) {
			Workflow workflow = sharedWorkflows.get(key);
			File file = new File(sessionSharedWorkflowDir + workflow.getPath());
			if (!file.exists())
				file.mkdirs();
			files[i] = file;
			i++;
		}
		return files;

	}

	class SharedWorkflowContentProvider implements ITreeContentProvider {

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

	class SharedWorkflowLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			StyledString text = new StyledString();
			File file = (File) cell.getElement();
			if (file.list().length == 0)
				cell.setImage(sharedImage);
			else if (file.getName().equalsIgnoreCase(User.username))
				cell.setImage(userImage);
			else
				cell.setImage(folderImage);
			text.append(file.getName());
			cell.setText(text.toString());
			super.update(cell);
		}

	}

	private Image createImage(String path) {
		Bundle bundle = FrameworkUtil.getBundle(SharedWorkflowLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		return imageDcr.createImage();
	}

	public void refreshCommunityResource() {
		createSharedDirectories();
		viewer.setInput(sessionSharedWorkflowDir.listFiles());
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
