package br.com.filazero;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class StreamLambdaHandler implements RequestHandler<HttpApiV2ProxyRequest, AwsProxyResponse> {
  private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

  static {
    try {
      handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(FilaZeroApplication.class);
    } catch (ContainerInitializationException e) {
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }

  @Override
  public AwsProxyResponse handleRequest(HttpApiV2ProxyRequest input, Context context) {
    return handler.proxy(input, context);
  }
}

