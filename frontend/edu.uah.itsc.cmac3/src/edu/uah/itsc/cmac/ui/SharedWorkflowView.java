/**
 * 
 */
package edu.uah.itsc.cmac.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;

/**
 * @author sshrestha
 * 
 */
public class SharedWorkflowView extends ViewPart {

	private TreeViewer	viewer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new SharedWorkflowContentProvider());
		viewer.setLabelProvider(new SharedWorkflowLabelProvider());
		viewer.setInput(getSharedWorkflow());
	}

	private Object getSharedWorkflow() {
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getWorkflowFeedURL() + "?field_is_shared=1");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(jsonText);
			JSONObject workflowsObj = (JSONObject) obj;

			if (workflowsObj == null)
				return null;
			JSONArray workFlowArray = (JSONArray) workflowsObj.get("workflows");
			Workflow[] workflows = new Workflow[workFlowArray.size()];
			if (workFlowArray == null || workFlowArray.size() == 0)
				return null;
			for (int i = 0; i < workFlowArray.size(); i++) {
				JSONObject workflowObj = (JSONObject) workFlowArray.get(i);
				workflowObj = (JSONObject) workflowObj.get("workflow");
				Workflow workflow = new Workflow();
				workflow.setPath(workflowObj.get("path").toString());
				workflow.setTitle(workflowObj.get("title").toString());
				workflow.setDescription(workflowObj.get("description").toString());
				workflow.setKeywords(workflowObj.get("keywords").toString());
				workflows[i] = workflow;
			}
			return workflows;
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
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
			return (Workflow[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Workflow){
				Workflow workflow = (Workflow) parentElement;
				String[] parts = workflow.getPath().split("/");
				return parts;
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Workflow){
				return true;
			}
			else
				return false;
		}

	}

	class SharedWorkflowLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			StyledString text = new StyledString();
			if (cell.getElement() instanceof Workflow){
				Workflow workflow = (Workflow) cell.getElement();
				text.append(workflow.getPath());
			}
			else
				text.append(cell.getElement().toString());
			cell.setText(text.toString());
			super.update(cell);
		}

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
