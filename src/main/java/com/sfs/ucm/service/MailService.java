/*
 * South Face Software
 * Copyright 2012, South Face Software, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sfs.ucm.service;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.sfs.ucm.controller.AppManager;
import com.sfs.ucm.util.Service;

/**
 * Mail Service
 * 
 * @author lbbisho
 * 
 */
@Named
@Stateless
@Service
public class MailService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private AppManager appManager;

	
	/**
	 * Send mail message in plain text. Mail host is obtained from instance-specific properties file via AppManager.
	 * 
	 * @param fromAddress
	 * @param ccRe
	 * @param toRecipient
	 * @param subject
	 * @param body
	 * @param messageType
	 *            - text/plain or text/html
	 * @return status message
	 * @throws IllegalArgumentException
	 */
	@Asynchronous
	public Future<String> sendMessage(final String fromAddress, final String ccRecipient, final String toRecipient, final String subject, final String body, final String messageType)
			throws IllegalArgumentException {

		// argument validation
		if (fromAddress == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined fromAddress");
		}
		if (toRecipient == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined toRecipient");
		}
		if (subject == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined subject");
		}
		if (body == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined body conent");
		}
		if (messageType == null || (!messageType.equals("text/plain") && !messageType.equals("text/html"))) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined messageType");
		}

		String status = null;

		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", appManager.getApplicationProperty("mail.host"));
			props.put("mail.smtp.port", appManager.getApplicationProperty("mail.port"));

			Object[] params = new Object[4];
			params[0] = (String) subject;
			params[1] = (String) fromAddress;
			params[2] = (String) ccRecipient;
			params[3] = (String) toRecipient;
			logger.info("Sending message: subject: {}, fromAddress: {}, ccRecipient: {}, toRecipient: {}", params);

			Session session = Session.getDefaultInstance(props);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			Address toAddress = new InternetAddress(toRecipient);
			message.addRecipient(Message.RecipientType.TO, toAddress);

			if (StringUtils.isNotBlank(ccRecipient)) {
				Address ccAddress = new InternetAddress(ccRecipient);
				message.addRecipient(Message.RecipientType.CC, ccAddress);
			}
			message.setSubject(subject);
			message.setContent(body, messageType);
			Transport.send(message);
		}
		catch (AddressException e) {
			logger.error("sendMessage Address Exception occurred: {}", e.getMessage());
			status = "sendMessage Address Exception occurred";
		}
		catch (MessagingException e) {
			logger.error("sendMessage Messaging Exception occurred: {}", e.getMessage());
			status = "sendMessage Messaging Exception occurred";
		}

		return new AsyncResult<String>(status);

	}

	/**
	 * Send mail message in plain text. Mail host is obtained from instance-specific properties file via AppManager.
	 * 
	 * @param fromAddress
	 * @param recipients
	 *            - fully qualified recipient address
	 * @param subject
	 * @param body
	 * @param messageType
	 *            - text/plain or text/html
	 * @throws IllegalArgumentException
	 */
	@Asynchronous
	public Future<String> sendMessage(final String fromAddress, final String ccRecipient, final String[] toRecipients, final String subject, final String body, final String messageType) {

		// argument validation
		if (fromAddress == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined fromAddress");
		}
		if (toRecipients == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined toRecipients");
		}
		if (subject == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined subject");
		}
		if (body == null) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined body conent");
		}
		if (messageType == null || (!messageType.equals("text/plain") && !messageType.equals("text/html"))) {
			throw new IllegalArgumentException("sendMessage: Invalid or undefined messageType");
		}

		String status = null;
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", appManager.getApplicationProperty("mail.host"));
			props.put("mail.smtp.port", appManager.getApplicationProperty("mail.port"));

			Object[] params = new Object[4];
			params[0] = (String) subject;
			params[1] = (String) fromAddress;
			params[2] = (String) ccRecipient;
			params[3] = (String) StringUtils.join(toRecipients);
			logger.info("Sending message: subject: {}, fromAddress: {}, ccRecipient: {}, toRecipient: {}", params);

			Session session = Session.getDefaultInstance(props);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			Address[] toAddresses = new Address[toRecipients.length];
			for (int i = 0; i < toAddresses.length; i++) {
				toAddresses[i] = new InternetAddress(toRecipients[i]);
			}
			message.addRecipients(Message.RecipientType.TO, toAddresses);

			if (StringUtils.isNotBlank(ccRecipient)) {
				Address ccAddress = new InternetAddress(ccRecipient);
				message.addRecipient(Message.RecipientType.CC, ccAddress);
			}
			message.setSubject(subject);
			message.setContent(body, messageType);
			Transport.send(message);
		}
		catch (AddressException e) {
			logger.error("sendMessage Address Exception occurred: {}", e.getMessage());
			status = "sendMessage Address Exception occurred";
		}
		catch (MessagingException e) {
			logger.error("sendMessage Messaging Exception occurred: {}", e.getMessage());
			status = "sendMessage Messaging Exception occurred";
		}

		return new AsyncResult<String>(status);

	}
}
