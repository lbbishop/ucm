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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Help;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.HelpUpdated;
import com.sfs.ucm.util.Log;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Handles Help content actions
 * 
 * @author lbbisho
 * 
 */
@Stateful
@Named("helpContentAction")
@ConversationScoped
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class HelpContentAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED, unitName = "wddrPU")
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	private FacesContextMessage facesContextMessage;

	private Help helpItem;

	private List<Help> helpItems;

	@Inject
	@HelpUpdated
	private Event<Help> helpEventSrc;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private boolean selected;

	private int itemCount;

	/**
	 * Constructor
	 */
	public HelpContentAction() {
		super();
	}

	/**
	 * init method
	 */
	@Inject
	public void init() {
		this.selected = false;
		this.helpItem = new Help();

		// begin work unit
		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	@Log
	public void load() throws UCMException {
		loadList();
	}

	/**
	 * save action
	 * 
	 * @throws UCMException
	 */
	@Log
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {
		try {
			if (validate()) {
				if (helpItem.getId() == null) {
					this.helpItems.add(this.helpItem);
				}

				em.persist(helpItem);

				// fire help change event
				helpEventSrc.fire(this.helpItem);

				// queue message
				this.logger.info("Saved {}", this.helpItem.getKeyword());
				facesContextMessage.infoMessage("messages", "{0} saved successfully", this.helpItem.getKeyword());

				loadList();
			}
		}
		catch (Exception e) {
			logger.error("Error occurred saving Help Content. {}", e.getMessage());
			throw new UCMException(e);
		}

	}

	/**
	 * Remove help entry action
	 * 
	 * @throws UCMException
	 */
	@Log
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			em.remove(this.helpItem);

			// refresh list
			loadList();
			this.selected = false;

			// queue message
			this.logger.info("Help Item {} deleted successfully", this.helpItem.getKeyword());
			facesContextMessage.infoMessage("messages", "Help Item {0} deleted successfully", this.helpItem.getKeyword());
		}
		catch (Exception e) {
			logger.error("Error occurred removing Help Content. {}", e.getMessage());
			throw new UCMException(e);
		}
	}

	/**
	 * File Upload Handler
	 * 
	 * @param event
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void handleHelpContentUpload(FileUploadEvent event) throws UCMException {

		try {
			extractHelpContent(event.getFile().getContents());

			updateProducer();

			logger.info("Help Content File {} uploaded successfully", event.getFile().getFileName());
			facesContextMessage.infoMessage("importMessages", "Help Content File {0} uploaded successfully", event.getFile().getFileName());

			// refresh list
			loadList();
		}
		catch (UCMException e) {
			logger.error("An error occurred importing help content {}", e.getMessage());
			throw new UCMException("An error occurred importing help content" + e.getMessage());
		}
	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.helpItem = (Help) event.getObject();
		this.selected = true;
	}

	/**
	 * Close action
	 * 
	 * @return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * Add Action
	 */
	@Log
	public void add() {
		this.helpItem = new Help();
	}

	/**
	 * @return the helpItem
	 */
	public Help getHelpItem() {
		return helpItem;
	}

	/**
	 * @param helpItem
	 *            the helpItem to set
	 */
	public void setHelpItem(Help helpItem) {
		this.helpItem = helpItem;
	}

	/**
	 * @return the helpItems
	 */
	public List<Help> getHelpItems() {
		return helpItems;
	}

	/**
	 * @param helpItems
	 *            the helpItems to set
	 */
	public void setHelpItems(List<Help> helpItems) {
		this.helpItems = helpItems;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * Validate helpItem
	 * <ul>
	 * <li>If new helpItem check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.helpItem.getId() == null) {
			for (Help theHelpItem : this.helpItems) {
				if (this.helpItem.equals(theHelpItem)) {
					facesContextMessage.warningMessage(null, "Help Item {0} already exists", this.helpItem.getKeyword());
					logger.warn("help entry {} already exists", this.helpItem);
					isvalid = false;
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.addCallbackParam("validationFailed", !isvalid);
					break;
				}
			}
		}

		return isvalid;
	}

	/**
	 * load resources
	 * 
	 * @throws UCMException
	 */
	private void loadList() throws UCMException {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Help> c = cb.createQuery(Help.class);
			Root<Help> obj = c.from(Help.class);
			c.select(obj).orderBy(cb.asc(obj.get("keyword")));
			this.helpItems = em.createQuery(c).getResultList();

			this.itemCount = this.helpItems.size();
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Extract help content
	 * 
	 * @param buf
	 *            byte array representing help content file
	 * @throws UCMException
	 */
	private void extractHelpContent(byte[] buf) throws UCMException {

		try {
			InputStream inp = new ByteArrayInputStream(buf);

			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> iter = sheet.iterator();
			Cell cell = null;
			Row row = null;

			// header rows
			if (iter.hasNext()) {
				row = iter.next(); // table title
				row = iter.next(); // column headers
			}

			if (iter.hasNext()) {
				while (iter.hasNext()) {

					// process records
					row = iter.next();

					// help key
					cell = row.getCell(0);

					if (cell != null) {
						String key = cell.getStringCellValue();

						// help content
						cell = row.getCell(1);
						String contents = cell.getStringCellValue();

						// log it
						Object[] values = new Object[3];
						values[0] = row.getRowNum() + 1; // display as one-based
						values[1] = key;
						values[2] = StringUtils.abbreviate(contents, 20);
						logger.info("Processing row {}; contents: {};{}", values);

						// construct the help content object
						Help theHelpItem = new Help(key, contents);

						// if help item already exists then just update its contents otherwise add record
						int ndx = this.helpItems.indexOf(theHelpItem);
						if (ndx == -1) {
							this.helpItems.add(theHelpItem);
							logger.info("Added Help Item {}", theHelpItem.getKeyword());

							// persist the object
							em.persist(theHelpItem);

						}
						else {
							Help tmp = this.helpItems.get(ndx);
							tmp.setContent(contents);

							// persist the object
							em.persist(tmp);
							logger.info("Updated Help Item {}", tmp.getKeyword());

						}
					}
				}
			}

			// done
			inp.close();
		}
		catch (InvalidFormatException e) {
			logger.error(e.getMessage());
			throw new UCMException(e);
		}
		catch (IOException e) {
			logger.error(e.getMessage());
			throw new UCMException(e);
		}
	}

	/**
	 * Helper method to update all help entries.
	 */
	private void updateProducer() {

		for (Help helpItem : helpItems) {
			Help help = em.find(Help.class, helpItem.getId());

			// fire help change event
			helpEventSrc.fire(help);
		}

	}

}
