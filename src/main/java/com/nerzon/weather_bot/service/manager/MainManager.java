package com.nerzon.weather_bot.service.manager;

import com.nerzon.weather_bot.service.factory.KeyboardFactory;
import com.nerzon.weather_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.nerzon.weather_bot.data.QueryData.fc;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainManager {
    KeyboardFactory keyboardFactory;

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return SendMessage.builder()
                .text(
                        """
                                🖖 Приветствую в боте прогноза погоды!
                                
                                ✅ Бот был написан в демонстрационных целях
                                ✅ Использовалось API WeatherBit
                                ✅ Использованные технологии: Java & Spring Boot
                                
                                Нажав на кнопку снизу вы переместитесь на страницу прогноза, приятного пользования!
                                """
                )
                .chatId(message.getChatId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("⛈ Прогноз ⛈"),
                                List.of(1),
                                List.of(fc.name())
                        )
                )
                .build();
    }

    public BotApiMethod<?> answerQuery(CallbackQuery query, String[] data, Bot bot) {
        return EditMessageText.builder()
                .text(
                        """
                                🖖 Приветствую в боте прогноза погоды!
                                
                                ✅ Бот был написан в демонстрационных целях
                                ✅ Использовалось API WeatherBit
                                ✅ Использованные технологии: Java & Spring Boot
                                
                                Нажав на кнопку снизу вы переместитесь на страницу прогноза, приятного пользования!
                                """
                )
                .chatId(query.getMessage().getChatId())
                .messageId(query.getMessage().getMessageId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("⛈ Прогноз ⛈"),
                                List.of(1),
                                List.of(fc.name())
                        )
                )
                .build();
    }
}
