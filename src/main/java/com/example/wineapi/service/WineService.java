package com.example.wineapi.service;

import com.example.wineapi.dto.WineRequest;
import com.example.wineapi.dto.WineResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class WineService {

    private final ChatClient chat;

    public WineService(ChatClient.Builder builder) {
        chat = builder.build();
    }

    public WineResponse suggestion(WineRequest request) {
        return chat.prompt()
                .system("Given the dish and wine list. Return two wines from the list, " +
                        "that would be a great pairing for the food chosen.")
                .user("Meal: " + request.mealPairing() + " Wines: " + request.wines())
                .call()
                .entity(WineResponse.class, ChatClient.EntityParamSpec::useProviderStructuredOutput);
    }
}
