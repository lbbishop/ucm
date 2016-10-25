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
package com.sfs.ucm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;

@Named("appManager")
@ApplicationScoped
public class AppManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String releaseVersion;
	private String releaseBuildDate;

	private int navigatorWidth;
	private int growlLife; // non-sticky growl message life in milliseconds
	private int menuHeight;
	private int tableScrollHeight;
	private int listScrollHeight; // scroll height for viewing all artifacts
	private String displayName;

	public AppManager() {
		this.name = Literal.APPNAME.toString();
		this.releaseVersion = "1.0.0";
		this.releaseBuildDate = "10/24/2016";
		this.navigatorWidth = Constants.SIDEBAR_WIDTH;
		this.growlLife = Constants.GROWL_LIFE;
		this.menuHeight = Constants.MENU_HEIGHT;
		this.tableScrollHeight = Constants.TABLE_SCROLL_HEIGHT;
		this.listScrollHeight = Constants.LIST_SCROLL_HEIGHT;
		
		// display name
		this.displayName = Literal.APPNAME.toString();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the releaseVersion
	 */
	public String getReleaseVersion() {
		return releaseVersion;
	}

	/**
	 * @param releaseVersion
	 *            the releaseVersion to set
	 */
	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	/**
	 * @return the releaseBuildDate
	 */
	public String getReleaseBuildDate() {
		return releaseBuildDate;
	}

	/**
	 * @param releaseBuildDate
	 *            the releaseBuildDate to set
	 */
	public void setReleaseBuildDate(String releaseBuildDate) {
		this.releaseBuildDate = releaseBuildDate;
	}

	/**
	 * @return the navigatorWidth
	 */
	public int getNavigatorWidth() {
		return navigatorWidth;
	}

	/**
	 * @param navigatorWidth
	 *            the navigatorWidth to set
	 */
	public void setNavigatorWidth(int navigatorWidth) {
		this.navigatorWidth = navigatorWidth;
	}

	/**
	 * @return the growlLife
	 */
	public int getGrowlLife() {
		return growlLife;
	}

	/**
	 * @param growlLife
	 *            the growlLife to set
	 */
	public void setGrowlLife(int growlLife) {
		this.growlLife = growlLife;
	}

	/**
	 * @return the menuHeight
	 */
	public int getMenuHeight() {
		return menuHeight;
	}

	/**
	 * @param menuHeight
	 *            the menuHeight to set
	 */
	public void setMenuHeight(int menuHeight) {
		this.menuHeight = menuHeight;
	}

	/**
	 * @return the tableScrollHeight
	 */
	public int getTableScrollHeight() {
		return tableScrollHeight;
	}

	/**
	 * @param tableScrollHeight
	 *            the tableScrollHeight to set
	 */
	public void setTableScrollHeight(int tableScrollHeight) {
		this.tableScrollHeight = tableScrollHeight;
	}

	/**
	 * @return the listScrollHeight
	 */
	public int getListScrollHeight() {
		return listScrollHeight;
	}

	/**
	 * @param listScrollHeight
	 *            the listScrollHeight to set
	 */
	public void setListScrollHeight(int listScrollHeight) {
		this.listScrollHeight = listScrollHeight;
	}

	/**
	 * Retrieve the current environment (local, dev, qual, prod)
	 * 
	 * <p>
	 * The environment is obtained from build.properties found in the top level Java classpath.
	 * 
	 * @return formatted date string representing the environment
	 * @throws UCMException
	 */
	public String getEnv() throws UCMException {

		// Read properties file.
		Properties properties = new Properties();

		try {
			properties.load(AppManager.class.getClassLoader().getResourceAsStream("build.properties"));
		}
		catch (FileNotFoundException e) {
			throw new UCMException(e);
		}
		catch (IOException e) {
			throw new UCMException(e);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}

		return properties.getProperty("environment");

	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Retrieve the application property value
	 * 
	 * <p>
	 * The property value is obtained from app.properties found in the top level Java classpath. The following keys are supported:
	 * <ul>
	 * <li>mail.host - returns the platform specific mail host server</li>
	 * <li>mail.servicerequest.group - returns metagroup members (userId) used for Service Request recipients</li>
	 * <li>mail.feedback.group - returns metagroup members (userId) used for Feedback recipients</li>
	 * <li>mail.test.recipient - returns recipient userId for test mail messages</li>
	 * <li>mail.reply.address - returns fully qualified mail reply address</li>
	 * <li>mail.domain - returns the mail domain name (e.g. mail.sandia.gov) for the current environment</li>
	 * <li>mail.port - the mail server port</li>
	 * <li>host.url - returns the application host URL for the current environment</li>
	 * <li>metagroup.uri - returns the metagroup utility URI (you must append a group name for group lookup)</li>
	 * <li>mailto.application.support - person or distribution list for application support needs</li>
	 * <li>ws.credential.username - web service credential: username</li>
	 * <li>ws.credential.password - web service credential: password</li>
	 * </ul>
	 * 
	 * @return application property value or null if not found
	 * @throws UCMException
	 */
	public String getApplicationProperty(final String key) throws UCMException {

		String value = null;

		// Read properties file.
		Properties properties = new Properties();

		try {
			properties.load(AppManager.class.getClassLoader().getResourceAsStream("app.properties"));
			value = properties.getProperty(key);
		}
		catch (FileNotFoundException e) {
			throw new UCMException(e);
		}
		catch (IOException e) {
			throw new UCMException(e);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}

		return value;
	}

}
