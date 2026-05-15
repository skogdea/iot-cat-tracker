package com.staticoyster.iotcattracker.exception;

public class CatDrinkWaterSignalModelNotFoundException extends RuntimeException {
    public CatDrinkWaterSignalModelNotFoundException(String time) {
        super("There are no new signal models after " + time + ".");
    }
}
