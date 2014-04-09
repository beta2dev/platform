package ru.beta2.platform.core.undercover;

import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import io.undertow.Undertow;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 17:06
 */
public class UndercoverServer implements Startable, UndercoverService
{

    private final Logger log = LoggerFactory.getLogger(UndercoverServer.class);

    private final Map<String, HessianSkeleton> skeletons = Collections.synchronizedMap(new HashMap<String, HessianSkeleton>());

    private final UndercoverConfig cfg;
    private final Undertow server;

    public UndercoverServer(UndercoverConfig cfg)
    {
        this.cfg = cfg;
        server = cfg.isEnabled()
                ? Undertow.builder()
                    .addHttpListener(cfg.getPort(), cfg.getHost())
                    .setHandler(new UndercoverHandler())
                    .build()
                : null;
    }

    @Override
    public HandlerRegistration registerCover(final String path, Class intf, Object impl)
    {
        log.debug("Register cover: path={}, intf={}, impl={}", path, intf, impl);
        skeletons.put(path, new HessianSkeleton(impl, intf));
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                log.debug("Unregister cover: path={}", path);
                skeletons.remove(path);
            }
        };
    }

    @Override
    public void start()
    {
        if (server != null) {
            log.trace("Starting UndercoverServer");
            server.start();
            log.info("UndercoverServer started");
        }
        else {
            log.info("UndercoverServer is disabled");
        }
    }

    @Override
    public void stop()
    {
        if (server != null) {
            log.trace("Stopping UndercoverServer");
            server.stop();
            log.info("UndercoverServer stopped");
        }
    }

    private class UndercoverHandler implements HttpHandler
    {

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception
        {
            String path = exchange.getRequestPath();
            log.trace("Handle request for path={}", path);

            HttpString method = exchange.getRequestMethod();

            if ("/".equals(path) && cfg.isCoversListEnabled()) {
                log.trace("Process root request, method is {}", method);
                if (Methods.GET.equals(method)) {
                    sendCoversList(exchange);
                }
                else if (Methods.HEAD.equals(method)) {
                    // do nothing
                }
                else {
                    sendMethodNotAllowed("GET,HEAD", exchange);
                }
                return;
            }

            log.trace("Process skeleton request, method is {}", method);
            HessianSkeleton skeleton = skeletons.get(path);
            if (skeleton != null) {
                if (Methods.POST.equals(exchange.getRequestMethod())) {
                    invokeSkeleton(skeleton, exchange);
                }
                else if (Methods.GET.equals(exchange.getRequestMethod()) && cfg.isCoverMetadataEnabled()) {
                    sendCoverMetadata(skeleton, exchange);
                }
                else if (Methods.HEAD.equals(exchange.getRequestMethod())) {
                    // do nothing
                }
                else {
                    sendMethodNotAllowed(cfg.isCoverMetadataEnabled() ? "POST,GET,HEAD" : "POST,HEAD", exchange);
                }
            }
            else {
                exchange.setResponseCode(StatusCodes.NOT_FOUND);
            }
        }
    }

    private void sendMethodNotAllowed( String allowedMethods, HttpServerExchange exchange)
    {
        exchange.setResponseCode(StatusCodes.METHOD_NOT_ALLOWED);
        exchange.getResponseHeaders().put(Headers.ALLOW, allowedMethods);
    }

    private void invokeSkeleton(HessianSkeleton skeleton, HttpServerExchange exchange) throws Exception
    {
        log.trace("Invoke skeleton: {}", skeleton.getAPIClassName());
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "x-application/hessian");
        exchange.startBlocking();
        try {
            skeleton.invoke(exchange.getInputStream(), exchange.getOutputStream());
        }
        catch (Exception e) {
            log.error("Error invoke skeleton", e);
            throw e;
        }

        exchange.endExchange();
    }

    private void sendCoverMetadata(HessianSkeleton skeleton, HttpServerExchange exchange)
    {
        log.trace("Send cover metadata");
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(skeleton.getAPIClassName());
        exchange.endExchange();
    }

    private void sendCoversList(HttpServerExchange exchange)
    {
        log.trace("Send covers list");
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        synchronized (skeletons) {
            boolean metadataEnabled = cfg.isCoverMetadataEnabled();
            StringBuilder b = new StringBuilder();
            for (Map.Entry<String, HessianSkeleton> e : skeletons.entrySet()) {
                log.debug("There is {}:{}", e.getKey(), e.getValue().getAPIClassName());
                if (metadataEnabled) {
                    b.append(e.getKey()).append(":").append(e.getValue().getAPIClassName()).append("\n");
                }
                else {
                    b.append(e.getKey()).append("\n");
                }
            }
            exchange.getResponseSender().send(b.toString());
        }
        exchange.endExchange();
    }
}
