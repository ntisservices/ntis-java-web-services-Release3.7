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
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZIP2WayResponseWrapper extends HttpServletResponseWrapper {
    private HttpServletResponse response = null;
    private GZIP2WayResponseStream outStream = null;
    private PrintWriter writer = null;

    public GZIP2WayResponseWrapper(HttpServletResponse response) {
        super(response);

        this.response = response;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.outStream == null) {
            this.outStream = new GZIP2WayResponseStream(this.response);
        }

        return this.outStream;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new PrintWriter(getOutputStream());
        }
        return this.writer;
    }
}