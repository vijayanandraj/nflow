package com.nitorcreations.nflow.springboot.configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;
import static java.util.Collections.list;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SECURITY;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyConfigurer implements JettyServerCustomizer {
  private static final Logger logger = LoggerFactory.getLogger(JettyConfigurer.class);
  private final Environment env;
  public JettyConfigurer(Environment env) {
    this.env = env;
  }
  @Override
  public void customize(Server server) {
    WebAppContext webAppContext = (WebAppContext) server.getHandler();
    setupHandlers(server, webAppContext);
    try {
      setupServletContextHandler(webAppContext, new String[]{});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // TODO configure jetty
    // - request log
    // - jmx
    // - see StartNflow for more stuff
  }

  private void setupServlet(ServletHandler servletHandler) {

  }


  private ServletContextHandler setupServletContextHandler(ServletContextHandler context, String[] extraStaticResources) throws IOException {

    // workaround for a jetty bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=364936
    Resource.setDefaultUseCaches(false);

    List<String> resources = new ArrayList<>();
    for (String path : extraStaticResources) {
      File f = new File(path);
      if (f.isDirectory()) {
        resources.add(f.getCanonicalFile().toURI().toURL().toString());
      }
    }

    // add all resource roots locations from classpath
    for (URL url : list(this.getClass().getClassLoader().getResources("static"))) {
      resources.add(url.toString());
    }

    logger.info("Static resources served from {}", resources);
    context.setBaseResource(new ResourceCollection(resources.toArray(new String[resources.size()])));
    context.setWelcomeFiles(new String[] { "index.html", "service.json" });

    ServletHolder holder = new ServletHolder(new DefaultServlet());
    holder.setInitParameter("dirAllowed", "false");
    holder.setInitParameter("gzip", "true");
    holder.setInitParameter("acceptRanges", "false");
    holder.setDisplayName("nflow-static");
    holder.setInitOrder(1);

    context.addServlet(holder, "/*");

    context.getMimeTypes().addMimeMapping("ttf", "application/font-sfnt");
    context.getMimeTypes().addMimeMapping("otf", "application/font-sfnt");
    context.getMimeTypes().addMimeMapping("woff", "application/font-woff");
    context.getMimeTypes().addMimeMapping("eot", "application/vnd.ms-fontobject");
    context.getMimeTypes().addMimeMapping("svg", "image/svg+xml");
    context.getMimeTypes().addMimeMapping("html", "text/html; charset=utf-8");
    context.getMimeTypes().addMimeMapping("css", "text/css; charset=utf-8");
    context.getMimeTypes().addMimeMapping("js", "application/javascript; charset=utf-8");
    return context;
  }

  private void setupHandlers(final Server server, final ServletContextHandler context) {
    HandlerCollection handlers = new HandlerCollection();
    server.setHandler(handlers);
    handlers.addHandler(context);
    handlers.addHandler(createAccessLogHandler());
  }

  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  private RequestLogHandler createAccessLogHandler() {
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    String directory = env.getProperty("nflow.jetty.accesslog.directory", "log");
    // TODO is this good in production? This creates only one level of missing directories
    new File(directory).mkdir();
    NCSARequestLog requestLog = new NCSARequestLog(Paths.get(directory, "yyyy_mm_dd.request.log").toString());
    requestLog.setRetainDays(90);
    requestLog.setAppend(true);
    requestLog.setLogDateFormat("yyyy-MM-dd:HH:mm:ss Z");
    requestLog.setExtended(true);
    requestLog.setLogTimeZone(TimeZone.getDefault().getID());
    requestLog.setPreferProxiedForAddress(true);
    requestLog.setLogLatency(true);
    requestLogHandler.setRequestLog(requestLog);
    return requestLogHandler;
  }

}
