package yakovenkodenis.com.presencecheck;


import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    public WebServer() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String answer = "<html><body><h1>Hello World!</h1></body></html>";

        return new Response(answer);
    }
}
