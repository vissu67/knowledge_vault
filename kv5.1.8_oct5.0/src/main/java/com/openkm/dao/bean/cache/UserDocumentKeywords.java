package com.openkm.dao.bean.cache;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserDocumentKeywords implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String user;
	private String document;
	private Set<String> keywords = new HashSet<String>();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getDocument() {
		return document;
	}
	
	public void setDocument(String document) {
		this.document = document;
	}

	public Set<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", user="); sb.append(user);
		sb.append(", document="); sb.append(document);
		sb.append(", keywords="); sb.append(keywords);
		sb.append("}");
		return sb.toString();
	}
}
