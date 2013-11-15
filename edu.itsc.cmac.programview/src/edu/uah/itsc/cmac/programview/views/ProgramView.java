package edu.uah.itsc.cmac.programview.views;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Program;

public class ProgramView extends ViewPart {
	public static final String ID = "edu.uah.itsc.cmac3.ui.Tableview";
	private TableViewer tableviewer;
	private Action openProgramView;

	public ProgramView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		dragAndDrop(parent);
		makeActions();
	}

	@Override
	public void setFocus() {

	}

	private void makeActions() {
		openProgramView = new Action() {
			private IViewPart showView;

			public void run() {
				try {
					showView = PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.showView(
									"edu.uah.itsc.programformview.views.ProgramFormView");
					
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		openProgramView.setText("Add Program");
		openProgramView.setToolTipText("Add Program");
		openProgramView.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = bars.getToolBarManager();
		toolBarManager.add(openProgramView);
	}

	private void dragAndDrop(Composite parent) {
		IEditorPart myeditor = null;
		try {
			myeditor = PlatformUI
					.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.openEditor(new StringEditorInput(""),
							"org.eclipse.ui.DefaultTextEditor");

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		ArrayList<Program> progs = getPrograms();

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		// For Drag
		tableviewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableviewer.addDragSupport(operations, transferTypes,
				new ProgramDragListener(tableviewer, progs));
		for (int i = 0; i < progs.size(); i++) {

			if (progs.get(i) != null)
				tableviewer.add(progs.get(i).getTitle());
		}
	}

	private ArrayList<Program> getPrograms() {
		String data = PortalUtilities.getDataFromURL(PortalUtilities
				.getNodeRestPoint() + "?parameters[type]=program");
		ArrayList<Program> programs = new ArrayList<Program>();
		try {
			JSONArray jsonData = new JSONArray(data);
			int i = 0, numElements = jsonData.length();
			for (i = 0; i < numElements; i++) {
				JSONObject jsonElement = (JSONObject) jsonData.get(i);
				Program program = new Program(jsonElement.getString("title"),
						jsonElement.getString("uri"));
				programs.add(program);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// System.out.println(data);
		return programs;
	}

}
