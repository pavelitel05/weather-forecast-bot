package com.nerzon.weather_bot.controller;

import com.nerzon.weather_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainController {
    Bot bot;

    @PostMapping("/")
    public BotApiMethod<?> updateListener(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

}
