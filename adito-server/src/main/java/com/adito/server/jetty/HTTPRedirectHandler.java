package com.adito.server.jetty;

import com.adito.boot.ContextHolder;
import com.adito.boot.RequestHandler;
import com.adito.boot.RequestHandlerException;
import com.adito.boot.SystemProperties;
import java.io.IOException;
import java.util.ArrayList;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;

public class HTTPRedirectHandler implements HttpHandler {

    HttpContext context;

    static ArrayList<RequestHandler> handlers = new ArrayList<RequestHandler>();

    public static void registerHandler(RequestHandler handler) {
        handlers.add(handler);
    }

    public static void unregisterHandler(RequestHandler handler) {
        handlers.remove(handler);
    }


    public void initialize(HttpContext context) {
        this.context = context;
    }

    public boolean isStarted() {
        return true;
    }

    public void stop() {

    }

    public void start() {

    }

    public String getName() {
        return "SECURE";
    }

    public HttpContext getHttpContext() {
        return context;
    }

    public void handle(String pathInContext,
            String str,
            HttpRequest request,
            HttpResponse response) throws IOException {
        handle(pathInContext, request, response);
    }

    public void handle(String pathInContext,
            HttpRequest request,
            HttpResponse response) throws IOException {

        for (RequestHandler handler : handlers) {
            try {
                request.setCharacterEncoding(SystemProperties.get("adito.encoding", "UTF-8"), false);
                if (handler.handle(pathInContext, "", new RequestAdapter(request), new ResponseAdapter(response))) {
                    request.setHandled(true);
                    return;
                }
            } catch (RequestHandlerException e) {
                throw new HttpException(e.getCode(), e.getMessage());
            }

        }

        if (!request.isConfidential()) {
            int sslPort = ContextHolder.getContext().getPort();
            response.sendRedirect("https://" + request.getHost() + (sslPort > 0 && sslPort != 443 ? ":" + sslPort : "") + request.getEncodedPath());
            request.setHandled(true);
        }
    }
}
