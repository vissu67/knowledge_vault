package com.openkm.module.direct;

import org.apache.http.entity.mime.content.InputStreamBody;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamKnownSizeBody extends InputStreamBody {
	private int length;

	public InputStreamKnownSizeBody(
			final InputStream in, final int length,
			final String mimeType, final String filename) {
		super(in, mimeType, filename);
		this.length = length;
	}

	@Override
	public long getContentLength() {
		return this.length;
	}
}