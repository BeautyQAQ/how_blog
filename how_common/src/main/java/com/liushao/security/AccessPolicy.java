package com.liushao.security;

/** One method-aware policy for both Gateway and direct service requests. */
public final class AccessPolicy {
    private AccessPolicy() {}
    public enum Service { ARTICLE, USER, SEARCH }
    public enum Access { PUBLIC, USER, ADMIN }

    public static Access required(Service service, String method, String path) {
        if ("OPTIONS".equals(method)) return Access.PUBLIC;
        if (path.contains(";") || path.contains("%") || path.contains("\\") || path.contains("//")
                || path.matches(".*(?:^|/)\\.\\.?(?:/|$).*")) return Access.ADMIN;
        boolean read = "GET".equals(method) || "HEAD".equals(method);
        if (read && (path.equals("/v3/api-docs") || path.startsWith("/v3/api-docs/")
                || path.equals("/swagger-ui.html") || path.startsWith("/swagger-ui/")
                || path.startsWith("/webjars/"))) return Access.PUBLIC;
        if (service == Service.ARTICLE) {
            if (read && (path.matches("/article/public(?:/[^/;]+)?") || path.equals("/channel")
                    || path.matches("/comment/article/[^/;]+")
                    || path.matches("/media/[a-f0-9]{32}\\.(png|jpg)"))) return Access.PUBLIC;
            if ("POST".equals(method) && path.equals("/comment")) return Access.USER;
        }
        if (service == Service.USER && "POST".equals(method)
                && (path.equals("/admin/login") || path.equals("/user/login"))) return Access.PUBLIC;
        return Access.ADMIN;
    }

    public static Access gateway(String method, String path) {
        for (Service service : Service.values()) {
            String prefix = "/" + service.name().toLowerCase(java.util.Locale.ROOT);
            if (path.startsWith(prefix + "/")) return required(service, method, path.substring(prefix.length()));
        }
        return required(null, method, path);
    }
}
