package io.nflow.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;

import io.nflow.tests.runner.NflowServerRule;

@FixMethodOrder(NAME_ASCENDING)
public class MetricsAdminServletTest extends AbstractNflowTest {

  @ClassRule
  public static NflowServerRule server = new NflowServerRule.Builder()
          .prop("nflow.executor.timeout.seconds", 1)
          .prop("nflow.executor.keepalive.seconds", 5)
          .prop("nflow.dispatcher.await.termination.seconds", 1)
          .prop("nflow.db.h2.url", "jdbc:h2:mem:executorrecoverytest;TRACE_LEVEL_FILE=4;DB_CLOSE_DELAY=-1")
          .build();

  public MetricsAdminServletTest() {
    super(server);
  }

  @Test
  public void t01_canFetchMetrics() {
    URI uri = URI.create("http://localhost:" + server.getPort() + "/metrics/metrics");
    makeRequest(uri);
  }

  @Test
  public void t02_canFetchHealthChecks() {
    URI uri = URI.create("http://localhost:" + server.getPort() + "/metrics/healthcheck");
    makeRequest(uri);
  }

  private void makeRequest(URI uri) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(uri);
    Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
    assertEquals(200, response.getStatus());
  }
}
