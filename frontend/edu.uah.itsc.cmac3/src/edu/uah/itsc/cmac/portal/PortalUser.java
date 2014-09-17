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
	private String	firstName;
	private String	lastName;

	public PortalUser(String username, String portalUserID, String email, String firstName, String lastName) {
		super();
		this.username = username;
		this.portalUserID = portalUserID;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFullName() {
		if (!firstName.isEmpty())
			return firstName + " " + lastName;
		else
			return username;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
