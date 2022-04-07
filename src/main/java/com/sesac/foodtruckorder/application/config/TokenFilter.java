package com.sesac.foodtruckorder.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
public class TokenFilter extends GenericFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String header = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        log.info("요청 헤더 값 : " + header);

        chain.doFilter(request, response);
    }
}
