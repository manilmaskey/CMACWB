/*
 * This is a model class for SearchResult. The data received from portal (Drupal) is converted to a list of objects of
 * SearchResult.
 */
package edu.uah.itsc.cmac.searchview.models;

import java.net.URL;
import java.util.Date;

public class SearchResult {

	private Date	created;
	private String	language;
	private URL		link;
	private int		node;
	private String	snippet;
	private String	title;
	private String	type;
	private String	user;
	private String	description;
	private String	folderPath;

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the link
	 */
	public URL getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(URL link) {
		this.link = link;
	}

	/**
	 * @return the node
	 */
	public int getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(int node) {
		this.node = node;
	}

	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}

	/**
	 * @param snippet
	 *            the snippet to set
	 */
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user.replaceAll("\\<.*?\\>", "");
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}

	/**
	 * @param folderPath
	 *            the folderPath to set
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public SearchResult() {
	}

	public SearchResult(int node, Date created, String title, String type, String user, String snippet,
		String description, URL link) {
	}

	@Override
	public String toString() {
		return title;
	}

}
