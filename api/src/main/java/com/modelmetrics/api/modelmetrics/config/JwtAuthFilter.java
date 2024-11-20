package com.modelmetrics.api.modelmetrics.config;

import com.modelmetrics.api.modelmetrics.helper.UserDetailsServiceImpl;
import com.modelmetrics.api.modelmetrics.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/** JwtAuthFilter. */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  private final PathMatcher pathMatcher = new AntPathMatcher();

  @Value("${security.jwt.cookie-token-key}")
  private String tokenKey;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Verify If Authentication Is Needed
    // (Bypass Public Routes)
    String uri = request.getRequestURI();
    boolean isPublicRoute = false;

    for (String publicRoute : PublicRoutes.getWhiteList()) {
      if (pathMatcher.match(publicRoute, uri)) {
        isPublicRoute = true;
        break;
      }
    }

    if (isPublicRoute) {
      filterChain.doFilter(request, response);
      return;
    }

    // Begin Authentication Process
    // (Authenticate Private Routes)
    String token = null;
    String email = null;

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals(tokenKey)) {
          token = cookie.getValue();
        }
      }
    }

    try {
      if (token == null) {
        throw new AccessDeniedException("Token was not found.");
      }

      email = jwtService.extractEmail(token);

      if (email != null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtService.validateToken(token, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }
}
