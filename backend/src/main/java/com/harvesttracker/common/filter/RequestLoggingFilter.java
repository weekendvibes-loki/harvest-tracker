package com.harvesttracker.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Skip detailed logging for static resources / favicon
        if (uri.equals("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

            if (status >= 500) {
                log.error("HTTP {} {}{} -> Status {} in {} ms [CorrelationID: {}]",
                        method, uri, queryString, status, duration, correlationId);
            } else if (status >= 400) {
                log.warn("HTTP {} {}{} -> Status {} in {} ms [CorrelationID: {}]",
                        method, uri, queryString, status, duration, correlationId);
            } else {
                log.info("HTTP {} {}{} -> Status {} in {} ms [CorrelationID: {}]",
                        method, uri, queryString, status, duration, correlationId);
            }
        }
    }
}
