package edu.uah.itsc.cmac.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.cmac.portal.PortalUser;
import edu.uah.itsc.cmac.portal.PortalUtilities;

public class AllowCloneCommandHandler extends AbstractHandler implements IHandler {
	private IStructuredSelection		selection	= StructuredSelection.EMPTY;
	private IFolder						selectedFolder;
	private HashSet<PortalUser>			grantees	= new HashSet<PortalUser>();
	private HashMap<Button, PortalUser>	buttons		= new HashMap<Button, PortalUser>();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		if (selection.size() == 1) {
			final Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IFile) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
					"You can only select workflows!");
			}
			else if (firstElement instanceof IFolder) {
				selectedFolder = (IFolder) firstElement;
				IFolder gitFolder = selectedFolder.getFolder(".git");
				if (!gitFolder.exists()) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"You can only select tracking workflows!");
					return null;
				}

				System.out.println("Finally here");
				createWindow();

			}
		}
		return null;
	}

	private void createWindow() {
		final Shell shell = new Shell(Display.getDefault().getActiveShell());
		shell.setText(selectedFolder.getName() + " - Allow cloning to users");
		shell.setLayout(new GridLayout(1, true));

		Label userLabel = new Label(shell, SWT.NONE);
		userLabel.setText("Check users to grant clone access");

		Label emptyLabel1 = new Label(shell, SWT.NONE);

		Composite userComposite = new Composite(shell, SWT.BORDER);
		GridLayout layout = new GridLayout(8, false);
		userComposite.setLayout(layout);

		HashSet<PortalUser> portalUserList = PortalUtilities.getUserList();
		Iterator<PortalUser> iter = portalUserList.iterator();
		while (iter.hasNext()) {
			Label nameLabel = new Label(userComposite, SWT.NONE);
			final PortalUser user = (PortalUser) iter.next();
			nameLabel.setText(user.getUsername());

			Button checkButton = new Button(userComposite, SWT.CHECK);
			checkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if (button.getSelection())
						grantees.add(user);
					else
						grantees.remove(user);
				}
			});
			buttons.put(checkButton, user);
		}

		Label emptyLabel2 = new Label(shell, SWT.NONE);

		if (!portalUserList.isEmpty()) {
			Composite buttonComposite = new Composite(shell, SWT.NONE);
			buttonComposite.setLayout(new GridLayout(2, true));

			Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
			selectAllButton.setText("Select All");
			selectAllButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Map.Entry<Button, PortalUser> entry : buttons.entrySet()) {
						Button button = entry.getKey();
						PortalUser user = entry.getValue();
						grantees.add(user);
						button.setSelection(true);
					}
				}
			});

			Button selectNoneButton = new Button(buttonComposite, SWT.PUSH);
			selectNoneButton.setText("Select None");
			selectNoneButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Map.Entry<Button, PortalUser> entry : buttons.entrySet()) {
						Button button = entry.getKey();
						PortalUser user = entry.getValue();
						grantees.remove(user);
						button.setSelection(false);
					}
				}
			});

			Button okButton = new Button(buttonComposite, SWT.PUSH);
			okButton.setText("OK");
			okButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Iterator<PortalUser> iter = grantees.iterator();
					while (iter.hasNext()) {
						PortalUser user = iter.next();
						System.out.println(user.getUsername());
					}
				}
			});

			Button cancelButton = new Button(buttonComposite, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					shell.close();
				}
			});
		}
		shell.pack();
		shell.open();

	}

}
