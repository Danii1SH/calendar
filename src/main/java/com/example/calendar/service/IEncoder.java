package com.example.calendar.service;

public interface IEncoder {
    String getCode(String str);
    boolean checkPassword(String passwordFromDB, String inputPassword);
}
