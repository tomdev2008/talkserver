package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 5. Time: 오전 10:36 To change this template use File | Settings | File Templates.
 */
public class TestWebSocketTalkMessageHandler extends TestCase {

	private TalkEngine tengine;
	private ReadSession rsession;
	private FakeWebSocketConnection ryun;

	public void setUp() throws Exception {

		tengine = TalkEngine.test().registerHandler(new WebSocketMessageHandler()).startForTest();
		rsession = tengine.readSession();
		ryun = FakeWebSocketConnection.create("ryun");

		rsession.tranSync(new TransactionJob<Object>() {

			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/ryun").property("accessToken", "testToken");

				wsession.pathBy("/script/users/register").property(
						"script",
						"session.tranSync(function(wsession){\n" + "  wsession.pathBy(\"/users/\" + params.asString(\"userId\"))\n" + "    .property(\"phone\",params.asString(\"phone\"))\n" + "    .property(\"nickname\",params.asString(\"nickname\"))\n"
								+ "    .property(\"pushId\",params.asString(\"pushId\"))\n" + "    .property(\"deviceOS\",params.asString(\"deviceOS\"))\n" + "    .property(\"friends\", \"\");\n" + "});");

				wsession.pathBy("/script/users/info").property("script", "var user=session.pathBy(\"/users/\"+params.asString(\"userId\")); rb.create().newInner().property(user,\"nickname, phone\").build().toJsonObject();");
				return null;
			}
		});

		ryun.data("accessToken", "testToken");
	}

	public void testSuceessSendMessage() throws Exception {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{\"script\":\"/users/register\", \"id\":\"userRegister\",\"params\":{\"userId\":\"ryun\", \"phone\":\"0101234568\",\"nickname\":\"ryuneeee\",\"pushId\":\"lolem ipsum pushId\",\"deviceOS\":\"android\",\"friends\":[\"alex\",\"lucy\"]}}");
		try {
			Debug.line(ryun.recentMsg());
			fail();
		} catch (Exception e) {

		}

	}

	public void testInvalidParams() throws Exception {
		tengine.onOpen(ryun);

		tengine.onMessage(ryun, "{\"script\":\"/users/info\"}");
		Debug.line(ryun.recentMsg());
		assertEquals("success", JsonObject.fromString(ryun.recentMsg()).asString("status"));

	}

	public void testEmptyMessage() {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
	}

	public void testEmptyJson() {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{}");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
	}

	public void testInvalidJson() {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{a}");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
		tengine.onMessage(ryun, "{안녕}");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
		tengine.onMessage(ryun, "{\"cript\":\"hell\"}");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
	}

	@Override
	public void tearDown() throws Exception {
		tengine.stopForTest();
		super.tearDown();
	}
}
