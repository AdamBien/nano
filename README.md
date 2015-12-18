# nano
Micro Java 8 HTTP Server

## Start

java -jar nano.jar [ROOT_FOLDER] [PORT_NUMBER]

## Sample Request

```javascript
function process(method, requestHeaders, responseHeaders, request, response) {
    print(requestHeaders.values());
    responseHeaders.add("hey", "ho");

    print(request);
    response.write(method + "echo: " + request);
    return 200;
}
```
