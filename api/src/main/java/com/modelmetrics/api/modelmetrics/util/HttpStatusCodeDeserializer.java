package com.modelmetrics.api.modelmetrics.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/** HttpStatusCodeDeserializer. */
public class HttpStatusCodeDeserializer extends StdDeserializer<HttpStatusCode> {

  public HttpStatusCodeDeserializer() {
    this(null);
  }

  public HttpStatusCodeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public HttpStatusCode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    String statusCode = jp.getText();
    return HttpStatus.valueOf(statusCode);
  }
}
