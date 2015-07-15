package com.uiSearch.utils;

public enum Constants {

    RainDropRadius("60");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
