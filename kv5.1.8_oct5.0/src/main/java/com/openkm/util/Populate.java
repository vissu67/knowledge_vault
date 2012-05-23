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

package com.openkm.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;

public class Populate {
	private static Logger log = LoggerFactory.getLogger(Populate.class);
	public static final List<String> DEFAULT_TYPES = Arrays.asList(new String[] { "pdf", "rtf", "doc", "ppt",
			"xls" });

	public static void massiveImport(final String token, final String seedWord, final int numDocs,
			final List<String> fileTypes, Writer out) throws Exception {
		final OKMRepository okmRepository = OKMRepository.getInstance();
		final OKMDocument okmDocument = OKMDocument.getInstance();
		final OKMFolder okmFolder = OKMFolder.getInstance();

		int n = 0;
		for (int typeIdx = 0; typeIdx < fileTypes.size(); typeIdx++) {
			String type = (String) fileTypes.get(typeIdx);
			int offset = 0;
			while (n < numDocs * (typeIdx + 1) / fileTypes.size()) {
				final URL[] urls = new Populate.Search(type, seedWord, offset).getURLs();
				if (urls.length == 0) {
					break;
				}
				for (int i = 0; i < urls.length; i++) {
					final URL currentURL = urls[i];
					String urlPath = currentURL.getPath();
					if (urlPath.startsWith("/")) {
						urlPath = urlPath.substring(1);
					}
					final String host = urls[i].getHost();
					List<String> folderNames = new ArrayList<String>();
					folderNames.addAll(Arrays.asList(host.split("\\.")));
					Collections.reverse(folderNames);
					folderNames.addAll(Arrays.asList(urlPath.split("/", 0)));
					final String fileName = URLDecoder.decode(
							(String) folderNames.remove(folderNames.size() - 1), "UTF-8")
							.replaceAll(":", "_");
					String path = okmRepository.getRootFolder(null).getPath();

					for (Iterator<String> fn = folderNames.iterator(); fn.hasNext();) {
						String name = URLDecoder.decode((String) fn.next(), "UTF-8");
						name = name.replaceAll(":", "_");
						if (name.length() == 0) {
							continue;
						}
						
						path = path + "/" + name;
						if (!okmRepository.hasNode(null, path)) {
							//log.info("Create folder: {}", path);
							Folder fld = new Folder();
							fld.setPath(path);
							okmFolder.create(null, fld);
						}
					}
					
					path = path + "/" + fileName;
					if (!okmRepository.hasNode(null, path)) {
						final Writer fOut = out;
						final String docPath = path; 
						final Exception[] ex = new Exception[1];
						final int nDoc = n;
						
						Thread t = new Thread(new Runnable() {
							public void run() {
								try {
									//String info = fileName + " (" + host + ")";
									URLConnection con = currentURL.openConnection();
									InputStream in = con.getInputStream();
									try {
										synchronized (fOut) {
											fOut.write("<tr class=\""+(nDoc%2==0?"odd":"even")+"\">");
											fOut.write("<td>"+nDoc+"</td>");
											fOut.write("<td>"+Text.encodeIllegalXMLCharacters(currentURL.toString())+"</td>");
											fOut.flush();
										}
										int length = con.getContentLength();
										if (length != -1) {
											// in = new ProgressInputStream(in,
											// length, info, "dp", fOut);
										}

										log.info("Create document: {}", docPath);
										Document doc = new Document();
										doc.setPath(docPath);
										okmDocument.create(null, doc, in);
									} finally {
										in.close();
									}
								} catch (Exception e) {
									ex[0] = e;
								}
							}
						});
						t.start();
						for (int s = 0; t.isAlive(); s++) {
							Thread.sleep(100);
							if (s % 10 == 0) {
								synchronized (fOut) {
									//fOut.write("pb.inform(" + n + ", '')");
									//fOut.flush();
								}
							}
						}
						if (ex[0] == null) {
							n++;
							synchronized (fOut) {
								fOut.write("<td>Ok</td></tr>");
								fOut.flush();
							}
							if (n >= numDocs * (typeIdx + 1) / fileTypes.size()) {
								break;
							}
						} else {
							fOut.write("<td>Error</td></tr>");
							fOut.flush();
						}
					}
				}
				
				offset += 10;
			}
		}
	}

	static class Search {
		private final String filetype;
		private final String term;
		private final int start;

		public Search(String filetype, String term, int start) {
			this.filetype = filetype;
			this.term = term;
			this.start = start;
		}

		public URL[] getURLs() throws Exception {
			List<URL> urls = new ArrayList<URL>();
			String query = term + " filetype:" + filetype;
			URL google = new URL("http://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8")
					+ "&start=" + start);
			URLConnection con = google.openConnection();
			con.setRequestProperty("User-Agent", "");
			InputStream in = con.getInputStream();

			try {
				HTMLEditorKit kit = new HTMLEditorKit();
				HTMLDocument doc = new HTMLDocument();
				doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
				kit.read(new InputStreamReader(in, "UTF-8"), doc, 0);
				HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);

				while (it.isValid()) {
					AttributeSet attr = it.getAttributes();
					if (attr != null) {
						String href = (String) attr.getAttribute(HTML.Attribute.HREF);
						if (href != null && href.endsWith("." + filetype)) {
							URL url = new URL(new URL("http", "www.google.com", "dummy"), href);
							if (url.getHost().indexOf("google") == -1) {
								urls.add(url);
							}
						}
					}
					it.next();
				}
			} finally {
				in.close();
			}

			return (URL[]) urls.toArray(new URL[urls.size()]);
		}
	}
}
