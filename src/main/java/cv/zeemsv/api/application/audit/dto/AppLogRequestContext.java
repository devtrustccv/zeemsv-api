package cv.zeemsv.api.application.audit.dto;

import jakarta.servlet.http.HttpServletRequest;

/** Copies request values while the servlet request is still available. */
public record AppLogRequestContext(
    String method,
    String path,
    String queryString,
    String remoteAddr,
    String userAgent
) {
    public static AppLogRequestContext capture(HttpServletRequest request) {
        return request == null ? new AppLogRequestContext(null, null, null, null, null)
            : new AppLogRequestContext(request.getMethod(), request.getRequestURI(), request.getQueryString(),
                request.getRemoteAddr(), request.getHeader("User-Agent"));
    }
}
