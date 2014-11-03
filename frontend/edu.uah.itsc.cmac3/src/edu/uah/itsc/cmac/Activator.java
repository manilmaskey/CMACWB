package edu.uah.itsc.cmac;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.LoginDialog;
import edu.uah.itsc.cmac.util.GITUtility;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String	PLUGIN_ID	= "edu.uah.itsc.cmac3";

	// The shared instance
	private static Activator	plugin;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		if (User.sessionID == null || User.sessionID.equals("")) {
			LoginDialog loginDialog = new LoginDialog(PlatformUI.createDisplay());
			loginDialog.createContents();
		}
		setResourceChangeListener();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 * the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private void setResourceChangeListener() {
		IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				IResource resource = event.getResource();
				if (resource != null) {
					System.out.println("Resource is" + resource);
				}
				IResourceDelta delta = event.getDelta();
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) {
						// only interested in removed resources
						if (delta.getKind() != IResourceDelta.REMOVED)
							return true;
						IResource resource = delta.getResource();
						System.out.println(resource.getFullPath());

						String repoName = null;
						String repoLocalPath = null;
						String fileName = null;

						if (resource instanceof IFile) {
							IFile file = resource.getProject().getFile(resource.getFullPath().removeFirstSegments(1));
							IResource folder = file.getParent();
							while (folder.getParent() != resource.getProject())
								folder = folder.getParent();
							repoName = folder.getName();
							repoLocalPath = folder.getParent().getLocation().toString();
							fileName = file.getName();

						}
						if (resource instanceof IFolder) {
							IResource folder = (IFolder) resource;
							fileName = folder.getName();
							while (folder.getParent() != folder.getProject())
								folder = folder.getParent();
							repoName = folder.getName();
							repoLocalPath = folder.getParent().getLocation().toString();
						}

						try {
							GITUtility.delete(repoName, repoLocalPath, fileName);
							GITUtility.commitLocalChanges(repoName, repoLocalPath, "Commit after deletion",
								User.username, User.userEmail);
						}
						catch (Exception e) {
							e.printStackTrace();
						}

						return true;
					}
				};

				try {
					delta.accept(visitor);
				}
				catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

}
