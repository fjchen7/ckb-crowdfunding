package com.example.crowdfunding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Trace {
    private static final Logger log = LoggerFactory.getLogger(Trace.class);

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository() {
            @Override
            public void add(HttpTrace trace) {
                log.info("Incoming request: " + trace.getRequest().getMethod()
                        + " " + trace.getRequest().getUri()
                        + " from " + trace.getRequest().getRemoteAddress()
                        + ", status: " + trace.getResponse().getStatus());
                super.add(trace);
            }
        };
    }
}
