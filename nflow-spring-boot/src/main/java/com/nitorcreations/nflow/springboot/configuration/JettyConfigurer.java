package com.nitorcreations.nflow.springboot.configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static java.util.Arrays.asList;
import static java.util.Collections.list;

public class JettyConfigurer implements JettyServerCustomizer {
  private static final Logger logger = LoggerFactory.getLogger(JettyConfigurer.class);
  private final Environment env;

  public JettyConfigurer(Environment env) {
    this.env = env;
  }

  @Override
  public void customize(Server server) {
    WebAppContext webAppContext = (WebAppContext) server.getHandler();
    setupJmx(server, env);
    setupHandlers(server, webAppContext);
    try {
      setupServletContextHandler(webAppContext, env.getRequiredProperty("extra.resource.directories", String[].class));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void setupJmx(Server server, Environment env) {
    if (asList(env.getActiveProfiles()).contains("jmx")) {
      logger.info("Enable JMX for Jetty");
      MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
      server.addEventListener(mbContainer);
      server.addBean(mbContainer);
    }
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
    // TODO document configuration parameter
    Integer retainDays = env.getProperty("nflow.jetty.accesslog.retainDays", Integer.class, 90);
    requestLog.setRetainDays(retainDays);
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
