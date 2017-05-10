package com.emc.documentum.dfc.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;

public class DfcTest {

	private static String repo = "rep01";
	private static String user = "dmadmin";
	private static String pass = "";

	IDfSession session = null;

	@Test
	public void testConnection() throws DfException {
		IDfQuery q = new DfQuery("SELECT * FROM dm_server_config");
		IDfCollection c = q.execute(session, IDfQuery.DF_READ_QUERY);
		while (c.next()) {
			System.out.println("dm_server_config: " + c.getString("object_name"));
		}
		c.close();
	}

	@Before
	public void setUp() throws Exception {
		repo = getArg("repo", repo);
		user = getArg("user", user);
		pass = getArg("pass", pass);

		session = new DfClient().newSession(repo, new DfLoginInfo(user, pass));
	}

	@After
	public void tearDown() throws Exception {
		session.getSessionManager().release(session);
	}

	private String getArg(String prop, String value) {
		String arg = System.getProperty(prop);
		return (arg == null || arg.trim().isEmpty()) ? value : arg;
	}
}
