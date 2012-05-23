package com.openkm.util.cl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesystemClassLoader extends ClassLoader implements MultipleClassLoader {
	private static Logger log = LoggerFactory.getLogger(FilesystemClassLoader.class);
	private File file = null;
		
	public FilesystemClassLoader(File file) throws IOException {
		super();
		this.file = file;
		
	}
	public FilesystemClassLoader(File file, ClassLoader parent) throws IOException {
		super(parent);
		this.file = file;
	}
	
	/**
	 * Get main class name
	 */
	@Override
	public String getMainClassName() throws IOException {
		log.debug("getMainClassName()");
		File mf = new File(file, "META-INF/MANIFEST.MF");
		FileInputStream fis = null;
		
		try {
			if (mf.exists() && mf.canRead()) {
				fis = new FileInputStream(mf);
				Manifest manif = new Manifest(fis);
		        Attributes attr = manif.getMainAttributes();
				return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
		
		return null;
	}
	
	/**
	 * Find class
	 */
	@Override
	public Class<?> findClass(String className) {
		log.info("findClass({})", className);
		String classFile = className.replace('.', '/').concat(".class"); 
		File fc = new File(file, classFile);
		FileInputStream fis = null;
		
		// Check for system class
		try {
			return findSystemClass(className);
		} catch (ClassNotFoundException e) {
			// Ignore
		}
		
		try {
			if (fc.exists() && fc.canRead()) {
				fis = new FileInputStream(fc);
				byte[] classByte = IOUtils.toByteArray(fis);
				
				if (classByte != null) {
					return defineClass(className, classByte, 0, classByte.length, null);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		
		return null;
	}
	
	/**
	 * Get resource input stream
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		log.debug("getResourceAsStream({})", name);
		File fr = new File(file, name);
		
		try {
			if (fr.exists() && fr.canRead()) { 
				return new FileInputStream(fr);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
