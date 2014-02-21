package edu.uah.itsc.cmac.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.resources.GoIntoActionProvider;

/**
 * Create the New actions and register then globally in the workbench using CMACEditActionProvider.
 * <p/>
 * Then, removes the contributions in the pop-up menu.
 */
public class CMACGoIntoActionProvider extends GoIntoActionProvider {
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);

		System.out.println("Goto Action Provider--------------");

		IContributionItem newItemRemoved = menu.remove("org.eclipse.ui.framelist.goInto");

	}
}