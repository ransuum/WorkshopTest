package com.example.test.controllers;

import com.example.test.entity.Animal;
import com.example.test.service.FileUploadService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.Scope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/animals")
@Tag(name = "Animal Controller", description = "Need file like .csv or .xml")
public class AnimalController {
    private FileUploadService fileUploadService;

    @Autowired
    public AnimalController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/files/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.uploadFile(file));
    }

    @GetMapping
    @Operation(summary = "get info from file and sort")
    public ResponseEntity<List<Animal>> getAnimals(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String sortBy
    ) {
        return ResponseEntity.ok(fileUploadService.getAnimalBy(type, category, sex, sortBy));
    }
}
