package com.devtech.gestiondestock.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();
        String idEntreprise = MDC.get("idEntreprise");
        
        long startTime = System.currentTimeMillis();
        
        try {
            MDC.put("requestId", requestId);
            
            log.info("==> REQUEST [{}] {} {} | IP: {} | Entreprise: {}",
                    requestId, method, uri, remoteAddr, 
                    idEntreprise != null ? idEntreprise : "N/A");
            
            if (queryString != null) {
                log.debug("    Query params: {}", queryString);
            }
            
            chain.doFilter(request, response);
            
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = httpResponse.getStatus();
            
            log.info("<== RESPONSE [{}] {} {} | Status: {} | Duration: {}ms",
                    requestId, method, uri, status, duration);
            
            MDC.remove("requestId");
        }
    }
}
