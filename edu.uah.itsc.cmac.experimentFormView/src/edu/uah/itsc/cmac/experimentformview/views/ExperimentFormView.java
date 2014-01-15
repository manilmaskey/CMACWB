package edu.uah.itsc.cmac.experimentformview.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Experiment;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;

public class ExperimentFormView extends ViewPart {
	private FormToolkit toolkit;
	private Form form;
	private S3 s3;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText("Experiments");
		toolkit.decorateFormHeading(form);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		Dialog.applyDialogFont(form.getBody());
		Section s2 = createTableSection(form, toolkit, "Maintain Experiments");
		// This call is needed for all the children
		Dialog.applyDialogFont(form.getBody());
	}

	private Section createTableSection(final Form form2, FormToolkit toolkit,
			String title) {
		GridData gd;
		Section section = toolkit.createSection(form2.getBody(),
				Section.TWISTIE | Section.TITLE_BAR);
		section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(
				IFormColors.SEPARATOR));
		FormText description = toolkit.createFormText(section, false);
		description.setText(
				"<form><p>Create a new <b>experiment</b></p></form>", true,
				false);
		section.setDescriptionControl(description);

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);

		Label titleLabel = toolkit.createLabel(client, "Title");
		final Text titleText = toolkit.createText(client, "");
		titleText.setLayoutData(new GridData(200, 15));

		Label descriptionLabel = toolkit.createLabel(client, "Description");
		final Text descriptionText = toolkit.createText(client, "", SWT.MULTI);
		descriptionText.setLayoutData(new GridData(200, 150));

		// Label creatorLabel = toolkit.createLabel(client, "Creator");
		// final Text creatorText = toolkit.createText(client, "");
		// creatorText.setLayoutData(new GridData(200, 15));

		// Label workflowsLabel = toolkit.createLabel(client, "Workflows");
		// final Text workflowsText = toolkit.createText(client, "");
		// workflowsText.setLayoutData(new GridData(200, 15));

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		Button submitButton = toolkit.createButton(client, "Submit Experiment",
				SWT.PUSH);
		submitButton.setLayoutData(gd);
		submitButton.addSelectionListener(new SelectionListener() {
			HttpResponse response;
			PortalPost portalPost = new PortalPost();
			Experiment experiment = new Experiment();

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					
				final MessageBox message = new MessageBox(form2.getShell());
				try {
					experiment.setTitle(titleText.getText());
					experiment.setDescription(descriptionText.getText());
					experiment.setCreator(User.portalUserID);
					// experiment.setWorkflows(workflowsText.getText());
					Job job = new Job("Creating experiment..") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								final JSONObject jsonExperiment = experiment
										.getJSON();
								s3 = new S3();
								if (s3.doesBucketExist(experiment.getTitle())) {
									message.setMessage("This bucket is already created under your AWS ID");
									message.setText("Error");
									return Status.CANCEL_STATUS;
								}
								AmazonS3 amazonS3Service = s3
										.getAmazonS3Service();
								String bucketName = jsonExperiment.get("title")
										.toString();
								Bucket newBucket = amazonS3Service
										.createBucket(bucketName);
								s3.addGroupPolicy("cmac_collaborators",
										"policy_cmac_collaborators",
										getPolicyToAdd(bucketName));
								response = portalPost.post(
										PortalUtilities.getNodeRestPoint(),
										jsonExperiment);
								if (response == null
										|| response.getStatusLine()
												.getStatusCode() != 200) {
									message.setMessage("Invalid Status Code");
									return Status.CANCEL_STATUS;
								}
								monitor.worked(50);
								S3 adminS3 = new S3();
								Thread.sleep(5000);
								if (!adminS3.userFolderExists(User.username,
										experiment.getTitle())) {
									adminS3.uploadUserFolder(User.username,
											experiment.getTitle());
								}
								buildBucketAsProject(experiment.getTitle(),
										new NullProgressMonitor());
								monitor.done();
								message.setMessage("Added Experiment Successfully");
								Display.getDefault().asyncExec(new Runnable() {
									  public void run() {
									  message.open();
									  }
								});
								return Status.OK_STATUS;
							} catch (JSONException e) {
								e.printStackTrace();
								message.setMessage("Could not add the experiment in JSONException.\n"
										+ e.getMessage());
								message.setText("Error");
								Display.getDefault().syncExec(new Runnable() {
									  public void run() {
									  message.open();
									  }
								});
								return Status.CANCEL_STATUS;
							} catch (InterruptedException e) {
								e.printStackTrace();
								message.setMessage("Could not add the experiment in InterruptedException\n"
										+ e.getMessage());
								message.setText("Error");
								Display.getDefault().syncExec(new Runnable() {
									  public void run() {
									  message.open();
									  }
								});
								return Status.CANCEL_STATUS;
							}
						}
					};
					job.setUser(true);
					job.schedule();
					job.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					message.setMessage("Could not add the experiment in InterruptedException during join\n"
							+ e1.getMessage());
					message.setText("Error");
					message.open();
				} catch (Exception e2){
					message.setText("Error, the last resort");
					message.open();
				}

				descriptionText.setText("");
				titleText.setText("");
				if (message.getText().length() > 0) {
					message.open();

				}
				} catch (Exception e3){
					MessageBox message = new MessageBox(Display.getDefault().getActiveShell().getShell());
					message.setMessage("THE ULTIMATE ERROR");
					message.open();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		// gd = new GridData(GridData.FILL_BOTH);
		// Table t = toolkit.createTable(client, SWT.NULL);
		// gd.heightHint = 200;
		// gd.widthHint = 100;
		// gd.horizontalSpan = 2;
		// t.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		section.setText(title);
		section.setClient(client);
		section.setExpanded(true);
		// section.addExpansionListener(new ExpansionAdapter() {
		// public void expansionStateChanged(ExpansionEvent e) {
		// // ((IManagedForm) form2).reflow(false);
		// }
		// });
		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		return section;
	}

	@Override
	public void setFocus() {

	}

	private String getPolicyToAdd(String bucketName) {
		BufferedReader bufferedReader = null;
		String fileContent = "";
		String line = "";
		try {
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream("policy.template");
			bufferedReader = new BufferedReader(new InputStreamReader(stream));
			while ((line = bufferedReader.readLine()) != null) {
				fileContent = fileContent + "\n" + line;
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileContent = fileContent.replace("%cmac-bucketName-cmac%", bucketName);
		return fileContent;
	}

	public void buildBucketAsProject(String bucket, IProgressMonitor monitor) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(bucket);
		try {
			if (!project.exists())
				project.create(monitor);
			buildTree(User.username + "_$folder$", project, bucket);
			project.refreshLocal(0, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildTree(String prefix, IResource tp, String bucket) {
		ListObjectsRequest lor = new ListObjectsRequest();
		lor.setBucketName(bucket);
		lor.setDelimiter(s3.getDelimiter());
		lor.setPrefix(prefix);

		System.out.println("Building tree.............." + bucket
				+ " delimiter=" + s3.getDelimiter() + " prefix=" + prefix);

		// Just listing the buckets here
		// List<Bucket> bu = s3.getService().listBuckets();
		//
		// for (int i=0;i<bu.size();i++){
		// Bucket b = bu.get(i);
		// System.out.println("---------------"+ b.getName()+"  " +
		// b.getOwner().getDisplayName());
		// }
		ObjectListing filteredObjects = null;
		try {
			filteredObjects = s3.getService().listObjects(lor);
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Cannot build tree for " + bucket + "\n"
					+ e.getMessage());
			return;
		}
		// if (filteredObjects.getObjectSummaries().isEmpty()){
		// IFolder tp1 = ((IProject)tp).getFolder(prefix);
		// if (!tp1.exists())
		// tp1.create(false, true, null);
		// }
		//
		for (S3ObjectSummary objectSummary : filteredObjects
				.getObjectSummaries()) {
			String currentResource = objectSummary.getKey();
			System.out.println("Prefix=" + prefix);
			System.out.println("buildTree currentResource=" + currentResource);

			// check if the resource is a folder
			if (currentResource.indexOf("_$folder$") > 0) {
				IFolder tp1;

				System.out.println("Folder="
						+ currentResource.substring(0,
								currentResource.indexOf("_$folder$")));

				if (tp instanceof IFolder) {
					System.out.println("IFolder="
							+ currentResource.substring(0,
									currentResource.indexOf("_$folder$")));
					System.out
							.println("Current Foldername tp= " + tp.getName());
					tp1 = ((IFolder) tp).getFolder(currentResource.substring(0,
							currentResource.indexOf("_$folder$")).replaceAll(
							prefix, ""));
					System.out.println("Current Foldername tp1= "
							+ tp1.getName());
					if (!tp1.exists())
						try {
							tp1.create(false, true, null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							System.err
									.println("buildTree method tp1.create for IFolder ->"
											+ e.toString());
						}
				} else {
					tp1 = ((IProject) tp).getFolder(currentResource.substring(
							0, currentResource.indexOf("_$folder$"))
							.replaceAll(prefix, ""));
					if (!tp1.exists()) {
						try {
							if (!((IProject) tp).isOpen())
								((IProject) tp).open(null);
							tp1.create(false, true, null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							System.err
									.println("buildTree method tp1.create for IProject ->"
											+ e.toString());
						}
					}
				}
				buildTree(
						currentResource.substring(0,
								currentResource.indexOf("_$folder$"))
								+ "/", tp1, bucket);
			} else { // not a folder, must be a file
				System.out.println("Not a folder prefix: " + prefix);
				IFile f;
				String fullFilePath = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toOSString()
						+ java.io.File.separator
						+ bucket
						+ java.io.File.separator + currentResource;
				System.out.println("full path: " + fullFilePath);
				IPath location = new Path(fullFilePath);

				java.io.File file = new java.io.File(fullFilePath);
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err
							.println("buildTree method file.createNewFile() ->"
									+ e.toString());
				}
				if (tp instanceof IFolder)
					f = ((IFolder) tp).getFile(currentResource.replaceAll(
							prefix, ""));
				else
					f = ((IProject) tp).getFile(currentResource.replaceAll(
							prefix, ""));
				if (!f.exists())
					try {
						f.createLink(location, IResource.NONE, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						System.err
								.println("buildTree method f.createLink(location, IResource.NONE, null) ->"
										+ e.toString());
					}
				// tp.addChild(new TreeObject(currentResource.replaceAll(prefix,
				// ""),currentResource));
				System.out.println("File=" + currentResource);
			}
		}
	}
}