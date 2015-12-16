package com.airhacks.nano;

/**
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface NanoRequest {

    int process(String method, String request, ResponseWriter response);
}
