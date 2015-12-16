function process(method, request, response) {
    print(request);
    response.write(method + "echo: " + request);
    return 200;
}

