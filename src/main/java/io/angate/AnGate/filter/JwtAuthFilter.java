package io.angate.AnGate.filter;

import io.angate.AnGate.entity.Users;
import io.angate.AnGate.service.JwtService;
import io.angate.AnGate.service.UserDetailServiceImplem;
import io.angate.AnGate.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailServiceImplem userDetailServiceImplem;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String path = request.getRequestURI();
        if( bearerToken==null || !bearerToken.startsWith("Bearer ") ){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = bearerToken.substring(7);
        Claims claims = jwtService.extractClaim(jwt);
        String tokenType = claims.get("type",String.class);

        if (!"access".equals(tokenType)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtService.getUsernameFromToken(jwt);

        if(username!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailServiceImplem.loadUserByUsername(username);
           if(jwtService.isTokenValid(jwt,userDetails)){
               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
               authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        }
        filterChain.doFilter(request,response);
    }
}
