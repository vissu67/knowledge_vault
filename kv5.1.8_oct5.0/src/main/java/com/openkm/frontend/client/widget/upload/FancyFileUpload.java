/**
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
package com.openkm.frontend.client.widget.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.ProgressBar;
import com.google.gwt.widgetideas.client.ProgressBar.TextFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTFileUploadingStatus;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.contants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.notify.NotifyPanel;

/**
 * FancyFileUpload
 * 
 * @author jllort
 */
public class FancyFileUpload extends Composite implements HasText, HasChangeHandlers {
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	private final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT.create(OKMRepositoryService.class);
	
	/**
	 * State definitions
	 */
	public static final int EMPTY_STATE = 1;
	public static final int PENDING_STATE = 2;
	public static final int UPLOADING_STATE = 3;
	public static final int UPLOADED_STATE = 4;
	public static final int FAILED_STATE = 5;
	
	/**
	 * OK message expected from file upload servlet to indicate successful
	 * upload.
	 */
	private static final String returnOKMessage = "OKM_OK";
	private static final String returnErrorMessage = "OKM-";
	
	/**
	 * Initial State of the widget.
	 */
	private int widgetState = EMPTY_STATE;
	
	/**
	 * Default delay for pending state, when delay over the form is submitted.
	 */
	private int pendingUpdateDelay = 1000;
	
	private VerticalPanel mainPanel = new VerticalPanel();
	public CheckBox notifyToUser = new CheckBox();
	private CheckBox importZip = new CheckBox();
	private CheckBox digitalSignature = new CheckBox();
	private HTML versionCommentText = new HTML();
	private HTML notifyToUserText = new HTML();
	private HTML importZipText = new HTML();
	private HTML digitalSignatureText = new HTML();
	private HorizontalPanel hNotifyPanel = new HorizontalPanel();
	private HorizontalPanel hUnzipPanel = new HorizontalPanel();
	private HorizontalPanel hDigitalSignaturePanel = new HorizontalPanel();
	public NotifyPanel notifyPanel = new NotifyPanel();
	private HTML versionHTMLBR;
	private TextArea versionComment;
	private ScrollPanel versionCommentScrollPanel;
	public TextBox users;
	public TextBox roles;
	private TextArea message;
	private VerticalPanel vNotifyPanel = new VerticalPanel();
	private VerticalPanel vVersionCommentPanel = new VerticalPanel();
	private HTML commentTXT;
	private ScrollPanel messageScroll;
	public HTML errorNotify;
	private ProgressBar progressBar;
	private TextFormatter progressiveFormater;
	private TextFormatter finalFormater;
	private boolean wizard = false;
	private int action = UIFileUploadConstants.ACTION_NONE;
	private FileUploadForm uploadForm;
	private List<FileToUpload> filesToUpload = new ArrayList<FileToUpload>();
	private FileToUpload actualFileToUpload;
	private List<FileToUpload> uploadedWorkflowFiles = new ArrayList<FileToUpload>();
	
	/**
	 * Internal timer for checking if pending delay is over.
	 */
	private Timer p;
	
	/**
	 * Widget representing file to be uploaded.
	 */
	private UploadDisplay uploadItem;
	
	/**
	 * FileName to be uploaded
	 */
	String fileName = "";
	
	/**
	 * Uploading status
	 */
	private GWTFileUploadingStatus fileUploadingStatus = new GWTFileUploadingStatus();
	private boolean fileUplodingStartedFlag = false;
	
	/**
	 * Class used for the display of filename to be uploaded, and handling the
	 * update of the display states.
	 */
	protected class UploadDisplay extends Composite {
		
		/**
		 * Label to display after file widget is filled with a filename
		 */
		HTML status = new HTML();
		
		/**
		 * Label to display if some error on unzip uplaoded file
		 */
		HTML statusZipNotify;
		ScrollPanel statusZipNotifyScroll;
		
		/**
		 * Panel to hold the widget
		 */
		FlowPanel mainPanel = new FlowPanel();
		
		/**
		 * Panel to hold pending, loading, loaded or failed state details.
		 */
		VerticalPanel pendingPanel = new VerticalPanel();
		
		HorizontalPanel hFileUpload = new HorizontalPanel();
		
		/**
		 * Constructor
		 */
		public UploadDisplay() {
			hFileUpload.setWidth("350");
			
			status.setWidth("100%");
			status.setWordWrap(true);
			status.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
			
			// Adds error panel when zip file is uploaded
			statusZipNotify = new HTML();
			statusZipNotify.setSize("100%", "100%");
			statusZipNotify.setVisible(true);
			statusZipNotifyScroll = new ScrollPanel(statusZipNotify);
			statusZipNotifyScroll.setAlwaysShowScrollBars(false);
			statusZipNotifyScroll.setVisible(false);
			statusZipNotifyScroll.setSize("375", "100");
			statusZipNotifyScroll.setStyleName("okm-Bookmark-Panel");
			statusZipNotifyScroll.addStyleName("okm-Input");
			
			progressiveFormater = new TextFormatter() {
				@Override
				protected String getText(ProgressBar bar, double curProgress) {
					String text = "";
					text += Util.formatSize(curProgress);
					text += " " + Main.i18n("fileupload.status.of") + " ";
					text += Util.formatSize(progressBar.getMaxProgress());
					text += " " + (int) (100 * progressBar.getPercent()) + "% ";
					return text;
				}
			};
			
			finalFormater = new TextFormatter() {
				@Override
				protected String getText(ProgressBar bar, double curProgress) {
					String text = " " + (int) (100 * progressBar.getPercent()) + "% ";
					return text;
				}
			};
			
			progressBar = new ProgressBar();
			progressBar.setTextFormatter(progressiveFormater);
			
			HorizontalPanel hPBPanel = new HorizontalPanel();
			hPBPanel.add(progressBar);
			hPBPanel.setCellVerticalAlignment(progressBar, HasAlignment.ALIGN_MIDDLE);
			hPBPanel.setCellHorizontalAlignment(progressBar, HasAlignment.ALIGN_LEFT); // Corrects
																						// some
																						// problem
																						// with
																						// centering
																						// progress
																						// status
			progressBar.setSize("360", "20");
			
			pendingPanel.setWidth("375");
			pendingPanel.setVisible(true);
			pendingPanel.add(status);
			pendingPanel.add(hPBPanel);
			
			pendingPanel.setCellHorizontalAlignment(hPBPanel, HasAlignment.ALIGN_CENTER);
			
			mainPanel.add(hFileUpload);
			mainPanel.add(pendingPanel);
			mainPanel.add(statusZipNotifyScroll);
			
			initWidget(mainPanel);
		}
		
		/**
		 * Set the widget into pending mode by altering style of pending panel
		 * and displaying it. Hide the FileUpload widget and finally set the
		 * state to Pending.
		 */
		private void setPending() {
			status.setHTML(Main.i18n("fileupload.status.sending"));
			pendingPanel.setStyleName("fancyfileupload-pending");
			widgetState = PENDING_STATE;
			fireChange();
		}
		
		/**
		 * Set the widget into Loading mode by changing the style name and
		 * updating the widget State to Uploading.
		 */
		public void setLoading() {
			pendingPanel.setStyleName("fancyfileupload-loading");
			hFileUpload.setVisible(false);
			pendingPanel.setVisible(true);
			widgetState = UPLOADING_STATE;
			fileUplodingStartedFlag = true; // Activates flash uploading is
											// started
			getFileUploadStatus();
			fireChange();
		}
		
		/**
		 * Set the widget into pending mode by altering style of pending panel
		 * and displaying it. Hide the FileUpload widget and finally set the
		 * state to Pending.
		 */
		private void setIndexing() {
			status.setHTML(Main.i18n("fileupload.status.indexing"));
		}
		
		/**
		 * Set the widget to Loaded mode by changing the style name and updating
		 * the widget State to Loaded.
		 */
		private void setLoaded() {
			// Sometimes if upload is fast, has no time to getting file
			// uploading status information
			// on this cases must be setting it directly ( simulating )
			if (fileUploadingStatus.getContentLength() == 0) {
				progressBar.setTextFormatter(finalFormater);
				progressBar.setMaxProgress(100);
				progressBar.setProgress(100);
			}
			
			pendingPanel.setStyleName("fancyfileupload-loaded");
			status.setHTML(Main.i18n("fileupload.status.ok"));
			widgetState = UPLOADED_STATE;
			fileUplodingStartedFlag = false;
			
			// normal case is not a workflow
			if (!wizard && actualFileToUpload.getWorkflow()==null) {
				refresh();
			}
			
			fireChange();
			Main.get().mainPanel.dashboard.userDashboard.getUserLastModifiedDocuments();
			Main.get().mainPanel.dashboard.userDashboard.getUserCheckedOutDocuments();
			Main.get().mainPanel.dashboard.userDashboard.getUserLastUploadedDocuments();
			Main.get().workspaceUserProperties.getUserDocumentsSize();
			uploadNewPendingFile();
		}
		
		/**
		 * Set the widget to Failed mode by changing the style name and updating
		 * the widget State to Failed. Additionally, hide the pending panel and
		 * display the FileUpload widget.
		 */
		private void setFailed(String msg) {
			// Sometimes if upload is fast, has no time to getting file
			// uploading status information
			// on this cases must be setting it directly ( simulating )
			if (fileUploadingStatus.getContentLength() == 0) {
				progressBar.setTextFormatter(finalFormater);
				progressBar.setMaxProgress(100);
				progressBar.setProgress(100);
			}
			
			if (importZip.getValue()) {
				statusZipNotify.setHTML(msg.replaceAll("\n", "<br/>"));
				statusZipNotifyScroll.setVisible(true);
				pendingPanel.setVisible(true);
				status.setText(Main.i18n("fileupload.label.error.importing.zip"));
			} else if (msg.contains(returnErrorMessage)) {
				status.setHTML(Main.i18n(msg.substring(msg.indexOf("OKM"), msg.indexOf("OKM") + 10)));
			} else {
				status.setHTML(msg);
			}
			
			pendingPanel.setStyleName("fancyfileupload-failed");
			widgetState = FAILED_STATE;
			fileUplodingStartedFlag = false;
			refresh();
			fireChange();
		}
		
		/**
		 * Reset the display
		 */
		private void reset(boolean enableImport, boolean enableNotifyButton) {
			widgetState = EMPTY_STATE;
			fireChange();
			
			// Reseting values
			fileName = "";
			status.setText("");
			statusZipNotify.setText("");
			statusZipNotifyScroll.setVisible(false);
			message.setText("");
			versionComment.setText("");
			users.setText("");
			roles.setText("");
			notifyPanel.reset();
			getAllUsers();
			
			// On on root stack panel enabled must be enabled notify to user
			// option
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TAXONOMY) {
				hNotifyPanel.setVisible(enableNotifyButton);
			} else {
				hNotifyPanel.setVisible(enableNotifyButton);
			}
			
			errorNotify.setVisible(false);
			vNotifyPanel.setVisible(false);
			notifyToUser.setValue(false);
			importZip.setValue(false);
			digitalSignature.setValue(false);
			hFileUpload.setVisible(true);
			pendingPanel.setVisible(false);
			hUnzipPanel.setVisible(enableImport);
			hDigitalSignaturePanel.setVisible(true);
			
			resetProgressBar();
		}
		
		/**
		 * Inits values before reset ( used to correct center panel )
		 */
		private void init() {
			vNotifyPanel.setVisible(true);
		}
	}
	
	/**
	 * Refresh folders and documents
	 */
	public void refresh() {
		if (importZip.getValue()) {
			Main.get().activeFolderTree.refresh(true);
		} else {
			Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
		}
	}
	
	/**
	 * Perform the uploading of a file by changing state of display widget and
	 * then calling form.submit() method.
	 */
	private void uploadFiles() {
		fileName = uploadForm.getFileName();
		// Store some values to uploadForm
		uploadForm.setNotifyToUser(notifyToUser.getValue());
		uploadForm.setImportZip(importZip.getValue());
		uploadForm.setDigitalSignature(digitalSignature.getValue());
		uploadForm.setVersionCommnent(versionComment.getText());
		uploadForm.setUsers(users.getText());
		uploadForm.setRoles(roles.getText());
		uploadForm.setMessage(message.getText());
		uploadItem.setLoading();
		uploadForm.submit();
	}
	
	/**
	 * Put the widget into a Pending state, set the Pending delay timer to call
	 * the upload file method when ran out.
	 */
	public void pendingUpload() {
		// Fire an onChange event to anyone who is listening
		uploadItem.setPending();
		p = new Timer() {
			public void run() {
				uploadFiles();
			}
		};
		p.schedule(pendingUpdateDelay);
	}
	
	/**
	 * FancyFileUpload.
	 */
	public FancyFileUpload() {
		// Create a new upload display widget
		uploadItem = new UploadDisplay();
		// Add the new widget to the panel.
		mainPanel.add(uploadItem);
		
		// Adds error panel, whem user select notify but not select any user
		errorNotify = new HTML(Main.i18n("fileupload.label.must.select.users"));
		errorNotify.setWidth("370");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");
		mainPanel.add(errorNotify);
		
		// Adds version comment
		versionHTMLBR = new HTML("<br>");
		mainPanel.add(versionHTMLBR);
		versionComment = new TextArea();
		versionComment.setWidth("375");
		versionComment.setHeight("50");
		versionComment.setName("comment");
		versionComment.setStyleName("okm-TextArea");
		versionCommentText = new HTML(Main.i18n("fileupload.label.comment"));
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		// Table for solve some visualization problems
		versionCommentScrollPanel = new ScrollPanel(versionComment);
		versionCommentScrollPanel.setAlwaysShowScrollBars(false);
		versionCommentScrollPanel.setSize("100%", "100%");
		vVersionCommentPanel.add(versionCommentText);
		vVersionCommentPanel.add(versionCommentScrollPanel);
		mainPanel.add(vVersionCommentPanel);
		
		// Ads unzip file
		importZip = new CheckBox();
		importZip.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (importZip.getValue()) {
					notifyToUser.setValue(false);
					vNotifyPanel.setVisible(false);
					digitalSignature.setValue(false);
					hDigitalSignaturePanel.setVisible(false);
				} else {
					hDigitalSignaturePanel.setVisible(true);
				}
			}
		});
		importZip.setName("importZip");
		importZipText = new HTML(Main.i18n("fileupload.label.importZip"));
		hUnzipPanel = new HorizontalPanel();
		hUnzipPanel.add(importZip);
		hUnzipPanel.add(importZipText);
		hUnzipPanel.setCellVerticalAlignment(importZip, VerticalPanel.ALIGN_MIDDLE);
		hUnzipPanel.setCellVerticalAlignment(importZipText, VerticalPanel.ALIGN_MIDDLE);
		mainPanel.add(new HTML("<br>"));
		mainPanel.add(hUnzipPanel);
		
		// Adds digital signature
		digitalSignature = new CheckBox();
		digitalSignature.setName("digitalSignature");
		digitalSignatureText = new HTML(Main.i18n("fileupload.digital.signature"));
		hDigitalSignaturePanel = new HorizontalPanel();
		mainPanel.add(hDigitalSignaturePanel);
		
		// Adds the notify checkbox
		users = new TextBox();
		users.setName("users");
		users.setVisible(false);
		roles = new TextBox();
		roles.setName("roles");
		roles.setVisible(false);
		notifyToUser = new CheckBox();
		notifyToUser.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (notifyToUser.getValue()) {
					vNotifyPanel.setVisible(true);
					importZip.setValue(false);
					hDigitalSignaturePanel.setVisible(true);
				} else {
					errorNotify.setVisible(false);
					vNotifyPanel.setVisible(false);
				}
			}
		});
		notifyToUser.setName("notify");
		notifyToUserText = new HTML(Main.i18n("fileupload.label.users.notify"));
		hNotifyPanel = new HorizontalPanel();
		hNotifyPanel.add(notifyToUser);
		hNotifyPanel.add(notifyToUserText);
		hNotifyPanel.setCellVerticalAlignment(notifyToUser, VerticalPanel.ALIGN_MIDDLE);
		hNotifyPanel.setCellVerticalAlignment(notifyToUserText, VerticalPanel.ALIGN_MIDDLE);
		mainPanel.add(hNotifyPanel);
		mainPanel.add(new HTML("<br>"));
		
		// The notify user tables
		message = new TextArea();
		commentTXT = new HTML(Main.i18n("fileupload.label.notify.comment"));
		message.setName("message");
		message.setSize("375", "60");
		message.setStyleName("okm-TextArea");
		
		vNotifyPanel = new VerticalPanel();
		vNotifyPanel.add(commentTXT);
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);
		
		vNotifyPanel.add(messageScroll);
		vNotifyPanel.add(new HTML("<br>"));
		vNotifyPanel.add(notifyPanel);
		vNotifyPanel.add(new HTML("<br>"));
		
		mainPanel.add(users);
		mainPanel.add(roles);
		mainPanel.add(vNotifyPanel);
		
		// Set align to panels
		mainPanel.setCellHorizontalAlignment(hNotifyPanel, HorizontalPanel.ALIGN_LEFT);
		mainPanel.setCellHorizontalAlignment(hUnzipPanel, HorizontalPanel.ALIGN_LEFT);
		mainPanel.setCellHorizontalAlignment(hDigitalSignaturePanel, HorizontalPanel.ALIGN_LEFT);
		mainPanel.setCellHorizontalAlignment(vNotifyPanel, HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellHorizontalAlignment(vVersionCommentPanel, HorizontalPanel.ALIGN_CENTER);
		
		// Initialices users
		getAllUsers();
		
		// Initialise the widget.
		initWidget(mainPanel);
	}
	
	/**
	 * Reset he upload
	 */
	public void reset(boolean enableImport, boolean enableNotifyButton) {
		uploadItem.reset(enableImport, enableNotifyButton);
	}
	
	/**
	 * Init he upload
	 */
	public void init() {
		uploadItem.init();
	}
	
	/**
	 * Get the text from the widget - which in reality will be retrieving any
	 * value set in the Label element of the display widget.
	 */
	public String getText() {
		return uploadItem.status.getText();
	}
	
	/**
	 * Cannot set the text of a File Upload Widget, so raise an exception.
	 */
	public void setText(String text) {
		throw new RuntimeException("Cannot set text of a FileUpload Widget");
	}
	
	/**
	 * Retrieve the status of the upload widget.
	 * 
	 * @return Status of upload widget.
	 */
	public int getUploadState() {
		return widgetState;
	}
	
	/**
	 * isWizard
	 * 
	 * @return
	 */
	public boolean isWizard() {
		return wizard;
	}
	
	/**
	 * Set the delay value indicating how long a file will remain in pending
	 * mode prior to the upload action taking place.
	 * 
	 * @param newDelay
	 */
	public void setPendingDelay(int newDelay) {
		pendingUpdateDelay = newDelay;
	}
	
	/**
	 * Return value set for pending delay.
	 * 
	 * @return
	 */
	public int getPendingDelay() {
		return pendingUpdateDelay;
	}
	
	/**
	 * fire a change event
	 */
	private void fireChange() {
		NativeEvent nativeEvent = Document.get().createChangeEvent();
		ChangeEvent.fireNativeEvent(nativeEvent, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com
	 * .google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}
	
	/**
	 * getAction
	 * 
	 * @return
	 */
	public int getAction() {
		return action;
	}
	
	public void setAction(int action) {
		this.action = action;
		switch (action) {
			case UIFileUploadConstants.ACTION_INSERT:
				versionComment.setVisible(false);
				versionCommentText.setVisible(false);
				versionHTMLBR.setVisible(false);
				break;
			
			case UIFileUploadConstants.ACTION_UPDATE:
				versionComment.setVisible(true);
				versionCommentText.setVisible(true);
				versionHTMLBR.setVisible(true);
				break;
		}
		uploadForm.setAction("" + action);
	}
	
	/**
	 * Set the paht
	 * 
	 * @param path String path
	 */
	public void setPath(String path) {
		uploadForm.setPath(path);
	}
	
	/**
	 * setRename
	 *  
	 * @param rename
	 */
	public void setRename(String rename) {
		uploadForm.setRename(rename);
	}
	
	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		notifyToUserText.setHTML(Main.i18n("fileupload.label.users.notify"));
		importZipText.setHTML(Main.i18n("fileupload.label.importZip"));
		digitalSignatureText.setHTML(Main.i18n("fileupload.digital.signature"));
		versionCommentText.setHTML(Main.i18n("fileupload.label.comment"));
		commentTXT.setHTML(Main.i18n("fileupload.label.notify.comment"));
		notifyPanel.langRefresh();
	}
	
	/**
	 * Call back get file upload status
	 */
	final AsyncCallback<GWTFileUploadingStatus> callbackGetFileUploadStatus = new AsyncCallback<GWTFileUploadingStatus>() {
		public void onSuccess(GWTFileUploadingStatus result) {
			fileUploadingStatus = result;
			
			if (fileUplodingStartedFlag) {
				if (result.isStarted()) {
					if (result.getContentLength() != 0 && result.getContentLength() == result.getBytesRead()) {
						result.setUploadFinish(true);
						uploadItem.setIndexing();
					}
					
					if (result.isUploadFinish()) {
						progressBar.setTextFormatter(finalFormater);
					}
					
					progressBar.setMaxProgress(fileUploadingStatus.getContentLength());
					progressBar.setProgress(fileUploadingStatus.getBytesRead());
				}
				
				if (!result.isUploadFinish()) {
					getFileUploadStatus();
				}
			}
		}
		
		public void onFailure(Throwable caught) {
			Main.get().showError("getFileUploadStatus", caught);
		}
	};
	
	/**
	 * Resets the progress bar and all related values
	 */
	private void resetProgressBar() {
		fileUplodingStartedFlag = false;
		fileUploadingStatus = new GWTFileUploadingStatus();
		progressBar.setMinProgress(0);
		progressBar.setMaxProgress(0);
		progressBar.setProgress(0);
		progressBar.setTextFormatter(progressiveFormater);
	}
	
	/**
	 * Gets all users
	 */
	private void getAllUsers() {
		notifyPanel.getAll();
	}
	
	private void getFileUploadStatus() {
		generalService.getFileUploadStatus(callbackGetFileUploadStatus);
	}
	
	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}
	
	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}
	
	/**
	 * isDigitalSignature
	 * 
	 * @return
	 */
	public boolean isDigitalSignature() {
		return uploadForm.isDigitalSignature();
	}
	
	/**
	 * showDigitalSignature
	 */
	public void showDigitalSignature() {
		hDigitalSignaturePanel.add(digitalSignature);
		hDigitalSignaturePanel.add(digitalSignatureText);
		hDigitalSignaturePanel.setCellVerticalAlignment(digitalSignature, VerticalPanel.ALIGN_MIDDLE);
		hDigitalSignaturePanel.setCellVerticalAlignment(digitalSignatureText, VerticalPanel.ALIGN_MIDDLE);
	}
	
	/**
	 * getFileName
	 * 
	 * @return
	 */
	public String getFilename() {
		return uploadForm.getFileName();
	}
	
	/**
	 * @param filesToUpload
	 */
	public void enqueueFileToUpload(Collection<FileToUpload> filesToUpload) {
		this.filesToUpload.addAll(filesToUpload);
		if (actualFileToUpload == null) {
			uploadNewPendingFile();
		}
	}
	
	/**
	 * uploadPendingFile
	 */
	public void uploadNewPendingFile() {
		// Execute pending workflows
		if (actualFileToUpload!=null && actualFileToUpload.getWorkflow()!=null && actualFileToUpload.isLastToBeUploaded()) {
			uploadedWorkflowFiles.add(actualFileToUpload.clone()) ;
			executeWorkflow(actualFileToUpload.getWorkflowTaskId());
		}
		
		if (!filesToUpload.isEmpty()) {
			actualFileToUpload = filesToUpload.remove(0);
			uploadForm = new FileUploadForm(actualFileToUpload.getFileUpload(), FileToUpload.DEFAULT_SIZE); // Here always with default size
			uploadItem.hFileUpload.clear(); // removes all previous fileUpload widgets
			uploadItem.hFileUpload.add(uploadForm);
			setPath(actualFileToUpload.getPath());
			setAction(actualFileToUpload.getAction());
			setRename(actualFileToUpload.getDesiredDocumentName());
			addSubmitCompleteHandler();
			// Case fileupload is workflow notify to users must be disabled
			Main.get().fileUpload.showPopup(actualFileToUpload.isEnableAddButton(), actualFileToUpload.isEnableImport(),
											(actualFileToUpload.getWorkflow()==null));
			if (actualFileToUpload.getWorkflow()!=null) {
				Main.get().fileUpload.executeSend();
			}
		} else {
			if (actualFileToUpload!=null && actualFileToUpload.getWorkflow()!=null) {
				Main.get().fileUpload.executeCancel();
			} 
			actualFileToUpload = null;
		} 
	}
	
	/**
	 * executeWorkflow
	 * 
	 * @param taskId
	 */
	private void executeWorkflow(double taskId) {
		List<FileToUpload> uploadedFiles = new ArrayList<FileToUpload>();
		for (FileToUpload uploaded :uploadedWorkflowFiles) {
			if (uploaded.getWorkflowTaskId()==taskId) {
				uploadedFiles.add(uploaded);
			}
		}
		actualFileToUpload.getWorkflow().setTaskInstanceValues(actualFileToUpload.getWorkflowTaskId(), 
															   actualFileToUpload.getWorkflowTransition(),
															   uploadedFiles);
	}
	
	/**
	 * cancel
	 */
	public void cancel() {
		actualFileToUpload = null;
		filesToUpload = new ArrayList<FileToUpload>();
	}
	
	/**
	 * addSubmitCompleteHandler
	 */
	private void addSubmitCompleteHandler() {
		// Add an event handler to the form.
		uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// Fire an onChange Event
				fireChange();
				// Cancel all timers to be absolutely sure nothing is going on.
				p.cancel();
				// Ensure that the form encoding is set correctly.
				uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
				// Check the result to see if an OK message is returned from the
				// server.
				
				// Return params could be <pre> or <pre style=""> with some IE
				// and chrome
				String msg = event.getResults();
				
				if (msg.contains(returnOKMessage)) {
					String docPath = "";
					if (msg.indexOf("path[") > 0 && msg.indexOf("]path") > 0) {
						docPath = msg.substring(msg.indexOf("path[") + 5, msg.indexOf("]path"));
						docPath = URL.decodeQueryString(docPath);
					}
					
					// Normal case document uploaded is not a workflow
					if (actualFileToUpload.getWorkflow()==null) {
						// Case is not importing a zip and wizard is enabled
						if (!uploadForm.isImportZip()
							 && action == UIFileUploadConstants.ACTION_INSERT
							 && (Main.get().workspaceUserProperties.getWorkspace().isWizardPropertyGroups()
								 || Main.get().workspaceUserProperties.getWorkspace().isWizardWorkflows()
								 || Main.get().workspaceUserProperties.getWorkspace().isWizardCategories() 
								 || Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords())) {
							
							wizard = true;
						} else {
							// wizard only it'll be enable in case digital signature
							// be true
							wizard = uploadForm.isDigitalSignature();
						}
						
						if (wizard) {
							Main.get().wizardPopup.start(docPath);
						}
						
						// By default selected row after uploading is uploaded file
						if (!docPath.equals("")) {
							Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRowByPath(docPath);
						}
						uploadItem.setLoaded();
					} else {
						actualFileToUpload.setDocumentPath(docPath);
						repositoryService.getUUIDByPath(docPath, new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								actualFileToUpload.setDocumentUUID(result);
								uploadItem.setLoaded();
							}
							@Override
							public void onFailure(Throwable caught) {
								Main.get().showError("getUUIDByPath", caught);
							}
						});
					}
				} else {
					uploadItem.setFailed(msg);
				}
			}
		});
	}
}