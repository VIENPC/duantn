package com.poly.service;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.poly.entities.MailInfo;
import com.poly.dao.MailerService;

@Service
public class MailerServiceImpl implements MailerService {

	@Autowired
	private JavaMailSender sender;

	@Override
	public void send(MailInfo mail) throws MessagingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom(mail.getFrom());
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getBody(), true);
		helper.setReplyTo(mail.getFrom());
		String[] cc = mail.getCc();
		if (cc != null && cc.length > 0) {
			helper.setCc(cc);
		}
		String[] bcc = mail.getBcc();
		if (bcc != null && bcc.length > 0) {
			helper.setBcc(bcc);
		}
		String[] attachments = mail.getAttachments();
		if (attachments != null && attachments.length > 0) {
			for (String path : attachments) {
				File file = new File(path);
				helper.addAttachment(file.getName(), file);
			}
		}
		sender.send(message);
	}

	@Override
	public void send(String to, String subject, String body) throws MessagingException {
		this.send(new MailInfo(to, subject, body));
	}

	@Override
	public void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom("viennvpc04385@fpt.edu.vn", "NVA3-Watches");
		helper.setTo(email);
		String button = "background-color:#783ecf;color:#fff;padding:12px 10px;text-decoration:none;border-radius:3px";
		String subject = "Yêu cầu đặt lại mật khẩu của bạn";
		String content = "" + "<div style='font-family:Roboto,sans-serif;width:50%;margin:0 auto;text-align:center'>"
				+ "<div style='font-size:3em;padding:0.5em 1em'><b>Đặt Lại Mật Khẩu</b></div>"
				+ "<div style='background-color:#f0f8ff;font-size:16px;padding:1em 3em'>"
				+ "<p><b style='font-size:18px;'>Ai đó đã yêu cầu đặt lại mật khẩu cho tài khoản sau.</b></p>"
				+ "<p>Để đặt lại mật khẩu của bạn, hãy chi cập địa chỉ sau:</p>"
				+ "<p style='margin-top:2em'><a href=\""
				+ link + "\" style='" + button + "'>Đặt Lại Mật Khẩu Của Bạn</a></p>"
				+ "<p style='margin-bottom:2em'>Mail của bạn: <a href=\"" + "mailto:" + email
				+ "\" style='color:#b745dd;text-decoration:none'>" + email + "</a></p>"
				+ "<p>nếu đây là một sự nhầm lẫn, hãy bỏ qua mail này và không có chuyện gì sẽ xảy ra.</p>" + "</div>"
				+ "<div style='font-size:14px;padding:2em'>© Louvre. All Rights Reserved. Designed by G1 -.</div>"
				+ "</div>";
		helper.setSubject(subject);
		helper.setText(content, true);
		sender.send(message);
	}

	public MailerServiceImpl(JavaMailSender sender) {
		this.sender = sender;
	}

	public void sendInvoiceEmail(String to, String subject, String content)
			throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

		try {

			helper.setFrom("viennvpc04385@fpt.edu.vn", "Shop đồng hồ NVA3");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			sender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			// Handle the exception as needed
		}
	}

}
