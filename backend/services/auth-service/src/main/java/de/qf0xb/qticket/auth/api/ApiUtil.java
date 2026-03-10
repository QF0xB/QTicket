package de.qf0xb.qticket.auth.api;

import org.springframework.web.context.request.NativeWebRequest;

public final class ApiUtil {
  private ApiUtil() {
  }

  public static void setExampleResponse(NativeWebRequest request, String contentType, String example) {
    // No-op for now. This is only used by generated default methods to serve examples.
    // You can implement real example rendering later if you want.
  }
}
