/*
 * This Class holds current user information
 */
package edu.uah.itsc.aws;

import java.util.HashMap;

/*
 * This document is a part of the source code and related artifacts for CMAC Project funded by NASA Copyright © 2013,
 * University of Alabama in Huntsville You may not use this file except in compliance with University of Alabama in
 * Huntsville License. Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the license. Date: Jul 26, 2013
 * Filename: User.java Author: mmaskey
 */
public class User {

	public static String					username;
	public static String					password;
	public static String					awsAccessKey;
	public static String					awsSecretKey;
	public static String					rootFolder;
	public static String					sessionID;
	public static String					sessionName;
	public static String					portalUserID;
	public static String					userEmail;
	public static boolean					isActive;
	public static boolean					isAdmin	= false;
	public static HashMap<String, String>	userRoles;

}
