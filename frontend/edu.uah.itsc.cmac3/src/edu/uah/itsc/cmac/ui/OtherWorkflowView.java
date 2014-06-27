/**
 * 
 */
package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author sshrestha
 * 
 */
public class OtherWorkflowView extends ViewPart {

	private TreeViewer		viewer;
	private static Image	folderImage;
	private static File		sessionOtherWorkflowDir;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		if (sessionOtherWorkflowDir == null)
			sessionOtherWorkflowDir = Utilities.createTempDir("otherWorkflowDir");
		sessionOtherWorkflowDir.deleteOnExit();
		if (folderImage == null)
			folderImage = createImage("icons/folder.png");
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new SharedWorkflowContentProvider());
		viewer.setLabelProvider(new SharedWorkflowLabelProvider());
		createotherDirectories();
		viewer.setInput(sessionOtherWorkflowDir.listFiles());
	}

	private Object createotherDirectories() {
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
			// if (element instanceof Workflow) {
			// return true;
			// }
			// else
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

	// private void refreshCommunityResourceFromPortal() {
	// clearCommunityResource();
	// String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getWorkflowFeedURL() + "?field_is_shared=1");
	// JSONParser parser = new JSONParser();
	// Object obj;
	// try {
	// obj = parser.parse(jsonText);
	// JSONObject workflows = (JSONObject) obj;
	//
	// if (workflows == null)
	// return;
	// JSONArray workFlowArray = (JSONArray) workflows.get("workflows");
	// if (workFlowArray == null || workFlowArray.size() == 0)
	// return;
	// for (int i = 0; i < workFlowArray.size(); i++) {
	// JSONObject workflow = (JSONObject) workFlowArray.get(i);
	// workflow = (JSONObject) workflow.get("workflow");
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put("nid", workflow.get("nid").toString());
	// map.put("path", workflow.get("path").toString());
	// map.put("title", workflow.get("title").toString());
	// map.put("description", workflow.get("description").toString());
	// map.put("keywords", workflow.get("keywords").toString());
	// createSharedFolder(map);
	// map = null;
	// }
	// return;
	// }
	// catch (ParseException e) {
	// e.printStackTrace();
	// System.out.println("Unable to parse json object");
	// return;
	// }
	// }
	//
	public void refreshCommunityResource() {
		// refreshCommunityResourceFromPortal();
	}

	// public void clearCommunityResource() {
	// try {
	// cmacCommunity.delete(true, null);
	// }
	// catch (CoreException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// private IFolder createSharedFolder(HashMap<String, String> map) {
	// try {
	// if (!cmacCommunity.exists()) {
	// cmacCommunity.create(null);
	// cmacCommunity.open(null);
	// }
	// IFolder sharedFolder = cmacCommunity.getFolder(map.get("path").replaceFirst("/", ""));
	// // createFolder(sharedFolder);
	// return sharedFolder;
	// }
	// catch (CoreException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// private void createFolder(IFolder sharedFolder) throws CoreException {
	// if (!sharedFolder.getParent().exists() && sharedFolder.getParent() != sharedFolder.getProject())
	// createFolder((IFolder) sharedFolder.getParent());
	// if (!sharedFolder.exists())
	// sharedFolder.create(true, true, null);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

}
