package com.nerzon.weather_bot.service.manager;

import com.nerzon.weather_bot.dto.Data;
import com.nerzon.weather_bot.dto.WeatherResponse;
import com.nerzon.weather_bot.service.WeatherService;
import com.nerzon.weather_bot.service.factory.KeyboardFactory;
import com.nerzon.weather_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.nerzon.weather_bot.data.QueryData.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForecastManager {
    KeyboardFactory keyboardFactory;
    WeatherService weatherService;

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return SendMessage.builder()
                .text(
                        """
                                Сперва введите название города на английском языке,
                                потом выберете вариант прогноза ⬇️⬇️⬇️
                                """
                )
                .chatId(message.getChatId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("Ввести город"),
                                List.of(1),
                                List.of(fc_c_.name())
                        )
                )
                .build();
    }

    public BotApiMethod<?> answerQuery(CallbackQuery query, String[] data, Bot bot) {
        if (data.length == 1) {
            return mainMenu(query, bot);
        }
        switch (data[1]) {
            case "c" -> {
                return cityEntering(query, data.length == 2 ? "" : data[2], bot);
            }
            case "e" -> {
                if (data.length == 2) {
                    return AnswerCallbackQuery.builder()
                            .callbackQueryId(query.getId())
                            .text("Необходимо ввести название города!")
                            .build();
                }
                return cityEntered(query, data[2], bot);
            }
            case "s" -> {
                return sendResult(query, data[2], data[3], bot);
            }
        }
        log.error("Unsupported query: " + query);
        return null;
    }

    private BotApiMethod<?> sendResult(CallbackQuery query, String type, String cityName, Bot bot) {
        try {
            bot.execute(
                    SendMessage.builder()
                            .chatId(query.getMessage().getChatId())
                            .text("⏱ Запрос отправлен, ожидайте")
                            .build()
            );
            bot.execute(
                    DeleteMessage.builder()
                            .messageId(query.getMessage().getMessageId())
                            .chatId(query.getMessage().getChatId())
                            .build()
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        WeatherResponse weatherResponse = "h".equals(type) ? weatherService.getHoursByCityName(cityName) : weatherService.getDaysByCityName(cityName);
        StringBuilder sb = new StringBuilder();
        int number = 1;
        for (Data data : weatherResponse.getData()) {
            sb.append("\uD83D\uDCCD +").append(number).append(")\n")
                    .append("Температура: ").append(data.getTemp()).append("\n")
                    .append("Вероятность осадков: ").append(data.getPop()).append("%\n")
                    .append("Скорость ветра: ").append(data.getWind_spd()).append("\n")
                    .append("Описание: ").append(data.getWeather().getDescription()).append("\n\n");
            number++;
        }
        return SendMessage.builder()
                .text(sb.toString())
                .chatId(query.getMessage().getChatId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("На главную"),
                                List.of(1),
                                List.of(main.name())
                        )
                )
                .build();
    }

    private BotApiMethod<?> cityEntered(CallbackQuery query, String cityName, Bot bot) {
        return EditMessageText.builder()
                .chatId(query.getMessage().getChatId())
                .messageId(query.getMessage().getMessageId())
                .text(
                        """
                                Теперь выеберете один из двух режимов:
                                1️⃣ На следующие 24 часа
                                2️⃣ На следующие 7 дней
                                """
                )
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("24 часа", "7 дней"),
                                List.of(2),
                                List.of(fc_s_.name() + "h_" + cityName, fc_s_.name() + "d_" + cityName)
                        )
                )
                .build();
    }

    private BotApiMethod<?> cityEntering(CallbackQuery query, String current, Bot bot) {
        return EditMessageText.builder()
                .text("Введите название города используя интерактивную клавиатуру")
                .replyMarkup(
                        cityEnteringKeyboard(current)
                )
                .messageId(query.getMessage().getMessageId())
                .chatId(query.getMessage().getChatId())
                .build();
    }

    private InlineKeyboardMarkup cityEnteringKeyboard(String current) {
        return keyboardFactory.getInlineKeyboard(
                List.of(
                        !current.isEmpty() ? current : "Поле ввода",
                        "Q", "W", "E", "R", "T", "Y", "U", "I",
                        "O", "P", "A", "S", "D", "F", "G", "H",
                        "J", "K", "L", "Z", "X", "C", "V", "B",
                        "N", "M", "-", "\uD83D\uDEAB", "\uD83D\uDEAB", "\uD83D\uDEAB", "\uD83D\uDEAB", "\uD83D\uDEAB",
                        "Ввод", "Стереть"
                ),
                List.of(1, 8, 8, 8, 8, 2),
                List.of(
                        empty.name(),
                        fc_c_.name() + current + "Q", fc_c_.name() + current + "W",
                        fc_c_.name() + current + "E", fc_c_.name() + current + "R",
                        fc_c_.name() + current + "T", fc_c_.name() + current + "Y",
                        fc_c_.name() + current + "U", fc_c_.name() + current + "I",
                        fc_c_.name() + current + "O", fc_c_.name() + current + "P",
                        fc_c_.name() + current + "A", fc_c_.name() + current + "S",
                        fc_c_.name() + current + "D", fc_c_.name() + current + "F",
                        fc_c_.name() + current + "G", fc_c_.name() + current + "H",
                        fc_c_.name() + current + "J", fc_c_.name() + current + "K",
                        fc_c_.name() + current + "L", fc_c_.name() + current + "Z",
                        fc_c_.name() + current + "X", fc_c_.name() + current + "C",
                        fc_c_.name() + current + "V", fc_c_.name() + current + "B",
                        fc_c_.name() + current + "N", fc_c_.name() + current + "M",
                        fc_c_.name() + current + "-", empty.name(), empty.name(), empty.name(),
                        empty.name(), empty.name(), fc_e_.name() + current,
                        current.length() <= 1 ? fc_c_.name() : fc_c_.name() + current.substring(0, current.length() - 1)
                )
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery query, Bot bot) {
        return EditMessageText.builder()
                .text(
                        """
                                Сперва введите название города на английском языке,
                                потом выберете вариант прогноза ⬇️⬇️⬇️
                                """
                )
                .chatId(query.getMessage().getChatId())
                .messageId(query.getMessage().getMessageId())
                .replyMarkup(
                        keyboardFactory.getInlineKeyboard(
                                List.of("Ввести город"),
                                List.of(1),
                                List.of(fc_c_.name())
                        )
                )
                .build();
    }

}
