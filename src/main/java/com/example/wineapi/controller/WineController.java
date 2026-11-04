package com.example.wineapi.controller;

import com.example.wineapi.dto.WineRequest;
import com.example.wineapi.dto.WineResponse;
import com.example.wineapi.service.WineService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/wine")
public class WineController {

    private final WineService service;

    public WineController(WineService service) {
        this.service = service;
    }

    @PostMapping("/suggest")
    public WineResponse wineSuggestion(@RequestBody WineRequest wineRequest) {
        return service.suggestion(wineRequest);
    }

    @PostMapping(value = "/image/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WineResponse> convertImageToSuggestion(@RequestPart("image") MultipartFile multipartFile, @RequestPart("wineRequest") WineRequest wineRequest)
            throws TesseractException, IOException {
        return ResponseEntity.status(HttpStatus.OK).body(service.convertImageToSuggestion(multipartFile, wineRequest));
    }
}
