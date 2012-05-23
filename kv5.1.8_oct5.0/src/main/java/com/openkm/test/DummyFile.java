package com.openkm.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyFile {
	private static Logger log = LoggerFactory.getLogger(DummyFile.class);
	private static final String FILE = "prueba.txt";
	
	public static void main(String[] args) throws IOException {
		System.out.println("** CHARACTER ENCODING: "+(new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding());
		System.out.println("** CHARACTER ENCODING: "+Charset.defaultCharset());
		System.out.println("** file.encoding: "+System.getProperty("file.encoding"));
		System.out.println("** sun.jnu.encoding: "+System.getProperty("sun.jnu.encoding"));
		write();
		read();
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void write() throws FileNotFoundException, IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FILE));
		bos.write("Esto es una coñó".getBytes());
		bos.close();
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void read() throws FileNotFoundException, IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FILE));
		byte[] buffer = new byte[24];
				
		while (bis.read(buffer) > 0) {
			log.info("** Contenido: "+new String(buffer));
        }
		
		bis.close();
	}
}
