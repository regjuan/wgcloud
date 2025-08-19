package com.wgcloud.filter;


import com.wgcloud.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version v2.3
 * @ClassName:AuthRestFilter.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: http请求过滤器，拦截不是从路由过来的请求
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@WebFilter(filterName = "authRestFilter", urlPatterns = {"/api/*"})
public class AuthRestFilter implements Filter {

    static Logger log = LoggerFactory.getLogger(AuthRestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    private final String[] whitelist = {"/api/login"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        for (String path : whitelist) {
            if (servletPath.equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Token has expired\"}");
                return;
            } catch (Exception e) {
                log.error("Error parsing JWT token", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        }

        if (username != null && jwtUtil.validateToken(jwt, username)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Invalid or missing token for path: {}", servletPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
        }
    }
}