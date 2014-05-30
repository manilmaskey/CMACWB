/**
 * 
 */
package edu.uah.itsc.cmac.model;

import org.eclipse.core.resources.IFolder;

/**
 * @author sshrestha
 * 
 */
public interface VersionViewInterface {
	public void accept(IFolder folder, String repoName, String repoPath);
}
