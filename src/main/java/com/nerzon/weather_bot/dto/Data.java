package com.nerzon.weather_bot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Data {
    private float wind_spd;
    private int temp;
    private int pop;
    private Weather weather;
}