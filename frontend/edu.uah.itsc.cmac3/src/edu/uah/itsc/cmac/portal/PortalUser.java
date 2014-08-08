/**
 * 
 */
package edu.uah.itsc.cmac.portal;

/**
 * @author sshrestha
 * 
 */
public class PortalUser {
	private String	username;
	private String	portalUserID;
	private String	email;

	public PortalUser(String username, String portalUserID, String email) {
		super();
		this.username = username;
		this.portalUserID = portalUserID;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPortalUserID() {
		return portalUserID;
	}

	public void setPortalUserID(String portalUserID) {
		this.portalUserID = portalUserID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
