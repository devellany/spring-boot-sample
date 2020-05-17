package com.devellany.sample.common.infra.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler {

    @ExceptionHandler(value = BadCredentialsException.class)
    public ModelAndView badCredentialsException(BadCredentialsException e) {
        return new ModelAndView("redirect:/account/email/confirm", HttpStatus.MOVED_PERMANENTLY);
    }
}
