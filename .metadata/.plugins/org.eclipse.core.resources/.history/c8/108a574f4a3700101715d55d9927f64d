package com.cts.inventorymanagement.gateway.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            log.info("Incoming request: {} {}", 
                exchange.getRequest().getMethod(), 
                exchange.getRequest().getURI());
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Outgoing response: {} | Path: {}",
                    exchange.getResponse().getStatusCode(),
                    exchange.getRequest().getPath().value());
            }));
        };
    }
}