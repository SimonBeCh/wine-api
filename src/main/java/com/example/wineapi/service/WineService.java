package com.example.wineapi.service;

import com.example.wineapi.dto.WineRequest;
import com.example.wineapi.dto.WineResponse;
import com.example.wineapi.service.ocr.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class WineService {

    private final ChatClient chat;
    private final OCRService ocrService;

    public WineService(ChatClient.Builder builder, OCRService ocrService) {
        chat = builder.build();
        this.ocrService = ocrService;
    }

    public WineResponse suggestion(WineRequest request) {
        return chat.prompt()
                .system("Given the dish and wine list. Return two wines from the list, " +
                        "that would be a great pairing for the food chosen.")
                .user("Meal: " + request.mealPairing() + " Wines: " + request.wines())
                .call()
                .entity(WineResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }

    public WineResponse convertImageToSuggestion(MultipartFile image, WineRequest request) throws TesseractException, IOException {
        return chat.prompt()
                .system("Given the dish and uploaded image containing wine," + ocrService.processImage(image) + " to return two wines, " +
                        "that would be a great pairing for the food chosen.")
                .user("Meal: " + request.mealPairing() + " Wines: " + request.wines())
                .call()
                .entity(WineResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
