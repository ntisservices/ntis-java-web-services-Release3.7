/*  Copyright (c) 2008-2011 predic8 GmbH
 Use of this software is free of charge for both personal and commercial purposes.
 predic8 HEREBY DISCLAIMS ANY AND ALL WARRANTIES, EXPRESS OR IMPLIED, RELATIVE TO THE SOFTWARE, 
 INCLUDING BUT NOT LIMITED TO ANY WARRANTY OF FITNESS FOR A PARTICULAR PURPOSE OR MERCHANTIBILITY. 
 predic8 SHALL NOT BE LIABLE OR RESPONSIBLE FOR ANY DAMAGES, INJURIES OR LIABILITIES CAUSED DIRECTLY 
 OR INDIRECTLY FROM THE USE OF THE SOFTWARE, INCLUDING BUT NOT LIMITED TO INCIDENTAL, CONSEQUENTIAL 
 OR SPECIAL DAMAGES.
 */

package com.thales.ntis.gzipfilter;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class GZIP2WayResponseStream extends ServletOutputStream {
    private HttpServletResponse response = null;
    private ServletOutputStream outStream;
    private GZIPOutputStream out;

    public GZIP2WayResponseStream(HttpServletResponse response)
            throws IOException {
        this.response = response;
        this.outStream = response.getOutputStream();
        this.out = new GZIPOutputStream(this.outStream);
        this.response.addHeader("Content-Encoding", "gzip");
    }

    public void write(int b) throws IOException {
        this.out.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.out.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
    }

    public void close() throws IOException {
        finish();
        this.out.close();
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void finish() throws IOException {
        this.out.finish();
    }
}