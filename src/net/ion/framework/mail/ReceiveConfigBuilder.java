package net.ion.framework.mail;

import net.ion.framework.mail.ReceiveConfigBuilder.Protocol;
import net.ion.framework.util.StringUtil;


public class ReceiveConfigBuilder {

	private MailConfigBuilder parent ;
	private String server = "smtp.i-on.net";
	private int portNum = 110;
	private String mailId;
	private String password;
	private Protocol protocol = Protocol.POP3 ;
	
	public enum Protocol {
		POP3 {
			public String stringValue() {
				return "pop3";
			}
		};

		public abstract String stringValue() ;
	}

	ReceiveConfigBuilder(MailConfigBuilder parent) {
		this.parent = parent ;
	}


	
	public MailConfig buildConfig() {
		return parent.buildConfig() ;
	}

	
	public String serverHost(){
		return server;
	}
	
	public String mailUserId(){
		return mailId ;
	}
	
	public String mailUserPwd(){
		return password ;
	}
	
	public int serverPort(){
		return portNum ;
	}

	public Protocol protocol(){
		return protocol ;
	}
	
	
	public ReceiveConfigBuilder server(String hostServer) {
		this.server = hostServer ;
		return this;
	}


	public ReceiveConfigBuilder port(int portNum) {
		this.portNum = portNum ;
		return this;
	}


	public ReceiveConfigBuilder mailUserId(String mailUserId) {
		this.mailId = mailUserId ;
		return this;
	}


	public ReceiveConfigBuilder mailUserPwd(String mailUserPwd) {
		this.password = mailUserPwd ;
		return this;
	}

	public ReceiveConfigBuilder protocol(Protocol protocol) {
		this.protocol = protocol ;
		return this;
	}
	
	
	
	
}
