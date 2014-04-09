/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class InitTrackingWorkflowHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = selection.getFirstElement();

		final IFolder folder = (IFolder) firstElement;
		final String path = folder.getLocation().toOSString();

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
					GITUtility.createLocalRepo(workflow, path);
				}
				catch (IOException exception) {
					System.out.println("Yes I caught it");
					MessageDialog.openError(shell, "Error!!", exception.getMessage());
					return;
				}
				shell.pack();
				shell.close();
				
				try {
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
//		shell.setSize(450, 200);
		shell.open();

		return null;
	}
}
