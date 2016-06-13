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
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class GZIP2WayRequestStream extends ServletInputStream {

    private ServletInputStream inStream = null;
    private GZIPInputStream in = null;

    public GZIP2WayRequestStream(HttpServletRequest request) throws IOException {
        this.inStream = request.getInputStream();
        this.in = new GZIPInputStream(this.inStream);
    }

    public int read() throws IOException {
        return this.in.read();
    }

    public int read(byte[] b) throws IOException {
        return this.in.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return this.in.read(b, off, len);
    }

    public void close() throws IOException {
        this.in.close();
    }
}