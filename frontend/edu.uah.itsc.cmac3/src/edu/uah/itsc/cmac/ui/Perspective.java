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

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.35, editorArea);
		IFolderLayout leftTop = layout.createFolder("leftTop", IPageLayout.TOP, (float) 0.125, "left");
		IFolderLayout leftMiddle = layout.createFolder("leftMiddle", IPageLayout.BOTTOM, (float) 0.30, "left");
		IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.BOTTOM, (float) 0.50, "leftMiddle");

		leftTop.addView("edu.uah.itsc.cmac.searchview.views.SearchView");
		left.addView("edu.uah.itsc.cmac.ui.SharedWorkflowView");
		leftMiddle.addView("edu.uah.itsc.cmac.NavigatorView");
		leftBottom.addView("edu.uah.itsc.cmac.ui.OtherWorkflowView");

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.80, editorArea);
		bottom.addView("edu.uah.itsc.cmac.programview.views.ProgramView");
		bottom.addView("org.eclipse.ecf.presence.ui.MultiRosterView");
		bottom.addView("edu.uah.itsc.cmac.ami.views.AMIView");
		bottom.addView("edu.uah.itsc.cmac.ec2instance.views.EC2InstanceView");

	}

}
