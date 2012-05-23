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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;

public class ReaderInputStream extends InputStream {
	/** Input Reader class. */
	private Reader reader;
	private PipedOutputStream pos;
	private PipedInputStream pis;
	private OutputStreamWriter osw;

	/**
	 * Creates new input stream from the given reader. Uses the platform default
	 * encoding.
	 * 
	 * @param reader
	 *            Input reader
	 */
	public ReaderInputStream(Reader reader) throws IOException {
		this.reader = reader;
		pos = new PipedOutputStream();
		pis = new PipedInputStream(pos);
		osw = new OutputStreamWriter(pos);
	}

	/**
	 * Creates new input stream from the given reader and encoding.
	 * 
	 * @param reader
	 *            Input reader
	 * @param encoding
	 */
	public ReaderInputStream(Reader reader, String encoding) throws IOException {
		this.reader = reader;
		pos = new PipedOutputStream();
		pis = new PipedInputStream(pos);
		osw = new OutputStreamWriter(pos, encoding);
	}

	public int read() throws IOException {
		if (pis.available() > 0) {
			return pis.read();
		}

		int c = reader.read();

		if (c == -1) {
			return c;
		}

		osw.write(c);
		osw.flush();
		pos.flush();

		return pis.read();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (len == 0) {
			return 0;
		}

		int c = read();

		if (c == -1) {
			return -1;
		}

		b[off] = (byte) c;

		int i = 1;

		// Don't try to fill up the buffer if the reader is waiting.
		for (; (i < len) && reader.ready(); i++) {
			c = read();

			if (c == -1) {
				return i;
			}

			b[off + i] = (byte) c;
		}

		return i;
	}

	public int available() throws IOException {
		int i = pis.available();

		if (i > 0) {
			return i;
		}

		if (reader.ready()) {
			// Char must produce at least one byte.
			return 1;
		} else {
			return 0;
		}
	}

	public void close() throws IOException {
		reader.close();
		osw.close();
		pis.close();
	}
}