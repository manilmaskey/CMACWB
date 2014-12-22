package edu.uah.itsc.cmac.ami.views;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;

import edu.uah.itsc.aws.CustomAWSImage;
import edu.uah.itsc.aws.EC2;
import edu.uah.itsc.aws.User;

public class AMIView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID	= "edu.uah.itsc.cmac.ami.views.AMIView";

	private TableViewer			viewer;
	private Button				deleteButton;
	private Button				runButton;

	/**
	 * The constructor.
	 */
	public AMIView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		EC2 amazonEC2 = new EC2();
		GridLayout layout = new GridLayout(1, false);
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
		Composite addComposite = new Composite(parent, SWT.NONE);
		addComposite.setLayout(new GridLayout(7, false));

		GridData widgetLayoutData = new GridData();
		widgetLayoutData.widthHint = 140;
		Label nameLabel = new Label(addComposite, SWT.NONE);

		nameLabel.setText("New AMI Name");
		final Text nameText = new Text(addComposite, SWT.BORDER);
		nameText.setLayoutData(widgetLayoutData);
		Label instanceLabel = new Label(addComposite, SWT.NONE);
		instanceLabel.setText("Instance ID");
		final Text instanceText = new Text(addComposite, SWT.BORDER);
		instanceText.setLayoutData(widgetLayoutData);

		Button submitButton = new Button(addComposite, SWT.PUSH);
		submitButton.setText("Submit");
		org.eclipse.swt.graphics.Image image = new org.eclipse.swt.graphics.Image(parent.getDisplay(), getClass()
			.getClassLoader().getResourceAsStream("icons/submit.gif"));
		submitButton.setImage(image);
		submitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				boolean userConfirmation = MessageDialog.openConfirm(parent.getShell(),
					"Warning! Create new Amazon Machine Image in Amazon cloud!!",
					"Are you sure you want to create a new AMI in the cloud?");
				if (userConfirmation) {
					if (instanceText.getText().trim().isEmpty()) {
						MessageDialog.openError(parent.getShell(), "Error", "You must provide the instance ID");
						return;
					}

					if (nameText.getText().trim().isEmpty()) {
						MessageDialog.openError(parent.getShell(), "Error", "You must provide name for the AMI");
						return;
					}
					
					try {
						Regions region = amazonEC2.getInstanceRegion(instanceText.getText());
						if (region == null) {
							MessageDialog.openError(parent.getShell(), "Error", "Invalid instance ID");
							return;
						}
						createAMI(amazonEC2, nameText.getText(), instanceText.getText(), region);
						MessageDialog.openInformation(parent.getShell(), "Success", "Added new AMI successfully");
						nameText.setText("");
						instanceText.setText("");
					}
					catch (Exception exception) {
						MessageDialog.openError(parent.getShell(), "Error",
							"Unable to create new AMI.\n" + exception.getMessage());
					}
				}
			}
		});

		FontData[] fontData = submitButton.getFont().getFontData();
		nameLabel.setFont(new Font(parent.getDisplay(), fontData[0]));
		instanceLabel.setFont(new Font(parent.getDisplay(), fontData[0]));

		addComposite.setLayoutData(layoutData);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(5, false));

		Label runInstanceLabel = new Label(buttonComposite, SWT.NONE);
		runInstanceLabel.setText("New Instance name");
		runInstanceLabel.setFont(new Font(parent.getDisplay(), fontData[0]));
		final Text runInstanceText = new Text(buttonComposite, SWT.BORDER);
		runInstanceText.setLayoutData(widgetLayoutData);
		runButton = new Button(buttonComposite, SWT.PUSH);
		runButton.setText("Launch Instance");
		image = new org.eclipse.swt.graphics.Image(parent.getDisplay(), getClass().getClassLoader()
			.getResourceAsStream("icons/start.png"));
		runButton.setImage(image);
		runButton.setEnabled(false);
		runButton.setLayoutData(widgetLayoutData);
		runButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				boolean userConfirmation = MessageDialog.openConfirm(parent.getShell(),
					"Warning! Run a new instance using Amazon Machine Image in Amazon cloud!!",
					"Are you sure you want to run a new instance using selected AMI?");
				if (userConfirmation) {
					if (runInstanceText.getText().trim().isEmpty()) {
						MessageDialog.openError(parent.getShell(), "Error", "You must provide name for the instance");
						return;
					}
					try {
						Table table = viewer.getTable();
						CustomAWSImage ami = (CustomAWSImage) table.getSelection()[0].getData();
						runInstanceUsingAMI(amazonEC2, ami.getImage().getImageId(), runInstanceText.getText(),
							ami.getRegion());
						MessageDialog.openInformation(parent.getShell(), "Success",
							"Sent request to start the instance based on this. It may take a while. Refresh to see the changes.");
						runInstanceText.setText("");
					}
					catch (Exception exception) {
						MessageDialog.openError(parent.getShell(), "Error",
							"Unable to run new instance.\n" + exception.getMessage());
					}
				}
			}
		});

		deleteButton = new Button(buttonComposite, SWT.PUSH);
		deleteButton.setText("Delete AMI");
		image = new org.eclipse.swt.graphics.Image(parent.getDisplay(), getClass().getClassLoader()
			.getResourceAsStream("icons/delete.png"));
		deleteButton.setImage(image);
		deleteButton.setEnabled(false);
		deleteButton.setLayoutData(widgetLayoutData);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				Table table = viewer.getTable();
				TableItem selectedItems[] = table.getSelection();
				boolean userConfirmation = MessageDialog.openConfirm(parent.getShell(),
					"Warning! Delete Amazon Machine Images from Amazon cloud!!",
					"Are you sure you want to delete the selected AMI(s) from the cloud?");
				if (userConfirmation) {
					for (TableItem tableItem : selectedItems) {
						CustomAWSImage image = (CustomAWSImage) tableItem.getData();
						degregisterAMI(amazonEC2, image.getImage().getImageId(), image.getRegion());
						MessageDialog.openInformation(parent.getShell(), "Success",
							"Sent request to delete the AMI. It may take a while. Refresh to see the changes.");
					}
				}
			}
		});

		Button refreshButton = new Button(buttonComposite, SWT.PUSH);
		refreshButton.setText("Refresh");
		image = new org.eclipse.swt.graphics.Image(parent.getDisplay(), getClass().getClassLoader()
			.getResourceAsStream("icons/refresh.png"));
		refreshButton.setImage(image);
		refreshButton.setLayoutData(widgetLayoutData);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				refreshAMI(amazonEC2);
			}
		});
		buttonComposite.setLayoutData(layoutData);

		// Disable texts and buttons for non-admin users
		if (!User.isAdmin) {
			nameText.setEnabled(false);
			instanceText.setEnabled(false);
			runInstanceText.setEnabled(false);
			submitButton.setEnabled(false);
		}
	}

	/**
	 * @param amazonEC2
	 * @param layoutData
	 */
	private void createTable(Composite parent, EC2 amazonEC2) {
		GridData layoutData = new GridData();
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		createColumns(viewer);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		layoutData.horizontalSpan = 2;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = SWT.CENTER;
		table.setLayoutData(layoutData);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(amazonEC2.getAMIImages());

		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (User.isAdmin) {
					runButton.setEnabled(true);
					deleteButton.setEnabled(true);
				}
			}
		});
	}

	public void createColumns(final TableViewer viewer) {
		String[] titles = { "Name", "Owner", "Description", "AMI ID", "Source", "Status", "Platform", "Root Device" };
		int[] bounds = { 100, 100, 200, 100, 150, 75, 100, 120 };

		// Name
		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0], 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getName();
			}
		});

		// Owner
		column = createTableViewerColumn(titles[1], bounds[1], 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				List<Tag> tags = ami.getImage().getTags();
				for (Tag tag : tags) {
					if (tag.getKey().equalsIgnoreCase("owner"))
						return tag.getValue();
				}
				return ami.getImage().getOwnerId();
			}
		});

		// AMI Name
		column = createTableViewerColumn(titles[2], bounds[2], 2);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getDescription();
			}
		});

		// AMI ID
		column = createTableViewerColumn(titles[3], bounds[3], 3);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getImageId();
			}
		});

		// Source
		column = createTableViewerColumn(titles[4], bounds[4], 4);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getImageLocation();
			}
		});

		// Status
		column = createTableViewerColumn(titles[5], bounds[5], 5);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getState();
			}
		});

		// Platform
		column = createTableViewerColumn(titles[6], bounds[6], 6);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getPlatform();
			}
		});

		// Root Device
		column = createTableViewerColumn(titles[7], bounds[7], 7);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CustomAWSImage ami = (CustomAWSImage) element;
				return ami.getImage().getRootDeviceType();
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * @param amazonEC2
	 */
	private void createAMI(EC2 amazonEC2, String name, String instanceId, Regions region) {
		CreateImageRequest createRequest = new CreateImageRequest();
		createRequest.withName(name).withDescription(name);
		createRequest.withInstanceId(instanceId);
		CreateImageResult result = amazonEC2.createImage(createRequest, region);
		String imageId = result.getImageId();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		Tag tag = new Tag("owner", User.username);
		tags.add(tag);
		CreateTagsRequest ctRequest = new CreateTagsRequest();
		ctRequest.withResources(imageId);
		ctRequest.withTags(tags);
		amazonEC2.createTags(ctRequest, region);
		refreshAMI(amazonEC2);
	}

	private void degregisterAMI(EC2 amazonEC2, String imageID, Regions region) {
		DeregisterImageRequest deregisterRequest = new DeregisterImageRequest(imageID);
		amazonEC2.deregisterImage(deregisterRequest, region);
		refreshAMI(amazonEC2);
	}

	private void refreshAMI(EC2 amazonEC2) {
		viewer.setInput(amazonEC2.getAMIImages());
		viewer.refresh();
		runButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}

	private void runInstanceUsingAMI(EC2 amazonEC2, String imageId, String instanceName, Regions region) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest(imageId, 1, 1);
		amazonEC2.runInstances(runInstancesRequest, instanceName, region);
	}
}