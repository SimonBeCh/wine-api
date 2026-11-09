package com.example.wineapi.controller;

import com.example.wineapi.dto.imageOperation.ImageWineRequest;
import com.example.wineapi.dto.imageOperation.ImageWineResponse;
import com.example.wineapi.service.WineService;
import com.example.wineapi.service.ocr.OCRConverter;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController


public class OCRController {

    private final static Logger LOGGER = LoggerFactory.getLogger(OCRController.class);
    private final WineService wineService;

    public OCRController(WineService wineService) {
        this.wineService = wineService;
    }

    @PostMapping("/wine/image/suggest")
    public ImageWineResponse convertImageToText(
            @RequestParam("image") MultipartFile image,
            @RequestParam("mealPairing") String mealPairing)
            throws IllegalArgumentException, TesseractException, IOException {
        LOGGER.info("Processing image from request");
        return wineService.convertImageToSuggestion(image, new ImageWineRequest(mealPairing));
    }
}
