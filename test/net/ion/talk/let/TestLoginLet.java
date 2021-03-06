package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.radon.client.IAradonRequest;
import net.ion.talk.util.NetworkUtil;

import org.restlet.Response;
import org.restlet.data.Method;

import java.net.InetAddress;

public class TestLoginLet extends TestBaseLet {

	private ReadSession rsession;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.startAradon();
		rsession = tserver.readSession();

		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/" + wsession.workspace().repository()).property("host", NetworkUtil.hostAddress()).property("port", 9000);
				wsession.pathBy("/users/ryun").property("pushId", "C6833");
				return null;
			}
		});
	}

	public void testBasicAuth() throws Exception {

		IAradonRequest invalidRequest = tserver.mockClient().fake().createRequest("/auth/login", "ryun", "invalid push Id");
		assertEquals(401, invalidRequest.handle(Method.GET).getStatus().getCode());
		IAradonRequest validRequest = tserver.mockClient().fake().createRequest("/auth/login", "ryun", "C6833");
		assertEquals(200, validRequest.handle(Method.GET).getStatus().getCode());

		Debug.line(validRequest.handle(Method.GET).getEntityAsText());
		assertEquals(true, validRequest.handle(Method.GET).getEntityAsText().startsWith(NetworkUtil.wsAddress(9000, "/websocket/ryun/")));

	}

}
