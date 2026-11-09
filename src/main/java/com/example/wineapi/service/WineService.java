package com.example.wineapi.service;

import com.example.wineapi.dto.imageOperation.ImageWineRequest;
import com.example.wineapi.dto.imageOperation.ImageWineResponse;
import com.example.wineapi.dto.manualOperation.WineRequest;
import com.example.wineapi.dto.manualOperation.WineResponse;
import com.example.wineapi.service.ocr.OCRConverter;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class WineService {

    private final ChatClient chat;
    private final OCRConverter ocrConverter;

    public WineService(ChatClient.Builder builder, OCRConverter ocrConverter) {
        chat = builder.build();
        this.ocrConverter = ocrConverter;
    }

// Suggestion based on manual input
    public WineResponse suggestion(WineRequest wineRequest) {
        return chat.prompt()
                .system("Given the dish and uploaded wine list. Return two wines from the list, " +
                        "that would be a great pairing for the food chosen.")
                .user("Meal: " + wineRequest.mealPairing() + " Wines: " + wineRequest.wines())
                .call()
                .entity(WineResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
// Suggestion based on image passed through OCR converter
    public ImageWineResponse convertImageToSuggestion(MultipartFile image, ImageWineRequest imageWineRequest) throws TesseractException, IOException {

        String ocrText = ocrConverter.processImage(image);
        if (ocrText.isEmpty()) {
            throw new IllegalArgumentException("No text could be read from image chosen");
        }

        return chat.prompt()
                .system("Given the dish chosen and the list of wines you receive from OCR text. " +
                        "Pick two wines that would pair well with the dish. " +
                        "Only choose wines from the OCR text list")
                .user("Meal: " + imageWineRequest.mealPairing() + " Wines: " + ocrText)
                .call()
                .entity(ImageWineResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
