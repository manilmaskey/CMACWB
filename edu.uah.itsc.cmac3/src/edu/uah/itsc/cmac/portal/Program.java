/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sshrestha
 * 
 */
public class Program {
	private String title;
	private String description;
	private String keywords;
	private String path;
	private Date creationDate;
	private Date modificationDate;
	private String creator;
	private String submittor;
	private String docURL;
	private String contactInfo;
	private String version;
	private String uri;

	public Program(String title, String description, String path,
			String keywords, boolean isShared) {
		super();
		this.title = title;
		this.description = description;
		this.path = path;
		this.keywords = keywords;
	}

	public Program(String title, String uri) {
		super();
		this.title = title;
		this.uri = uri;
	}

	public Program() {
	}

	public JSONObject getJSON() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("title", title);
		jsonData.put("type", "program");
		if (description != null && !description.isEmpty())
			jsonData.put("body", getComplexObject(description));
		if (creator != null && !creator.isEmpty())
			jsonData.put("field_creator", getComplexObject(creator));
		if (submittor != null && !submittor.isEmpty())
			jsonData.put("field_submittor", getComplexObject(submittor));
		if (path != null && !path.isEmpty())
			jsonData.put("field_could_path", getComplexObject(path));
		if (keywords != null && !keywords.isEmpty())
			jsonData.put("field_keywords", new JSONObject("{'und':'" + keywords + "'}"));
		if (docURL != null && !docURL.isEmpty())
			jsonData.put("field_doc_url", getComplexObject(docURL));
		if (contactInfo != null && !contactInfo.isEmpty())
			jsonData.put("field_contact_info", getComplexObject(contactInfo));
		if (version != null && !version.isEmpty())
			jsonData.put("field_version", getComplexObject(version));
		// jsonData.put("field_start_time",
		// getComplexObject(startTime.toString()));
		// jsonData.put("field_end_time", getComplexObject(description));
		return jsonData;
	}

	private JSONObject getComplexObject(String value) throws JSONException {

		JSONObject undObject = new JSONObject();
		JSONArray undArray = new JSONArray();
		JSONObject undArrayObject = new JSONObject();

		/*
		 * This method will return a JSONObject similar to "field_is_shared": {
		 * "und": [ { "value": "1" } ] }
		 */

		undArrayObject.put("value", value);
		undArray.put(undArrayObject);
		undObject.put("und", undArray);
		return undObject;

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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 *            the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the modificationDate
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * @param modificationDate
	 *            the modificationDate to set
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the submittor
	 */
	public String getSubmittor() {
		return submittor;
	}

	/**
	 * @param submittor
	 *            the submittor to set
	 */
	public void setSubmittor(String submittor) {
		this.submittor = submittor;
	}

	/**
	 * @return the docURL
	 */
	public String getDocURL() {
		return docURL;
	}

	/**
	 * @param docURL
	 *            the docURL to set
	 */
	public void setDocURL(String docURL) {
		this.docURL = docURL;
	}

	/**
	 * @return the contactInfo
	 */
	public String getContactInfo() {
		return contactInfo;
	}

	/**
	 * @param contactInfo
	 *            the contactInfo to set
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
