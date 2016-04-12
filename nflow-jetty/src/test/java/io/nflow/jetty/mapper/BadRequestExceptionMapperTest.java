package io.nflow.jetty.mapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import io.nflow.jetty.mapper.BadRequestExceptionMapper;

public class BadRequestExceptionMapperTest {
  BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();

  @Test
  public void badRequestExceptionResultInStatusBadRequest() {
    Response response = mapper.toResponse(new BadRequestException());
    assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
  }
}
