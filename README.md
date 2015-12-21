# nano
Micro Java 8 HTTP Server embracing the "Convention over Configuration" idea.

Startup time: < 500ms

Jar-size: 8.4 kB

## Start

java -jar nano.jar [HANDLER_ROOT_FOLDER] [PORT_NUMBER]

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

## Usage

Http handlers are implemented in JavaScript and auto-discovered.

The folder structure is automatically translated into a context path:
```
root/developers/java/duke.js

java -jar nano.jar root 4242
```

The HTTP handler duke.js:

```javascript
function process(method, requestHeaders, responseHeaders, request, response) {
    response.write("Mighty duke!");
    return 200;
}
```

Becomes available under: http://localhost:4242/developers/java/duke:

```
curl -i http://localhost:4242/developers/java/duke

HTTP/1.1 200 OK
Date: Mon, 21 Dec 2015 04:16:46 GMT
Content-length: 12

Mighty duke
```



