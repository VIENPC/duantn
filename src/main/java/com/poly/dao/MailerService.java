package com.poly.dao;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import com.poly.entities.*;



public interface MailerService {
	/**
	 * Gửi email
	 * 
	 * @param mail thông tin email
	 * @throws MessagingException lỗi gửi email
	 */
	void send(MailInfo mail) throws MessagingException;

	/**
	 * Gửi email đơn giản
	 * 
	 * @param to      email người nhận
	 * @param subject tiêu đề email
	 * @param body    nội dung email
	 * @throws MessagingException lỗi gửi email
	 */
	void send(String to, String subject, String body) throws MessagingException;

	/**
	 * Xếp mail vào hàng đợi * @param mail thông tin email
	 */

	
	void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException;
}