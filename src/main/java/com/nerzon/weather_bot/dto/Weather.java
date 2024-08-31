package com.nerzon.weather_bot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {
    private String icon;
    private String code;
    private String description;
}
