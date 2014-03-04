package edu.uah.itsc.cmac.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);

		// layout.addStandaloneView("example.view", true /* show title */,
		// IPageLayout.LEFT, 0.25f, editorArea);
		// IFolderLayout left = layout.createFolder("left",
		// IPageLayout.LEFT, (float) 0.26, editorArea);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.35, editorArea);
		// IFolderLayout left2 = layout.createFolder("left",
		// IPageLayout.LEFT, (float) 0.35, editorArea);
		left.addView("edu.uah.itsc.cmac.NavigatorView");
		// left2.addView("edu.uah.itsc.cmac.PersonalNavigatorView");

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.80, editorArea);

		bottom.addView("org.eclipse.ecf.presence.ui.MultiRosterView");
		bottom.addView("edu.uah.itsc.cmac.programview.views.ProgramView");
		bottom.addView("edu.uah.itsc.cmac.ami.views.AMIView");
		bottom.addView("edu.uah.itsc.cmac.ec2instance.views.EC2InstanceView");
		bottom.addView("edu.uah.itsc.cmac.s3jgitview.views.S3jgitView");
		

		// left.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		// left.addView("org.eclipse.ui.browser.view");
		// layout.addView("org.eclipse.ui.browser.view", IPageLayout.BOTTOM,
		// (float)0.55, editorArea);

		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.browser.view","",
		// IWorkbenchPage.VIEW_ACTIVATE);
		// } catch (PartInitException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// layout.addView("org.eclipse.ui.browser.view", IPageLayout.LEFT,
		// 0.5f, IPageLayout.ID_EDITOR_AREA);
	}

}
