package com.openkm.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.module.direct.DirectDocumentModule;

public class VirusDetection {
	private static Logger log = LoggerFactory.getLogger(DirectDocumentModule.class);
	
	/**
	 * @param content
	 * @param name
	 * @throws VirusDetectedException 
	 */
	public static void detect(File tmpFile) throws VirusDetectedException {
		try {
			// Performs virus check
			log.debug("CMD: "+Config.SYSTEM_ANTIVIR+" "+tmpFile.getPath());
			ProcessBuilder pb = new ProcessBuilder(Config.SYSTEM_ANTIVIR, "--no-summary", tmpFile.getPath());
			Process process = pb.start();
			process.waitFor();
			String info = IOUtils.toString(process.getInputStream());
			process.destroy();

			// Check return code
			if (process.exitValue() == 1) {
				log.warn(info);
				info = info.substring(info.indexOf(':')+1);
				throw new VirusDetectedException(info);
			}
		} catch (InterruptedException e) {
			log.warn("Failed to check for viruses", e);
		} catch (IOException e) {
			log.warn("Failed to check for viruses", e);
		}
	}
}
