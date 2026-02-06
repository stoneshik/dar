package com.main.security;

import java.io.IOException;

import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorizeFilter extends HttpFilter {
    private final AuthorizeHandler authorizeHandler;

    private boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher("/css/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/img/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/js/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/static/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/manifest.json"),
            PathPatternRequestMatcher.withDefaults().matcher("/favicon.ico"),
            PathPatternRequestMatcher.withDefaults().matcher("/index.html"),
            PathPatternRequestMatcher.withDefaults().matcher("/"),
            PathPatternRequestMatcher.withDefaults().matcher("/login"),
            PathPatternRequestMatcher.withDefaults().matcher("/register"),
            PathPatternRequestMatcher.withDefaults().matcher("/map_without_login"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/v1/open/{*path}")
        );
        return matcher.matches(request);
    }

    @Override
    protected void doFilter(
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse,
        FilterChain filterChain
    ) throws IOException, ServletException {
        if (shouldNotFilter(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        if (!authorizeHandler.isAuthorized(httpServletRequest)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
