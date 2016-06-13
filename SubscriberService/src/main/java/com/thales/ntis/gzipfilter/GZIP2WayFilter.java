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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GZIP2WayFilter implements Filter {
    public void init(FilterConfig arg0) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest)) {
            HttpServletRequest req = (HttpServletRequest) request;
            String contentEncoding = req.getHeader("Content-Encoding");
            if ((contentEncoding != null)
                    && (contentEncoding.toLowerCase().indexOf("gzip") > -1)) {
                request = new GZIP2WayRequestWrapper(
                        (HttpServletRequest) request);
            }
        }
        if ((response instanceof HttpServletResponse)) {
            HttpServletRequest req = (HttpServletRequest) request;
            String acceptEncoding = req.getHeader("Accept-Encoding");
            if ((acceptEncoding != null)
                    && (acceptEncoding.toLowerCase().indexOf("gzip") > -1)) {
                response = new GZIP2WayResponseWrapper(
                        (HttpServletResponse) response);
            }
        }
        chain.doFilter(request, response);
        if ((response instanceof GZIP2WayResponseWrapper))
            ((GZIP2WayResponseStream) response.getOutputStream()).finish();
    }

    public void destroy() {
    }
}