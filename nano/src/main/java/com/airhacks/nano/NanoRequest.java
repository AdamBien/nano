package com.airhacks.nano;

import com.sun.net.httpserver.Headers;

/**
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface NanoRequest {

    int process(String method, Headers requestHeaders, Headers responseHeaders, String request, ResponseWriter response);
}
