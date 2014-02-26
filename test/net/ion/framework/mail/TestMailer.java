package net.ion.framework.mail;

import java.util.concurrent.Executors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.SystemUtils;
import org.apache.lucene.analysis.kr.utils.StringUtil;

import junit.framework.TestCase;
import net.ion.framework.mail.MailConfigBuilder;
import net.ion.framework.mail.Mailer;
import net.ion.framework.mail.MessageCreater;
import net.ion.framework.mail.MessageHandler;
import net.ion.framework.mail.ReceiveConfigBuilder.Protocol;
import net.ion.framework.util.Debug;

public class TestMailer extends TestCase {

	private String userPwd = StringUtil.defaultIfEmpty(System.getProperty("mail.password"), "notdefine") ;
	
	public void testSendMail() throws Exception {
		Mailer mailer = MailConfigBuilder.create().sendConfig().server("smtp.i-on.net").mailUserId("bleujin@i-on.net").mailUserPwd(userPwd).buildConfig().confirmValidOfSendMailConfig().createMailer();

		mailer.sendMail(new MessageCreater() {
			@Override
			public MimeMessage makeMessage(MimeMessage msg) throws MessagingException {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress("bleujin@i-on.net"));
				msg.setSubject("This is the Subject Line!");
				msg.setContent("<h1>This is actual message</h1>", "text/html");
				return msg;
			}
		});
	}

	public void testReceiveMail() throws Exception {
		Mailer mailer = MailConfigBuilder.create().receiveConfig().server("smtp.i-on.net").mailUserId("bleujin@i-on.net").mailUserPwd(userPwd).protocol(Protocol.POP3).buildConfig().confirmValidOfReceiveMailConfig().createMailer();

		mailer.unreadMessage(MessageHandler.PRINTER);
	}

	public void testAttachFile() throws Exception {
		Mailer mailer = MailConfigBuilder.create().sendConfig().server("smtp.i-on.net").mailUserId("bleujin@i-on.net").mailUserPwd(userPwd).buildConfig().confirmValidOfSendMailConfig().createMailer();

		mailer.sendMail(new MessageCreater() {
			@Override
			public MimeMessage makeMessage(MimeMessage initMsg) throws MessagingException {
				initMsg.addRecipient(Message.RecipientType.TO, new InternetAddress("bleujin@i-on.net"));
				initMsg.setSubject("This is the Subject Line!");

				Multipart multipart = new MimeMultipart(); // Create a multipart message
				
				BodyPart textPart = new MimeBodyPart();
				textPart.setContent("<h1>This is attached message</h1>", "text/html"); // Fill the message
				multipart.addBodyPart(textPart); // Set text message part

				// Part two is attachment
				MimeBodyPart filePart = new MimeBodyPart();
				filePart.setDataHandler(new DataHandler(new FileDataSource("./README.md")));
				filePart.setFileName("file.txt");
				multipart.addBodyPart(filePart);

				initMsg.setContent(multipart);
				return initMsg;
			}
		});
	}

	public void testAsync() throws Exception {
		Mailer mailer = MailConfigBuilder.create().sendConfig().server("smtp.i-on.net").mailUserId("bleujin@i-on.net").mailUserPwd(userPwd).buildConfig().confirmValidOfSendMailConfig().createMailer().executors(Executors.newSingleThreadExecutor());

		mailer.sendMail(new MessageCreater() {
			@Override
			public MimeMessage makeMessage(MimeMessage msg) throws MessagingException {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress("bleujin@i-on.net"));
				msg.setSubject("This is the Subject Line!");
				msg.setContent("<h1>This is actual message</h1>", "text/html");
				return msg;
			}
		}).get();
	}

}
