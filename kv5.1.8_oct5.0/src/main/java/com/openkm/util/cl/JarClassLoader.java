package com.openkm.util.cl;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarClassLoader extends URLClassLoader implements MultipleClassLoader {
	private static Logger log = LoggerFactory.getLogger(JarClassLoader.class);
	private URL url;
	
	public JarClassLoader(URL url) {
		super(new URL[] { url });
		this.url = url;
	}
	
	public JarClassLoader(URL url, ClassLoader parent) {
		super(new URL[] { url }, parent);
		this.url = url;
	}
	
	@Override
	public String getMainClassName() throws IOException {
		log.debug("getMainClassName()");
		URL u = new URL("jar", "", url + "!/");
		JarURLConnection uc = (JarURLConnection) u.openConnection();
		Attributes attr = uc.getMainAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}
}
