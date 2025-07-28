package org.example.lmsbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.lmsbackend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 JWT Filter - Auth header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ JWT Filter - No valid auth header");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        String role = jwtTokenUtil.extractRole(token);
        Integer userId = jwtTokenUtil.extractUserId(token);
        
        System.out.println("🔍 JWT Filter - Extracted username: " + username);
        System.out.println("🔍 JWT Filter - Extracted role: " + role);
        System.out.println("🔍 JWT Filter - Extracted userId: " + userId);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(role)  // ✅ Không thêm "ROLE_" vì đã có trong token
            );
            
            System.out.println("🔍 JWT Filter - Created authorities: " + authorities);

            CustomUserDetails customUserDetails = new CustomUserDetails(
                    userId,
                    username,
                    null,
                    authorities
            );

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✅ JWT Filter - Authentication set successfully");
        }

        filterChain.doFilter(request, response);
    }
}
