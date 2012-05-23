package com.openkm.module.base;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.mail.MessagingException;

import org.apache.commons.httpclient.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Notification;
import com.openkm.core.Config;
import com.openkm.dao.TwitterAccountDAO;
import com.openkm.dao.bean.TwitterAccount;
import com.openkm.module.direct.DirectAuthModule;
import com.openkm.util.MailUtils;
import com.openkm.util.TemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class BaseNotificationModule {
	private static Logger log = LoggerFactory.getLogger(BaseNotificationModule.class);
	
	/**
	 * Check for user subscriptions and send an notification
	 * 
	 * @param node Node modified (Document or Folder)
	 * @param user User who generated the modification event
	 * @param eventType Type of modification event
	 */
	public static void checkSubscriptions(Node node, String user, String eventType, String comment) {
		log.debug("checkSubscriptions({}, {}, {}, {})", new Object[] { node, user, eventType, comment });
		List<String> users = new ArrayList<String>();
		List<String> mails = new ArrayList<String>();
		
		try {
			users = checkSubscriptionsHelper(node);
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
		
		/**
		 * Mail notification
		 */
		try {
			for (String userId : users) {
				String mail = new DirectAuthModule().getMail(null, userId);
				
				if (mail != null && !mail.isEmpty()) {
					mails.add(mail);
				}
			}
				
			if (!mails.isEmpty()) {
				if (comment == null) { comment = ""; }
				StringWriter swSubject = new StringWriter();
				StringWriter swBody = new StringWriter();
				Configuration cfg = TemplateUtils.getConfig();
				
				Map<String, String> model = new HashMap<String, String>();
				model.put("documentUrl", Config.APPLICATION_URL+"?docPath=" + URLEncoder.encode(node.getPath(), "UTF-8"));
				
				
				//vissu touch on oct'9th
				String kvPath = node.getPath();
				kvPath = kvPath.replaceAll("okm:root","Knowledge Vault");
				//model.put("documentPath", node.getPath());
				model.put("documentPath", kvPath);
				
				model.put("documentName", node.getName());
				model.put("userId", user);
				model.put("eventType", eventType);
				model.put("subscriptionComment", comment);
				
				if (TemplateUtils.templateExists(Config.SUBSCRIPTION_MESSAGE_SUBJECT)) {
					Template tpl = cfg.getTemplate(Config.SUBSCRIPTION_MESSAGE_SUBJECT);
					tpl.process(model, swSubject);
				} else {
					StringReader sr = new StringReader(Config.SUBSCRIPTION_MESSAGE_SUBJECT);
					Template tpl = new Template("SubscriptionMessageSubject", sr, cfg);
					tpl.process(model, swSubject);
					sr.close();
				}
				
				if (TemplateUtils.templateExists(Config.SUBSCRIPTION_MESSAGE_BODY)) {
					Template tpl = cfg.getTemplate(Config.SUBSCRIPTION_MESSAGE_BODY);
					tpl.process(model, swBody);
				} else {
					StringReader sr = new StringReader(Config.SUBSCRIPTION_MESSAGE_BODY);
					Template tpl = new Template("SubscriptionMessageBody", sr, cfg);
					tpl.process(model, swBody);
					sr.close();
				}
				//added by vissu nov 20 for filter subscription emails
				if(eventType=="CHECKIN_DOCUMENT" || eventType=="CREATE_DOCUMENT" )
				MailUtils.sendMessage(mails, swSubject.toString(), swBody.toString());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (ValueFormatException e) {
			e.printStackTrace();
		} catch (javax.jcr.PathNotFoundException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * Twitter notification
		 */
		try {
			if (users != null && !users.isEmpty() && !Config.SUBSCRIPTION_TWITTER_USER.equals("") && !Config.SUBSCRIPTION_TWITTER_PASSWORD.equals("")) {
				Twitter twitter = new Twitter(Config.SUBSCRIPTION_TWITTER_USER, Config.SUBSCRIPTION_TWITTER_PASSWORD);
				StringWriter swStatus = new StringWriter();
				Configuration cfg = TemplateUtils.getConfig();
				
				Map<String, String> model = new HashMap<String, String>();
				model.put("documentUrl", MailUtils.getTinyUrl(Config.APPLICATION_URL+"?docPath="+node.getPath()));
				model.put("documentPath", node.getPath());
				model.put("documentName", node.getName());
				model.put("userId", user);
				model.put("eventType", eventType);
				model.put("subscriptionComment", comment);

				if (TemplateUtils.templateExists(Config.SUBSCRIPTION_TWITTER_STATUS)) {
					Template tpl = cfg.getTemplate(Config.SUBSCRIPTION_TWITTER_STATUS);
					tpl.process(model, swStatus);
				} else {
					StringReader sr = new StringReader(Config.SUBSCRIPTION_TWITTER_STATUS);
					Template tpl = new Template("SubscriptionTwitterStatus", sr, cfg);
					tpl.process(model, swStatus);
					sr.close();	
				}
				
				for (Iterator<String> itUsers = users.iterator(); itUsers.hasNext(); ) {
					String itUser = itUsers.next();
					Collection<TwitterAccount> twitterAccounts = TwitterAccountDAO.findByUser(itUser, true);
					
					for (Iterator<TwitterAccount> itTwitter = twitterAccounts.iterator(); itTwitter.hasNext(); ) {
						TwitterAccount ta = itTwitter.next();
						log.info("Twitter Notify from {} to {} ({}) - {}", new Object[] { twitter.getUserId(), ta.getTwitterUser(), itUser, swStatus.toString() });
						twitter.sendDirectMessage(ta.getTwitterUser(), swStatus.toString());
					}
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("checkSubscriptions: void");
	}
	
	/**
	 * Check for subscriptions recursively
	 */
	private static List<String> checkSubscriptionsHelper(Node node) throws 
			javax.jcr.RepositoryException {
		log.debug("checkSubscriptionsHelper: {}", node.getPath());
		ArrayList<String> al = new ArrayList<String>();
		
		if (node.isNodeType(Folder.TYPE) || node.isNodeType(Document.TYPE)) {
			if (node.isNodeType(Notification.TYPE)) {
				Value[] subscriptors = node.getProperty(Notification.SUBSCRIPTORS).getValues();
			
				for (int i=0; i<subscriptors.length; i++) {
					al.add(subscriptors[i].getString());
				}
			}
			
			// An user shouldn't be notified twice
			List<String> tmp = checkSubscriptionsHelper(node.getParent());
			for (Iterator<String> it = tmp.iterator(); it.hasNext(); ) {
				String usr = it.next();
				if (!al.contains(usr)) {
					al.add(usr);
				}
			}
		}
		
		return al;
	}
}
