/**
 * 
 */
package edu.uah.itsc.cmac.ec2instance.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

import edu.uah.itsc.aws.EC2;

/**
 * @author sshrestha
 *
 */
public class EC2InstanceView extends ViewPart {
	private TableViewer viewer;
	private Button stopButton;
	private Button startButton;
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		EC2 amazonEC2 = new EC2();
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createWidgets(parent, amazonEC2);
		createTable(parent, amazonEC2);
		getSite().setSelectionProvider(viewer);
	}

	/**
	 * @param parent
	 */
	private void createWidgets(final Composite parent, final EC2 amazonEC2) {
		GridData layoutData = new GridData();

//		Composite addComposite = new Composite(parent, SWT.NONE);
//		addComposite.setLayout(new GridLayout(5, false));
//		Label nameLabel = new Label(addComposite, SWT.NONE);
//		nameLabel.setText("AMI Name");
//		final Text nameText = new Text(addComposite, SWT.BORDER);
//
//		Label instanceLabel = new Label(addComposite, SWT.NONE);
//		instanceLabel.setText("Instance ID");
//		final Text instanceText = new Text(addComposite, SWT.BORDER);
//
//		layoutData.widthHint = 400;
//		addComposite.setLayoutData(layoutData);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3, false));
		startButton = new Button(buttonComposite, SWT.PUSH);
		startButton.setText("Start");
		Image image = new Image(parent.getDisplay(), getClass().getClassLoader().getResourceAsStream("icons/start.png"));
		startButton.setImage(image);
		startButton.setEnabled(false);
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				Table table = viewer.getTable();
				TableItem selectedItems[] = table.getSelection();
				List<String> instanceIds = new ArrayList<String>();
				boolean userConfirmation = MessageDialog.openConfirm(
						parent.getShell(),
						"Warning! Start Instances!!",
						"Are you sure you want to start the selected instance(s)?");
				if (userConfirmation)
					for (TableItem tableItem : selectedItems) {
						Instance instance = (Instance) tableItem.getData();
						System.out.println(instance.getInstanceId());
						instanceIds.add(instance.getInstanceId());
						StartInstancesResult result = amazonEC2.startInstances(instanceIds);
						viewer.refresh();
					}
			}
		});

		stopButton = new Button(buttonComposite, SWT.PUSH);
		stopButton.setText("Stop");
		image = new Image(parent.getDisplay(), getClass().getClassLoader().getResourceAsStream("icons/stop.png"));
		stopButton.setImage(image);
		stopButton.setEnabled(false);
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				Table table = viewer.getTable();
				TableItem selectedItems[] = table.getSelection();
				List<String> instanceIds = new ArrayList<String>();
				boolean userConfirmation = MessageDialog.openConfirm(
						parent.getShell(),
						"Warning! Stop Instances!!",
						"Are you sure you want to stop the selected instance(s)?");
				if (userConfirmation)
					for (TableItem tableItem : selectedItems) {
						Instance instance = (Instance) tableItem.getData();
						System.out.println(instance.getInstanceId());
						instanceIds.add(instance.getInstanceId());
						StopInstancesResult result = amazonEC2.stopInstances(instanceIds);
						viewer.refresh();
					}
			}
		});

		Button refreshButton = new Button(buttonComposite, SWT.PUSH);
		refreshButton.setText("Refresh");
		image = new Image(parent.getDisplay(), getClass().getClassLoader().getResourceAsStream("icons/refresh.png"));
		refreshButton.setImage(image);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				refreshInstances(amazonEC2);
			}


		});
		buttonComposite.setLayoutData(layoutData);
	}

	/**
	 * @param amazonEC2
	 * @param layoutData
	 */
	private void createTable(Composite parent, EC2 amazonEC2) {
		GridData layoutData = new GridData();
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(viewer);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		layoutData.horizontalSpan = 2;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.TOP;
		table.setLayoutData(layoutData);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(amazonEC2.getInstances(null));

		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Instance instance = (Instance) e.item.getData();
				System.out.println("selected: " + e.item + " name: "
						+ instance.getInstanceId());
				startButton.setEnabled(true);
				stopButton.setEnabled(true);

			}
		});
	}

	public void createColumns(final TableViewer viewer) {
		String[] titles = { "Name", "Instance ID", "Instance Type", "Instance State", "Status Checks",
				"Public DNS", "Public IP", "Key Name" };
		int[] bounds = { 100, 75, 100, 100, 100, 300, 100, 100 };


		// Name
		TableViewerColumn column = createTableViewerColumn(titles[0],
				bounds[0], 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				List<Tag> tags = instance.getTags();
				for (Tag tag : tags) {
					if (tag.getKey().equalsIgnoreCase("name"))
						return tag.getValue();
				}
				return instance.getInstanceId();
			}
		});

		// Instance ID
		column = createTableViewerColumn(titles[1], bounds[1], 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getInstanceId();
			}
		});

		// Instance Type
		column = createTableViewerColumn(titles[2], bounds[2], 2);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getInstanceType();
			}
		});

		// Instance State
		column = createTableViewerColumn(titles[3], bounds[3], 3);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getState().getName();
			}
		});

		// Status Checks
		column = createTableViewerColumn(titles[4], bounds[4], 4);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getInstanceId();
			}
		});

		// Public DNS
		column = createTableViewerColumn(titles[5], bounds[5], 5);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getPublicDnsName();
			}
		});

		// Public IP
		column = createTableViewerColumn(titles[6], bounds[6], 6);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getPublicIpAddress();
			}
		});

		// Key Name
		column = createTableViewerColumn(titles[7], bounds[7], 7);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Instance instance = (Instance) element;
				return instance.getKeyName();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	private void refreshInstances(EC2 amazonEC2) {
		viewer.setInput(amazonEC2.getInstances(null));
		viewer.refresh();
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
	}
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
