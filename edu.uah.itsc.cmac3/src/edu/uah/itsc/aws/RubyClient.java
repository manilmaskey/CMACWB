/*
 * This Class is client to the Amazon EC2.  It submits the active workflow.
 * */
package edu.uah.itsc.aws;

/*
 This document is a part of the source code and related artifacts for CMAC Project funded by NASA 
 Copyright © 2013, University of Alabama in Huntsville
 You may not use this file except in compliance with University of Alabama in Huntsville License.
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 limitations under the license.
 Date: Jul 26, 2013

 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

//Constructor
public class RubyClient {
	private String title;
	private String desc;
	private String bucket;
	private String folder;
	private String filename;
	private String fname;
	private String publicURL;
	public RubyClient(String title, String description, String bucket,
			String folder, String filename, String publicURL) {

		this.title = title;
		this.desc = description;
		this.bucket = bucket;
		this.folder = folder;
		this.filename = filename;
		this.publicURL = publicURL;
		InputStream is = null;
		FileInputStream fileInputStream = null;
		File file = new File(this.filename);
		byte content[] = new byte[(int) file.length()];
		fname = file.getName();
		// try {
		//
		// is = new FileInputStream(this.filename);
		// fname = new File(filename).getName();
		// content = new byte[2*1024];
		// int readCount = 0;
		// while((readCount = is.read(content)) > 0){
		// System.out.println(new String(content, 0, readCount-1));
		// }
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// try{
		// if(is != null) is.close();
		// } catch(Exception ex){
		//
		// }
		// }

		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(content);
			fileInputStream.close();
			postFile(content);

		} catch (Exception e) {
			System.out.println("Exception e " + e.toString());
		}

	}

	public void postFile(byte[] image) throws ClientProtocolException,
			IOException {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		// HttpPost httppost = new
		// HttpPost("http://ec2-107-21-179-173.compute-1.amazonaws.com:3000/posts");
		HttpPost httppost = new HttpPost(publicURL);
		ContentBody cb = new ByteArrayBody(image, "text/plain; charset=utf8",
				fname);
		// ContentBody cb = new InputStreamBody(new ByteArrayInputStream(image),
		// "image/jpg", "icon.jpg");

		MultipartEntity mpentity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		// mpentity.addPart(cb.);
		// mpentity.addPart("utf8", new
		// StringBody((Character.toString('\u2713'))));
		mpentity.addPart("post[photo]", cb);
		mpentity.addPart("post[content]", new StringBody(desc));
		// mpentity.addPart("post[filename]", new StringBody( fname));
		mpentity.addPart("post[title]", new StringBody(title));
		mpentity.addPart("post[bucket]", new StringBody(bucket));
		mpentity.addPart("post[user]", new StringBody(User.username));
		mpentity.addPart("post[folder]", new StringBody(folder));
		mpentity.addPart("commit", new StringBody("Create Post"));
		httppost.setEntity(mpentity);
		String response = EntityUtils.toString(httpclient.execute(httppost)
				.getEntity(), "UTF-8");
		System.out.println(response);
	}

}
