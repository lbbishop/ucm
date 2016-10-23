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

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.sfs.ucm.controller.AppManager;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.model.Issue;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.util.Service;

/**
 * Notifier
 * 
 * @author lbbisho
 * 
 */
@Service
@Model
public class Notifier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private AppManager appManager;

	@Inject
	@Service
	private MailService mailService;


	/**
	 * Notify assignee of issue
	 * <p>
	 * 
	 * 
	 * @param dispositionRequest
	 * @param line
	 */
	public void notifyAssigneeOfIssue(final ProjectMember assignee, final Issue issue) {

		String fromAddress = null;
		String recipient = null;

		try {

			fromAddress = this.appManager.getApplicationProperty("mail.reply.address");

			if (this.appManager.getEnv().equals(Literal.ENV_LOCAL.toString()) || this.appManager.getEnv().equals(Literal.ENV_DEV.toString())) {
				recipient = this.appManager.getApplicationProperty("mail.test.recipient");
			}
			else {
				recipient = assignee.getAuthUser().getEmail();
			}

			String subject = "Issue " + issue.getIdentifier() + " has been assigned to you.";

			StringBuilder body = new StringBuilder();

			body.append("<p style='font-family:verdana;font-size:9pt'>");
			body.append("Issue " + issue.getIdentifier() + " has been assigned to you.");
			body.append("</p>");

			body.append("<p style='font-family:verdana;font-size:9pt'>");
			body.append("You will be notified when this Disposition Request has been reviewed for Waste/Material Pickup.");
			body.append("</p>");

			body.append("<p style='font-family:verdana;font-size:9pt'>");
			body.append("Do not reply to this message as it was sent from an unattended mail box.");
			body.append("</p>");

			this.mailService.sendMessage(fromAddress, null, recipient, subject, body.toString(), "text/html");

		}
		catch (Exception e) {
			String msg = "Error occurred send mail message: " + e.getMessage();
			logger.error(msg);
		}
	}

}
