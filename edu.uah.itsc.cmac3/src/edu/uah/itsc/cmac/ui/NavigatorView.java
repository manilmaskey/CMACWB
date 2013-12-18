package edu.uah.itsc.cmac.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class NavigatorView extends CommonNavigator {

	private CommonViewer viewer;
	private String prefix = "";
	private edu.uah.itsc.aws.S3 s3;
	private IProject p1;
	private IProject p2;
	public static final String ID = "edu.uah.itsc.cmac.NavigatorView";

	public S3 getS3() {
		return s3;
	}

	public CommonViewer getViewer() {
		return viewer;
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		System.out.println("AWS: " + User.awsAccessKey + "  "
				+ User.awsSecretKey);

		if (User.awsAccessKey != null) {

			s3 = new S3(User.awsAccessKey, User.awsSecretKey);
			createHandlers();
			hookDoubleClickCommand();

			viewer = super.getCommonViewer();// new TreeViewer(parent, SWT.MULTI
												// | SWT.H_SCROLL | SWT.V_SCROLL
												// | SWT.BORDER);
			// communityViewer.setContentProvider(new ViewContentProvider());

			// communityViewer.setLabelProvider(new ViewLabelProvider());

			try {
				IProgressMonitor monitor = new NullProgressMonitor();

				System.out.println("Workspace Location: "
						+ ResourcesPlugin.getWorkspace().getRoot()
								.getLocation().toOSString());

				p1 = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(s3.getCommunityBucketName());

				if (!p1.exists()) {

					p1.create(monitor);
					p1.open(monitor);
				}
				System.out.println("Create Folder ------------ >");

				p2 = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(s3.getBucketName());
				if (!p2.exists()) {
					p2.create(monitor);
					p2.open(monitor);
				} else
					System.out.println("Create Folder ------------ > Exists");
				// p1.setDescription(description, monitor);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {

				}

				refreshCommunityResource();
				// buildTree(s3.getRootFolder()+java.io.File.separator,
				// p2,s3.getBucketName());

				IFolder userFolder = p2.getFolder(User.username);
				if (!userFolder.exists())
					userFolder.create(false, true, null);
				buildTree(User.username + "_$folder$", p2, s3.getBucketName());
				// buildTree(User.username+"/", p2,s3.getBucketName());

				// else{
				// buildTree(User.username+"_$folder$/", p2,s3.getBucketName());
				// }

				buildAllBucketsAsProjects(monitor);

				viewer.setInput(p1);
				viewer.setInput(p2);
			} catch (CoreException e) {
				System.out
						.println("Core Exception in NavigatorView.createPartControl: "
								+ e.toString());
			}
		}

		else {

			p1 = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(S3.communityBucketName);

			if (p1.exists()) {
				try {
					p1.setHidden(true);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			p2 = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(S3.bucketName);
			if (p2.exists()) {
				try {
					p2.setHidden(true);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void buildAllBucketsAsProjects(IProgressMonitor monitor) {
		ArrayList<String> buckets = s3.getAllBuckets();
		IProject project;
		for (String bucket : buckets) {
			project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(bucket);
			try {
				if (project.exists()
						&& (project.getName().equals(
								s3.getCommunityBucketName()) || project
								.getName().equals(s3.getBucketName()))) {
					continue;
				} else if (project.exists())
					project.delete(true, monitor);
				project.create(monitor);
				buildTree("", project, bucket);
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
	}

	public void clearCommunityResource() {
		try {
			p1.delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refreshCommunityResource() {
		try {
			clearCommunityResource();
			IProgressMonitor monitor = new NullProgressMonitor();
			p1 = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(s3.getCommunityBucketName());
			if (!p1.exists()) {
				p1.create(monitor);
				p1.open(monitor);
			}
			buildTree("", p1, s3.getCommunityBucketName());
		} catch (Exception e) {
			System.out
					.println("Exception in NavigatorView.refreshCommunityResource: "
							+ e.toString());
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

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	private void createHandlers() {
		IHandlerService handlerService = (IHandlerService) getSite()
				.getService(IHandlerService.class);

		// ///////////////////////////////////////////////////////
		AbstractHandler myHandlerDownload = new AbstractHandler() {

			public Object execute(ExecutionEvent event)
					throws ExecutionException {
				// viewer is an instance variable of
				IWorkbenchWindow window = HandlerUtil
						.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();

				System.out.print("Doubleclick------------>");

				// NavigatorView view = (NavigatorView)
				// page.findView(edu.uah.itsc.cmac.NavigatorView.ID);
				// Get the selection
				ISelection selection = getSite().getSelectionProvider()
						.getSelection();
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection)
							.getFirstElement();
					IResource res = (IResource) obj;
					// If we had a selection lets open the editor

					if (obj != null) {
						if (obj.getClass().getName().toString() == "org.eclipse.core.internal.resources.File") {
							File file = (File) obj;

							// if
							// (file.getProject().getName().equals(S3.communityBucketName))
							// //download first if community resource
							boolean isLinkedFile = false;
							if (file.isLinked()) {
								isLinkedFile = true;
								try {
									file.delete(true, new NullProgressMonitor());
								} catch (CoreException e1) {
									// TODO Auto-generated catch block
									System.err.print("file.delete----->"
											+ e1.toString());
								}
								s3.downloadFile(s3.getBucketName(file
										.getFullPath().toOSString()), s3
										.getS3ResourceName(file.getFullPath()
												.toOSString()), s3
										.getLocalResourceName(file
												.getFullPath().toOSString()));
								try {
									res.getProject().refreshLocal(
											IResource.DEPTH_INFINITE,
											new NullProgressMonitor());
								} catch (CoreException e) {
									// TODO Auto-generated catch block
									System.err.print(e.toString());
								}

							}

							String extension = file.getFileExtension();
							boolean isImageFile = extension.equals("gif")
									|| extension.equals("jpg")
									|| extension.equals("jpeg")
									|| extension.equals("png")
									|| extension.equals("bmp")
									|| extension.equals("ico");
							if (!isImageFile) {
								//
								// GliderText text = new GliderText(file);
								// TextEditorInput input = new
								// TextEditorInput(text);
								// try {
								//
								// IFileStore fileStore = null;
								// fileStore =
								// EFS.getStore(text.getFile().getLocationURI());
								//
								// page.openEditor(
								// new FileStoreEditorInput(fileStore),
								// EditorsUI.DEFAULT_TEXT_EDITOR_ID);
								//
								// // page.openEditor(input, TextEditor.ID);
								// } catch (PartInitException e) {
								// System.out.println("PartinitException "
								// + e.getMessage() + e.getStackTrace());
								// }catch (CoreException e) {
								// // TODO Auto-generated catch block
								// e.printStackTrace();
								// }
							} else {

								if (isLinkedFile) {
									try {
										//
										// ICommandService commandService =
										// (ICommandService)
										// PlatformUI.getWorkbench().getService(ICommandService.class);
										// Command command =
										// commandService.getCommand("org.bonitasoft.studio.application.importBarCommand");
										//
										//
										//
										// //res
										// notifyListeners(SWT.MouseDoubleClick,
										// new Event());
										//
										getSite().getSelectionProvider()
												.setSelection(selection);
										// ((IHandlerService) getSite()
										// .getService(IHandlerService.class)).createExecutionEvent(command,
										// event);
										//
										//
										//
										((IHandlerService) getSite()
												.getService(
														IHandlerService.class))
												.executeCommand(
														"edu.uah.itsc.cmac.downloadFile",
														null);
									} catch (NotDefinedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NotEnabledException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NotHandledException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}
						}
						if (obj.getClass().getName().toString() == "org.eclipse.core.internal.resources.Folder") {
							Folder folder = (Folder) obj;
							s3.downloadFolder(
									s3.getBucketName(folder.getFullPath()
											.toOSString()),
									s3.getS3ResourceName(folder.getFullPath()
											.toOSString())
											+ java.io.File.separator);
						}
						try {
							res.getProject().refreshLocal(
									IResource.DEPTH_INFINITE, null);
						} catch (CoreException ce) {
						}
					} // if obj!=null
				}
				return null;
			}
		};

		handlerService.activateHandler("edu.uah.itsc.cmac.downloadFile",
				myHandlerDownload);
		// ///////////////////////////////////////////////////////
		AbstractHandler myHandler2 = new AbstractHandler() {
			public Object execute(ExecutionEvent event)
					throws ExecutionException {

				// viewer is an instance variable of
				IWorkbenchWindow window = HandlerUtil
						.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();
				// NavigatorView view = (NavigatorView)
				// page.findView(edu.uah.itsc.cmac.NavigatorView.ID);
				// Get the selection
				ISelection selection = getSite().getSelectionProvider()
						.getSelection();
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection)
							.getFirstElement();
					// If we had a selection lets open the editor
					if (obj != null) {
						if (obj.getClass().getName().toString() != "org.eclipse.core.internal.resources.Project") {

							// File file = (File) obj;
							// GliderText text = new GliderText(file);
							// TextEditorInput input = new
							// TextEditorInput(text);
							// try {
							//
							// IFileStore fileStore = null;
							// try {
							//
							// fileStore =
							// EFS.getStore(text.getFile().getLocationURI());
							// } catch (CoreException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
							// page.openEditor(
							// new FileStoreEditorInput(fileStore),
							// EditorsUI.DEFAULT_TEXT_EDITOR_ID);
							//
							// // page.openEditor(input, TextEditor.ID);
							// } catch (PartInitException e) {
							// System.out.println("PartinitException "
							// + e.getMessage() + e.getStackTrace());
							// }
						}
					}
				}
				return null;
			}
		};
		handlerService
				.activateHandler("edu.uah.itsc.cmac.viewText", myHandler2);

		// //////////////////////////////////////////////////////////////////////////////////

		// /
	}

	private void hookDoubleClickCommand() {
		viewer = super.getCommonViewer();

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite()
						.getService(IHandlerService.class);

				boolean isImageFile = false;
				ISelection selection = event.getSelection();

				if (selection != null
						&& selection instanceof IStructuredSelection) {

					Object obj = ((IStructuredSelection) selection)
							.getFirstElement();
					if (obj != null) {
						// if (obj.getClass().getName().toString() !=
						// "org.eclipse.core.internal.resources.Project") {
						// File gliderFile = (File) obj;
						//
						// if ("gld".equalsIgnoreCase(gliderFile
						// .getFileExtension())
						// || "gsf".equalsIgnoreCase(gliderFile
						// .getFileExtension())) {
						// isImageFile = true;
						// }
						// }
					}
				}

				try {
					// handlerService = (IHandlerService)
					// getSite().getService(IHandlerService.class);
					// if (isImageFile) {
					// handlerService.executeCommand("edu.uah.itsc.cmac.viewMetadata",
					// null);
					// } else {
					handlerService.executeCommand(
							"edu.uah.itsc.cmac.downloadFile", null);
					// }
				} catch (ExecutionException ex) {
					// throw new
					// RuntimeException("edu.uah.itsc.cmac.viewMetadata not found execution exception "+ex.getMessage());
				} catch (NotDefinedException ex) {
					// throw new
					// RuntimeException("edu.uah.itsc.cmac.viewMetadata not found NotDefinedException exception "+ex.getMessage());
				} catch (NotEnabledException ex) {
					// throw new
					// RuntimeException("edu.uah.itsc.cmac.viewMetadata not found NotEnabledException exception "+ex.getMessage());
				} catch (NotHandledException ex) {
					// throw new
					// RuntimeException("edu.uah.itsc.cmac.viewMetadata not found NotHandledException exception "+ex.getMessage());
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
