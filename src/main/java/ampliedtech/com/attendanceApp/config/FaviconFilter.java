package ampliedtech.com.attendanceApp.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Returns 204 for GET /favicon.ico before the request reaches the resource handler,
 * so NoResourceFoundException is never thrown (avoids 500).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FaviconFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("GET".equalsIgnoreCase(request.getMethod()) && isFavicon(request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static boolean isFavicon(String uri) {
        if (uri == null) return false;
        return "favicon.ico".equals(uri) || "/favicon.ico".equals(uri) || uri.endsWith("/favicon.ico");
    }
}
