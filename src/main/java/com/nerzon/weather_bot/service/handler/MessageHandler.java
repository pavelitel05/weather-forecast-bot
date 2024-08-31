package com.nerzon.weather_bot.service.handler;

import com.nerzon.weather_bot.service.factory.KeyboardFactory;
import com.nerzon.weather_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.nerzon.weather_bot.data.QueryData.main;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageHandler {
    KeyboardFactory keyboardFactory;
    public BotApiMethod<?> answer(Message message, Bot bot) {
        return SendMessage.builder()
                .text("❌ Я не умею общаться текстом, взаимодействую со мной через кнопки в меню\n\n⬇\uFE0F⬇\uFE0F⬇\uFE0F")
                .chatId(message.getChatId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("На главную"),
                                List.of(1),
                                List.of(main.name())
                        )
                )
                .build();
    }
}
