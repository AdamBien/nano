package com.airhacks.nano;

import java.io.InputStream;

/**
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface NanoRequest {

    int process(String method, InputStream request, ResponseWriter response);
}
