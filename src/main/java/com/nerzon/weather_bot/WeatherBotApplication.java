package com.nerzon.weather_bot;

import com.nerzon.weather_bot.telegram.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties.class)
public class WeatherBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherBotApplication.class, args);
	}

}
