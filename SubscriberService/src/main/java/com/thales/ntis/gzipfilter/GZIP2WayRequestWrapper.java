/*  Copyright (c) 2008-2011 predic8 GmbH
 Use of this software is free of charge for both personal and commercial purposes.
 predic8 HEREBY DISCLAIMS ANY AND ALL WARRANTIES, EXPRESS OR IMPLIED, RELATIVE TO THE SOFTWARE, 
 INCLUDING BUT NOT LIMITED TO ANY WARRANTY OF FITNESS FOR A PARTICULAR PURPOSE OR MERCHANTIBILITY. 
 predic8 SHALL NOT BE LIABLE OR RESPONSIBLE FOR ANY DAMAGES, INJURIES OR LIABILITIES CAUSED DIRECTLY 
 OR INDIRECTLY FROM THE USE OF THE SOFTWARE, INCLUDING BUT NOT LIMITED TO INCIDENTAL, CONSEQUENTIAL 
 OR SPECIAL DAMAGES.
 */

package com.thales.ntis.gzipfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class GZIP2WayRequestWrapper extends HttpServletRequestWrapper {

    private ServletInputStream inStream = null;
    private BufferedReader reader = null;

    public GZIP2WayRequestWrapper(HttpServletRequest req) throws IOException {
        super(req);

        this.inStream = new GZIP2WayRequestStream(req);
        this.reader = new BufferedReader(new InputStreamReader(this.inStream));
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.inStream;
    }

    public BufferedReader getReader() throws IOException {
        return this.reader;
    }
}