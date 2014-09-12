package edu.uah.itsc.cmac.glm.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		//layout.addStandaloneView("example.view",  true /* show title */, IPageLayout.LEFT, 0.25f, editorArea);
		
//		 IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);
//		 IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, (float) 0.75, editorArea);
		 IFolderLayout top = layout.createFolder("top", IPageLayout.LEFT, (float) 0.75, editorArea);
//		 IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, "top");
		 IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.70, "top");
		 IFolderLayout topleft = layout.createFolder("topleft", IPageLayout.LEFT, (float) 0.75, "top");
		 IFolderLayout topright = layout.createFolder("topright", IPageLayout.RIGHT, (float) 0.25, editorArea);
//		 topleft.addView("LayerView");
		 IFolderLayout topleft_l = layout.createFolder("topleft_l", IPageLayout.LEFT, (float) 0.50, "topleft");
		 IFolderLayout topleft_r = layout.createFolder("topleft_r", IPageLayout.RIGHT, (float) 0.50, "topleft");
		 topleft_l.addView("DataView");
		 topleft_r.addView("ValidationView");
		 topright.addView("MetricsView");
//		 IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.45, editorArea);
//		 IFolderLayout bottomleft = layout.createFolder("bottomleft", IPageLayout.LEFT, (float) 0.45, "bottom");
//		 IFolderLayout bottomright = layout.createFolder("bottomright", IPageLayout.RIGHT, (float) 0.55, "bottom");
		 IFolderLayout bottomright = layout.createFolder("bottomright", IPageLayout.RIGHT, (float) 0.50, "bottom");
//		 bottom.addView(IPageLayout.ID_PROJECT_EXPLORER);

		 bottom.addView(IPageLayout.ID_RES_NAV);
//		 bottom.addView(IPageLayout.ID_PROP_SHEET);
//		 bottom.addView("NavigationView");
		 bottomright.addView("TimelineView");
		 
//		 DataView dataView;
//		 ValidationView validationView;
//		 while((dataView = (DataView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("DataView"))==null);
//		 dataView.linkValidationView();
//		 while ((validationView = (ValidationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("ValidationView"))==null);
//		 validationView.linkDataView();

//		 ((DataView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("DataView")).linkValidationView();
//		 ((ValidationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("ValidationView")).linkDataView();
//		 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("ValidationView")
	
	}
}
