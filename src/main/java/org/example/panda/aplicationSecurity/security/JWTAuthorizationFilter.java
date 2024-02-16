package org.example.panda.aplicationSecurity.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.panda.aplicationSecurity.services.IJWTUtilityService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    IJWTUtilityService jwtUtilityService;
    public JWTAuthorizationFilter(IJWTUtilityService jwtUtilityService) {
        this.jwtUtilityService=jwtUtilityService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //obtener el jwt a traves del header
        String header= request.getHeader("Authorization"); //el header tiene que ser el key y el value es el jwt
        if(header==null || !header.startsWith("Bearer ")){ //poner el espacio al final del bearer(IMPORTANTE)
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); //extraemos el bearer del token, son los primeros 7 caracteres

        try {
            JWTClaimsSet claims= jwtUtilityService.parseJWT(token);
            String userId = claims.getSubject();
            List<String> roles = claims.getStringListClaim("roles");
            Collection<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        filterChain.doFilter(request,response);
    }

}
