package com.example.calendar.model;

import lombok.Data;

@Data
public class CaptchaModel {
    private String captcha;
    private String hiddenCaptcha;
    private String realCaptcha;

}