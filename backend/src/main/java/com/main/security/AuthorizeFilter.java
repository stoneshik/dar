package com.main.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorizeFilter extends HttpFilter {
    private final AuthorizeHandler authorizeHandler;

    private boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new OrRequestMatcher(
                //new AntPathRequestMatcher("/api/some/url", HttpMethod.GET.name()),
                //new AntPathRequestMatcher("/api/some/url", HttpMethod.GET.name())
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/img/**"),
                new AntPathRequestMatcher("/js/**"),
                new AntPathRequestMatcher("/static/**"),
                new AntPathRequestMatcher("/manifest.json"),
                new AntPathRequestMatcher("/favicon.ico"),
                new AntPathRequestMatcher("/index.html"),
                new AntPathRequestMatcher("/"),
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/register"),
                new AntPathRequestMatcher("/map_without_login"),
                new AntPathRequestMatcher("/api/open/**")
        );
        return matcher.matches(request);
    }

    @Override
    protected void doFilter(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain)
            throws IOException, ServletException {
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
