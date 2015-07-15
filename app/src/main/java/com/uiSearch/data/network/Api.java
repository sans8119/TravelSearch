package com.uiSearch.data.network;


public enum Api {
    API_URL("http://api.goeuro.com/api/v2/"),
    API_URL_GET_SEARCH_LIST("position/suggest/");

    private final String value;

    Api(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
