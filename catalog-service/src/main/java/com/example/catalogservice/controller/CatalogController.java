package com.example.catalogservice.controller;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class CatalogController {
    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in Catalog Service"
                +"\nenv.getProperty(gateway.ip)=" + env.getProperty("gateway.ip")
                +"\nenv.getProperty(local.server.port)=" + env.getProperty("local.server.port")
                +"\nenv.getProperty(server.port)=" + env.getProperty("server.port")
                +"\nenv.getProperty(token.secret)=" + env.getProperty("token.secret")
                +"\nenv.getProperty(token.experation_time)=" + env.getProperty("token.experation_time")
        );
    }

    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getCatalogs(){
        Iterable<CatalogEntity> allCatalogs = catalogService.getAllCatalogs();
        List<ResponseCatalog> catalogs = new ArrayList<>();
        allCatalogs.forEach(v -> {
            catalogs.add(new ModelMapper().map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK)
                .body(catalogs);
    }
}
