/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.openkm.frontend.client.widget.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.foldertree.FolderSelectPopup;
import com.openkm.frontend.client.widget.properties.attachment.ExtendedFlexTable;
import com.openkm.frontend.client.widget.properties.attachment.MenuPopup;

/**
 * Mail
 * 
 * @author jllort
 *
 */
public class Mail extends Composite {
	
	private FlexTable table;
	private FlexTable dataTable;
	private ExtendedFlexTable attachmentsTable;
	private HorizontalPanel contentPanel;
	private ScrollPanel scrollPanel;
	private HTML content;
	public MenuPopup menuPopup;
	private Map<Integer, GWTDocument> attachmentsList;
	private GWTMail mail;
	
	public Mail() {
		table = new FlexTable();
		dataTable = new FlexTable();
		attachmentsTable = new ExtendedFlexTable();
		contentPanel = new HorizontalPanel();
		scrollPanel = new ScrollPanel(table);
		menuPopup = new MenuPopup();
		attachmentsList = new HashMap<Integer, GWTDocument>();
		
		table.setCellPadding(0);
		table.setCellSpacing(0);
		dataTable.setCellPadding(3);
		dataTable.setCellSpacing(2);
		
		dataTable.setHTML(0, 0, "<b>"+Main.i18n("mail.from")+"</b>");
		dataTable.setHTML(0, 1, "");
		dataTable.setHTML(0, 2, "");
		dataTable.setHTML(1, 0, "<b>"+Main.i18n("mail.reply")+"</b>");
		dataTable.setHTML(1, 1, "");
		dataTable.setHTML(2, 0, "<b>"+Main.i18n("mail.to")+"</b>");
		dataTable.setHTML(3, 1, "");
		dataTable.setHTML(3, 0, "<b>"+Main.i18n("mail.subject")+"</b>");	
		
		dataTable.getCellFormatter().setWidth(0, 2, "100%");
		dataTable.getCellFormatter().setVerticalAlignment(1, 1, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(2, 1, HasAlignment.ALIGN_TOP);
		
		// Sets wordWrap for al rows
		for (int i=0; i<4; i++) {
			setRowWordWarp(i, 2, false, dataTable);
		}
		
		HTML separatorLeft = new HTML("");
		HTML separatorRight = new HTML("");
		content = new HTML("");
		contentPanel.add(separatorLeft);
		contentPanel.add(content);
		contentPanel.add(separatorRight);
		
		contentPanel.setCellWidth(separatorLeft, "5px");
		content.setWidth("100%");
		contentPanel.setCellWidth(separatorRight, "5px");
		contentPanel.setWidth("100%");
		
		attachmentsTable.setCellPadding(3);
		attachmentsTable.setCellSpacing(2);
		attachmentsTable.setHTML(0, 0, "<b>"+Main.i18n("mail.attachment")+"</b>");
		attachmentsTable.getFlexCellFormatter().setColSpan(0, 0, 3);
		attachmentsTable.getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		
		table.setWidget(0, 0, dataTable);
		table.setWidget(0, 1, attachmentsTable);
		table.setHTML(1, 0, "");
		table.setWidget(2, 0, contentPanel);
		
		table.getCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setVerticalAlignment(0, 1, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setHorizontalAlignment(0, 1, HasAlignment.ALIGN_RIGHT);
		
		table.getFlexCellFormatter().setHeight(1,0,"10px");
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		table.getRowFormatter().setStyleName(1, "okm-Mail-White");
		table.getRowFormatter().setStyleName(2, "okm-Mail-White");
		table.setWidth("100%");

		dataTable.setStyleName("okm-DisableSelect");
		table.setStyleName("okm-Mail");
		menuPopup.setStyleName("okm-Mail-MenuPopup");
		attachmentsTable.setStyleName("okm-NoWrap");
		
		initWidget(scrollPanel);
	}
	
	/**
	 * Set the WordWarp for all the row cells
	 * 
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i=0; i<columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}
	
	/**
	 * get
	 * 
	 * @return
	 */
	public GWTMail get() {
		return mail;
	}
	
	/**
	 * Sets the mail values
	 * 
	 * @param mail The document object
	 */
	public void set(GWTMail mail) {
		this.mail = mail;
		Anchor hFrom = new Anchor();
		final String mailFrom = mail.getFrom().contains("<")?mail.getFrom().substring(mail.getFrom().indexOf("<")+1, mail.getFrom().indexOf(">")):mail.getFrom();
		hFrom.setHTML(mail.getFrom().replace("<", "&lt;").replace(">", "&gt;"));
		hFrom.setTitle("mailto:"+mailFrom);
		hFrom.setStyleName("okm-Mail-Link");
		hFrom.addStyleName("okm-NoWrap");
		hFrom.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Window.open("mailto:" + mailFrom, "_self", "");
			}
		});
		dataTable.setWidget(0, 1, hFrom);
		
		VerticalPanel replyPanel = new VerticalPanel();
		for (int i=0; i<mail.getReply().length; i++) {
			Anchor hReply = new Anchor();
			final String mailReply = mail.getReply()[i].contains("<")?mail.getReply()[i].substring(mail.getReply()[i].indexOf("<")+1, mail.getReply()[i].indexOf(">")):mail.getReply()[i];
			hReply.setHTML(mail.getReply()[i].replace("<", "&lt;").replace(">", "&gt;"));
			hReply.setTitle("mailto:"+mailReply);
			hReply.setStyleName("okm-Mail-Link");
			hReply.addStyleName("okm-NoWrap");
			hReply.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					Window.open("mailto:" + mailReply, "_self", "");
				}
			});
			replyPanel.add(hReply);
		}
		dataTable.setWidget(1, 1, replyPanel);
		
		VerticalPanel toPanel = new VerticalPanel();
		for (int i=0; i<mail.getTo().length; i++) {
			Anchor hTo = new Anchor();
			final String mailTo = mail.getTo()[i].contains("<")?mail.getTo()[i].substring(mail.getTo()[i].indexOf("<")+1, mail.getTo()[i].indexOf(">")):mail.getTo()[i];
			hTo.setHTML(mail.getTo()[i].replace("<", "&lt;").replace(">", "&gt;"));
			hTo.setTitle("mailto:"+mailTo);
			hTo.setStyleName("okm-Mail-Link");
			hTo.addStyleName("okm-NoWrap");
			hTo.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					Window.open("mailto:" + mailTo, "_self", "");
				}
			});
			toPanel.add(hTo);
		}
		dataTable.setWidget(2, 1, toPanel);
		
		dataTable.setHTML(3, 1, mail.getSubject());
		if (mail.getMimeType().equals("text/plain")) {
			content.setHTML(mail.getContent().replace("\n", "<br/>"));
		} else {
			content.setHTML(mail.getContent());
		}
		
		attachmentsTable.removeAllRows();
		attachmentsList = new HashMap<Integer, GWTDocument>();
		int count = 0;
		
		for (Iterator<GWTDocument> it = mail.getAttachments().iterator(); it.hasNext();) {
			final GWTDocument attach = it.next();
			attachmentsList.put(new Integer(count), attach);
			count++;
			Anchor hLink = new Anchor();
			hLink.setHTML(attach.getName());
			hLink.setTitle(attach.getName());
			hLink.setStyleName("okm-Mail-Link");
			hLink.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					Util.downloadFile(attach.getPath(), "");
				}
			});
			int row = attachmentsTable.getRowCount();
			attachmentsTable.setHTML(row, 0, Util.mimeImageHTML(attach.getMimeType()));
			attachmentsTable.setWidget(row, 1, hLink);
			attachmentsTable.setHTML(row, 2, Util.formatSize(attach.getActualVersion().getSize()));
			attachmentsTable.getCellFormatter().setHorizontalAlignment(row, 2, HasAlignment.ALIGN_CENTER);
		}
	}
	
	/**
	 * Gets the attach
	 * 
	 * @param selectedRow The selected row
	 * 
	 * @return The attach
	 */
	public GWTDocument getAttach(int selectedRow) {
		// Attachements are writem from second row
		if (attachmentsTable.getSelectedRow()>0 && attachmentsList.containsKey(new Integer(selectedRow-1))) {
			return (attachmentsList.get(new Integer(selectedRow-1)));
		} else {
			return null;
		}
	}
	
	public void downloadAttachment() {
		// Attachements are writem from second row
		if (attachmentsTable.getSelectedRow()>0) {
			if (getAttach(attachmentsTable.getSelectedRow())!=null) {
				Util.downloadFile(getAttach(attachmentsTable.getSelectedRow()).getPath(), "");
			}
		}
	}
	
	public void copyAttachment() {
		// Attachements are writem from second row
		if (attachmentsTable.getSelectedRow()>0) {
			Main.get().activeFolderTree.folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_MAIL_ATTACH);
			Main.get().activeFolderTree.folderSelectPopup.setToCopy(getAttach(attachmentsTable.getSelectedRow()));
			Main.get().activeFolderTree.showDirectorySelectPopup();
		}
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		dataTable.setHTML(0, 0, "<b>"+Main.i18n("mail.from")+"</b>");
		dataTable.setHTML(1, 0, "<b>"+Main.i18n("mail.reply")+"</b>");
		dataTable.setHTML(2, 0, "<b>"+Main.i18n("mail.to")+"</b>");
		dataTable.setHTML(3, 0, "<b>"+Main.i18n("mail.subject")+"</b>");
		attachmentsTable.setHTML(0, 0, "<b>"+Main.i18n("mail.attachment")+"</b>");
		menuPopup.langRefresh();
	}	
}
