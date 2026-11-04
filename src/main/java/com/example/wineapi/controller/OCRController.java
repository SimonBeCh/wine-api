package com.example.wineapi.controller;

import com.example.wineapi.service.ocr.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController


public class OCRController {

    private final OCRService ocrService;

    private final static Logger LOGGER = LoggerFactory.getLogger(OCRController.class);

    public OCRController(OCRService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/wine/image/suggest")
    public String convertImageToText(@RequestParam("file") MultipartFile multipartFile)
            throws IllegalArgumentException, TesseractException, IOException {
        LOGGER.info("Processing image from request");
        return ocrService.processImage(multipartFile);
    }
}
