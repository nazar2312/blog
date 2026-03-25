package com.portfolio.blog.security;

import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.services.JwtServiceInterface;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceInterface jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {

            String token = jwtService.extractToken(request);
            System.out.println("extracted");
            jwtService.isBlacklisted(token);
            System.out.println("not blacklisted");
            UserDetails userDetails = jwtService.validateToken(token);
            System.out.println("valid");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (JwtException | UnauthenticatedException ex) {
            //unauthorized
            log.warn("JWT authentication filter failed | Error message - {}", ex.getMessage());
            response.setStatus(401);
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Specifying endpoints that should not be filtered;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return
                path.startsWith("/api/registration") ||
                        path.startsWith("/api/auth") ||
                        request.getMethod().equals("GET") && path.startsWith("/api/posts") ||
                        request.getMethod().equals("GET") && path.startsWith("/api/categories") ||
                        request.getMethod().equals("GET") && path.startsWith("/api/tags");
    }


}
