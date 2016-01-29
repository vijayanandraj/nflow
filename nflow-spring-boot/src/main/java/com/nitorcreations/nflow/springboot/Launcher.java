package com.nitorcreations.nflow.springboot;

import com.nitorcreations.nflow.springboot.configuration.JettyConfigurer;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;

// including @SpringBootApplication loads all kinds of crap
@SpringBootApplication(exclude = { DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class })
@Configuration
public class Launcher extends SpringBootServletInitializer {
  private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
  public static void main(String[] args) {
    SpringApplication.run(Launcher.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Launcher.class);
  }

  @Value("${nflow.test.name}")
  private String value;

  @Inject
  private Environment environment;

  @Bean
  public EmbeddedServletContainerFactory containerFactory() throws UnknownHostException {
    final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory() {
      @Override
      protected JettyEmbeddedServletContainer getJettyEmbeddedServletContainer(Server server) {
        return new JettyEmbeddedServletContainer(server);
      }
    };
    factory.setPort(7400);
    factory.setAddress(InetAddress.getByName("0.0.0.0"));
    factory.setDisplayName("nflow");
    factory.setRegisterDefaultServlet(true);
    factory.setRegisterJspServlet(false);
    factory.addServerCustomizers(new JettyConfigurer(environment));
    logger.info("NAME= {}", value);
    return factory;
  }

}
