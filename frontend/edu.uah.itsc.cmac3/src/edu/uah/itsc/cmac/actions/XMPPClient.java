package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

public class XMPPClient {
	private String							client;
	private String							server	= "@zelda.itsc.uah.edu";
	private IRemoteServiceContainerAdapter	service1;
	private ID								id1;

	private String							uname;
	private String							passwd;

	public XMPPClient(String username, String password) {
		uname = username;
		passwd = password;
		client = uname + server;
	}

	public XMPPClient() {
	}

	public void setUsername(String username) {
		uname = username;
		client = uname + server;
	}

	public void setPassword(String password) {
		passwd = password;
	}

	public void setClient() {

	}

	public void connect1() throws Exception {
		// CLIENT 1
		IContainer container1 = ContainerFactory.getDefault().createContainer("ecf.xmpp.smack");

		id1 = IDFactory.getDefault().createID(container1.getConnectNamespace(), client);

		IConnectContext connectContext1 = ConnectContextFactory.createPasswordConnectContext(passwd);
		// container1.connect(id1, connectContext1);
		//
		// new AsynchContainerConnectAction(container1, client, connectContext1,
		// null, new Runnable() {
		// public void run() {
		// // cachePassword(client, passwd);
		// }
		// }).run();

		//
		// service1 = (IRemoteServiceContainerAdapter) container1
		// .getAdapter(IRemoteServiceContainerAdapter.class);
		//
		// IRosterManager rm1 = ((IPresenceContainerAdapter)
		// container1.getAdapter(IPresenceContainerAdapter.class)).getRosterManager();
		// rm1.addPresenceListener(new IPresenceListener() {
		// @Override
		// public void handlePresence(org.eclipse.ecf.core.identity.ID fromID,
		// IPresence presence) {
		// if (presence.getMode().equals(IPresence.Mode.AVAILABLE)) {
		// System.out.println("handlePresence("+id1+") fromID="+fromID+",presence="+presence);
		// // id2 = fromID;
		// }
		// }});
		//
		System.out.println("Connected to XMPP server");
	}

	public void createUser(String username, String password, String email, String name) {

		String baseurl = "http://zelda.itsc.uah.edu:9090/plugins/userService/userservice?type=add&secret=VMQuf3eA&";

		String posturl = baseurl + "username=" + username + "&password=" + password + "&name=" + name + "&email="
			+ email + "&groups=CMAC";

		URL url = null;
		try {
			url = new URL(posturl);
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseMessage());
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
