/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.aws.User;

/**
 * @author sshrestha
 * 
 */
public class PortalPost {
	public PortalPost() {
	}

	private PortalConnector	portalConnector	= new PortalConnector();

	public HttpResponse put(String url, JSONObject putData) {
		return executeRequest(url, putData, "PUT");
	}

	public HttpResponse post(String url, JSONObject postData) {
		return executeRequest(url, postData, "POST");
	}

	public HttpResponse delete(String url) {
		return executeRequest(url, "", "DELETE");
	}

	public HttpResponse put(String url, String putData) {
		return executeRequest(url, putData, "PUT");
	}

	public HttpResponse post(String url, String postData) {
		return executeRequest(url, postData, "POST");
	}

	public HttpResponse get(String url) {
		String data = null;
		return executeRequest(url, data, "GET");
	}

	public void runCron() {
		String url = PortalUtilities.getCronURL();
		HttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BasicHttpContext getHttpContext() {
		JSONObject jsonObject = portalConnector.connect(User.username, User.password);
		String session_id = "";
		String session_name = "";
		try {
			session_name = jsonObject.getString("session_name");
			session_id = jsonObject.getString("sessid");
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BasicHttpContext mHttpContext = new BasicHttpContext();
		CookieStore mCookieStore = new BasicCookieStore();

		BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
		cookie.setVersion(0);
		cookie.setDomain("54.208.76.40");
		cookie.setPath("/");
		mCookieStore.addCookie(cookie);

		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
		return mHttpContext;

	}

	private HttpResponse executeRequest(String url, JSONObject postData, String action) {

		HttpResponse response = null;

		StringEntity se = null;
		if (postData != null)
			try {
				se = new StringEntity(postData.toString());
				// set request content type
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			}
			catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		response = execute(url, action, se);
		return response;
	}

	private HttpResponse executeRequest(String url, String postData, String action) {

		HttpResponse response = null;

		StringEntity se = null;
		if (postData != null)
			try {
				se = new StringEntity(postData);
				// set request content type
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			}
			catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		response = execute(url, action, se);

		return response;
	}

	/**
	 * @param url
	 * @param action
	 * @param se
	 * @return
	 */
	private HttpResponse execute(String url, String action, StringEntity se) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		BasicHttpContext mHttpContext = getHttpContext();
		try {
			String csrf_token = getCSRF(PortalUtilities.getTokenURL(), mHttpContext);
			if (action.equalsIgnoreCase("get")) {
				HttpGet httpGet = new HttpGet(url);
				httpGet.addHeader("X-CSRF-TOKEN", csrf_token);
				response = httpClient.execute(httpGet, mHttpContext);
			}
			if (action.equalsIgnoreCase("put")) {
				HttpPut httpPut = new HttpPut(url);
				httpPut.setEntity(se);
				httpPut.addHeader("X-CSRF-TOKEN", csrf_token);
				response = httpClient.execute(httpPut, mHttpContext);

			}
			else if (action.equalsIgnoreCase("post")) {
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(se);
				httpPost.addHeader("X-CSRF-TOKEN", csrf_token);
				response = httpClient.execute(httpPost, mHttpContext);
				System.out.println(response.getEntity().toString());
				System.out.println("");
			}
			else if (action.equalsIgnoreCase("delete")) {
				HttpDelete httpDelete = new HttpDelete(url);
				httpDelete.addHeader("X-CSRF-TOKEN", csrf_token);
				response = httpClient.execute(httpDelete, mHttpContext);
			}
		}
		catch (Exception e) {
			System.out.println("Error executing");
			e.printStackTrace();
		}
		return response;
	}

	public String getCSRF(String tokenURL, BasicHttpContext mHttpContext) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(tokenURL);
		HttpResponse response = httpClient.execute(httpGet, mHttpContext);
		String csrf_token = EntityUtils.toString(response.getEntity());
		return csrf_token;
	}
}
