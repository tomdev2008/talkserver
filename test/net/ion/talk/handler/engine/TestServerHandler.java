package net.ion.talk.handler.engine;

import java.net.InetAddress;

import net.ion.framework.util.Debug;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.let.TestBaseLet;

public class TestServerHandler extends TestBaseLet {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.addTalkHander(ServerHandler.test());
		tserver.startRadon();
	}

	public void testIsOnServer() throws Exception {

		ServerHandler serverHandler = tserver.talkEngine().handler(ServerHandler.class);
		assertTrue(serverHandler.registered(InetAddress.getLocalHost().getHostName()));

		// tserver.talkEngine().onStop() ;
		// assertFalse(serverHandler.registered(InetAddress.getLocalHost().getHostName())) ;
	}

	public void xtestIPAddress() throws Exception {
		ServerHandler sh = ServerHandler.test();
		assertEquals("61.250.201.157", sh.serverHost());

		InetAddress address = InetAddress.getLocalHost();
		Debug.line(address.getHostName());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown(); // To change body of overridden methods use File | Settings | File Templates.
	}
}
