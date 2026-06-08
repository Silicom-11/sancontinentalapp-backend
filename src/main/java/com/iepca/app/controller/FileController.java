package com.iepca.app.controller;

import com.iepca.app.service.FileStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/uploads")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/r2/**")
    public ResponseEntity<InputStreamResource> getFile(jakarta.servlet.http.HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        String key = fullPath.substring("/api/uploads/r2/".length());

        InputStream stream = fileStorageService.getFileStream(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Cache-Control", "public, max-age=86400")
                .body(new InputStreamResource(stream));
    }
}

