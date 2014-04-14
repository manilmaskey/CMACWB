package edu.uah.itsc.cmac.versionview.views;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.uah.itsc.cmac.util.GITUtility;

public class VersionView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID	= "edu.uah.itsc.cmac.versionview.views.VersionView";

	private TableViewer			viewer;
	private Action				action1;
	private Action				action2;
	private Action				doubleClickAction;

	/**
	 * The constructor.
	 */
	public VersionView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		try {
			createTable(parent);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void createTable(Composite parent) throws GitAPIException {
		GridData layoutData = new GridData();
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		String repoName = "testTag";
		String repoPath = "c:\\projects";
		Git git = GITUtility.getGit(repoName, repoPath);

		createColumns(viewer, git);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		layoutData.horizontalSpan = 2;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.TOP;
		table.setLayoutData(layoutData);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(git.tagList().call());

		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Ref ref = (Ref) e.item.getData();
			}
		});
	}

	/**
	 * @param revWalk
	 * @param ref
	 * @return
	 */
	private RevTag getTag(final RevWalk revWalk, Ref ref) {
		ObjectId id = ref.getObjectId();
		try {
			RevTag tag = revWalk.parseTag(id);
			return tag;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void createColumns(final TableViewer viewer, Git git) {
		String[] titles = { "Version", "Created By", "Comments", "Created At" };
		int[] bounds = { 100, 200, 400, 200 };
		final RevWalk revWalk = new RevWalk(git.getRepository(), 0);

		// Tag Name
		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0], 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Ref ref = (Ref) element;
				RevTag tag = getTag(revWalk, ref);
				if (tag != null)
					return tag.getTagName();
				else
					return null;
			}
		});

		// Tagger Name
		column = createTableViewerColumn(titles[1], bounds[1], 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Ref ref = (Ref) element;
				RevTag tag = getTag(revWalk, ref);
				if (tag != null)
					return tag.getTaggerIdent().getName();
				else
					return null;
			}
		});

		// Tag message
		column = createTableViewerColumn(titles[2], bounds[2], 2);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Ref ref = (Ref) element;
				RevTag tag = getTag(revWalk, ref);
				if (tag != null)
					return tag.getShortMessage();
				else
					return null;
			}
		});

		// Tagged date
		column = createTableViewerColumn(titles[3], bounds[3], 3);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Ref ref = (Ref) element;
				RevTag tag = getTag(revWalk, ref);
				if (tag != null)
					return tag.getTaggerIdent().getWhen().toString();
				else
					return null;
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VersionView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Versions View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}