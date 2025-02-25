package com.annular.SchoolYogaBackends.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.annular.SchoolYogaBackends.service.serviceImpl.UserDetailsServiceImpl;



public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    	try {
    	    String jwt = parseJwt(request);
    	    logger.info("JWT from request: " + jwt);

    	    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
    	        logger.info("JWT available...");

    	        // Fetch username first
    	        String userName = jwtUtils.getDataFromJwtToken(jwt, "userName");

    	        // If username is null, fallback to emailId
    	        if (userName == null || userName.isEmpty()) {
    	            userName = jwtUtils.getDataFromJwtToken(jwt, "userEmailId");
    	            logger.info("Username is missing, using EmailId instead: " + userName);
    	        }

    	        if (userName != null && !userName.isEmpty()) {
    	            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
    	            UsernamePasswordAuthenticationToken authentication =
    	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    	            SecurityContextHolder.getContext().setAuthentication(authentication);
    	            logger.info("Authentication successful for: " + userName);
    	        } else {
    	            logger.warn("No valid identifier found in JWT.");
    	        }
    	    } else {
    	        logger.info("JWT not available or invalid...");
    	    }
    	} catch (Exception e) {
    	    logger.error("Cannot set user authentication...", e);
    	}

    	filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
