package com.harvesttracker.common.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class FaviconController {

    @GetMapping("favicon.ico")
    public ResponseEntity<Void> returnNoFavicon() {
        return ResponseEntity.noContent().build();
    }
}
