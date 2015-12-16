function process(method, requestHeaders, responseHeaders, request, response) {
    print(requestHeaders.values());
    responseHeaders.add("hey", "ho");

    print(request);
    response.write(method + "echo: " + request);
    return 200;
}

