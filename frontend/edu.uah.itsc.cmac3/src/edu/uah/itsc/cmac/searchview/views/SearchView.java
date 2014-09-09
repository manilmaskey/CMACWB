package edu.uah.itsc.cmac.searchview.views;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.uah.itsc.cmac.Utilities;
import edu.uah.itsc.cmac.searchview.models.SearchResult;
import edu.uah.itsc.cmac.searchview.models.SearchResultInterface;

/**
 * 
 */

public class SearchView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID	= "edu.uah.itsc.cmac.searchview.views.SearchView";

	/**
	 * The constructor.
	 */
	public SearchView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(final Composite parent) {
		/*
		 * Build the UI. The UI contains a composite. This compsite contains a search textbox and a push button. The
		 * search textbox fills the first column in the grid and is centered. The search textbox contains
		 * "Search Experiments" as help text and the button says "Search"
		 */
		parent.setLayout(new GridLayout(2, false));
		final Text searchTextWidget = new Text(parent, SWT.SEARCH);
		searchTextWidget.setMessage("Search Workflows");
		searchTextWidget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

		Button searchButton = new Button(parent, SWT.PUSH);
		searchButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		searchButton.setText("Search");

		/*
		 * Selection listener for the search button. When the button is pressed it grabs the content in the search
		 * textbox and calls the method createSearchResult.
		 */
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = parent.getShell();
				String searchText = searchTextWidget.getText();
				ArrayList<SearchResult> searchResults = createSearchResult(searchText);
				/*
				 * TODO: Make a generic messagebox to display messages from all places
				 */
				MessageBox message = new MessageBox(shell);
				if (searchResults == null || searchResults.size() == 0) {
					message.setText("Searching for " + searchText);
					message.setMessage("No workflow(s) found");
					message.open();
					return;
				}
				/*
				 * The IPartView returned using showView method is casted into SearchResultInterface. This is done so as
				 * to pass objects from this view to the SearchResultView. Once you get the IPartView as
				 * SearchResultInterface, you can call the accept method defined in the SearchResultInterface.
				 */
				try {
					SearchResultInterface searchResultView = (SearchResultInterface) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.showView("edu.uah.itsc.cmac.searchview.views.SearchResultView");
					searchResultView.accept(searchResults);
				}
				catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	/**
	 * This method fetches and parses data from the URL passed as parameter The parsing of json data is entirely
	 * dependent on the format of JSON provided by Drupal Search Services. If the data format changes, the parsing of
	 * JSON should be adjusted in this method as per required.
	 */

	public ArrayList<SearchResult> createSearchResult(String searchText) {
		ArrayList<SearchResult> SearchResults = new ArrayList<SearchResult>();

		String jsonText;

		try {
			String url = buildURL(searchText);
			// System.out.println("The URL we are going to fetch is: " + url);
			jsonText = getJSONStringFromURL(url);
			if (jsonText.length() <= 0) {
				System.out.println("No data from server");
				return null;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(jsonText);
			JSONArray jsonObjects = (JSONArray) obj;
			for (int i = 0; i < jsonObjects.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonObjects.get(i);
				if (isShared(jsonObject) == false)
					continue;
				SearchResult searchResult = new SearchResult();
				searchResult.setTitle(jsonObject.get("title").toString());
				searchResult.setSnippet(jsonObject.get("snippet").toString());
				searchResult.setType(jsonObject.get("type").toString());
				searchResult.setUser(jsonObject.get("user").toString());
				searchResult.setCreated(new Date(Long.parseLong(jsonObject.get("created").toString())));
				searchResult.setLanguage(jsonObject.get("language").toString());
				searchResult.setLink(new URL(jsonObject.get("link").toString()));
				jsonObject = (JSONObject) jsonObject.get("node");
				searchResult.setNode(Integer.parseInt(jsonObject.get("nid").toString()));

				Object creatorObject = jsonObject.get("field_creator");
				String creator = "";
				if (creatorObject instanceof JSONObject) {
					JSONObject jsonCreatorObject = (JSONObject) creatorObject;
					JSONArray creatorObjectArray = (JSONArray) jsonCreatorObject.get("und");
					if (creatorObjectArray.size() > 0) {
						creator = ((JSONObject) ((JSONObject) creatorObjectArray.get(0)).get("user")).get("name")
							.toString();
					}
				}
				searchResult.setCreator(creator);

				Object submittorObject = jsonObject.get("field_submittor");
				String submittor = "";
				if (submittorObject instanceof JSONObject) {
					JSONObject jsonSubmittorObject = (JSONObject) submittorObject;
					JSONArray submittorObjectArray = (JSONArray) jsonSubmittorObject.get("und");
					if (submittorObjectArray.size() > 0) {
						submittor = ((JSONObject) ((JSONObject) submittorObjectArray.get(0)).get("user")).get("name")
							.toString();
					}
				}
				searchResult.setSubmittor(submittor);

				Object object = jsonObject.get("body");
				String description = "";
				if (object instanceof JSONObject) {
					jsonObject = (JSONObject) object;
					JSONArray jsonUND = (JSONArray) jsonObject.get("und");
					jsonObject = (JSONObject) jsonUND.get(0);
					description = jsonObject.get("value").toString();
				}
				searchResult.setDescription(description);

				jsonObject = (JSONObject) jsonObjects.get(i);
				jsonObject = (JSONObject) jsonObject.get("node");
				object = jsonObject.get("field_could_path");
				String folderPath = "";
				if (object instanceof JSONObject) {
					jsonObject = (JSONObject) object;
					JSONArray jsonUND = (JSONArray) jsonObject.get("und");
					jsonObject = (JSONObject) jsonUND.get(0);
					folderPath = jsonObject.get("value").toString();
				}
				searchResult.setFolderPath(folderPath);
				SearchResults.add(searchResult);
			}
		}
		catch (IOException e) {
			e.printStackTrace();

		}
		catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

		return SearchResults;
	}

	private boolean isShared(JSONObject jsonObject) {
		JSONObject jsonTempObject = (JSONObject) jsonObject.get("node");
		Object objFieldIsShared = jsonTempObject.get("field_is_shared");
		if (objFieldIsShared instanceof JSONObject) {
			jsonTempObject = (JSONObject) jsonTempObject.get("field_is_shared");
			JSONArray jsonTempArray = (JSONArray) jsonTempObject.get("und");
			jsonTempObject = (JSONObject) jsonTempArray.get(0);
			System.out.println(jsonTempObject.get("value"));
			if (((String) jsonTempObject.get("value")).equalsIgnoreCase("0"))
				return false;
			else
				return true;
		}
		else if (objFieldIsShared instanceof JSONArray) {
		}
		return false;
	}

	protected String getJSONStringFromURL(String url) {

		StringBuilder sb = new StringBuilder();
		String jsonText;
		try {
			InputStream is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}

		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		jsonText = sb.toString();

		return jsonText;
	}

	/*
	 * This method builds the REST URL to fetch data from server. The URL is fetched from preferences. Also,
	 * the extraquery is appended to the URL, so that the content type to search on can be changed dynamically
	 */
	protected String buildURL(String searchText) {
		String url = null;
		try {
			url = Utilities.getKeyValueFromPreferences("portal", "search_url");
			url = url + "?keys=" + URLEncoder.encode(searchText, "UTF-8") + "%20" + "type:workflow";
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;

	}

}
