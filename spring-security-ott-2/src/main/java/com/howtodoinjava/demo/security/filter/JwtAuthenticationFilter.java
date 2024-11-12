package com.howtodoinjava.demo.security.filter;

import com.howtodoinjava.demo.security.CustomUserDetailService;
import com.howtodoinjava.demo.security.utility.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final CustomUserDetailService customUserDetailService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain)
    throws ServletException, IOException {

    final var authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader != null) {
      if (authorizationHeader.startsWith("Bearer ")) {
        final var token = authorizationHeader.substring(7);
        final var userId = jwtUtils.extractUserId(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

          UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());

          if (jwtUtils.validateToken(token, userDetails)) {

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
              .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
              .setAuthentication(usernamePasswordAuthenticationToken);
          }
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}