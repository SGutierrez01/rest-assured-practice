package com.testing.api.utils;

public final class Constants {
    public static final String VALUE_CONTENT_TYPE       = "application/json";
    public static final String CONTENT_TYPE             = "Content-Type";
    public static final String CLIENTS_PATH             = "clients";
    public static final String RESOURCES_PATH            = "resources";
    public static final String DEFAULT_CLIENTS_FILE_PATH = "src/main/resources/data/defaultClients.json";
    public static final String DEFAULT_RESOURCES_FILE_PATH = "src/main/resources/data/defaultResources.json";
    public static final String BASE_URL                 = "https://66e2fbd0494df9a478e3dd40.mockapi.io";
    public static final String URL                      = "/api/v1/%s";
    public static final String URL_WITH_PARAM           = "/api/v1/%s/%s";

    private Constants() {
    }
}
