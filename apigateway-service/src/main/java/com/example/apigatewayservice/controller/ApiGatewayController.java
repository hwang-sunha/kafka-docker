package com.example.apigatewayservice.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ApiGatewayController {
    private final Environment env;

    @Timed(value="gateways.status",longTask=true)
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in User Service"
                +"\nenv.getProperty(gateway.ip)=" + env.getProperty("gateway.ip")
                +"\nenv.getProperty(local.server.port)=" + env.getProperty("local.server.port")
                +"\nenv.getProperty(server.port)=" + env.getProperty("server.port")
                +"\nenv.getProperty(token.secret)=" + env.getProperty("token.secret")
                +"\nenv.getProperty(token.experation_time)=" + env.getProperty("token.experation_time")
        );
    }

    @Timed(value="gateways.welcome",longTask=true)
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";

    }

}
