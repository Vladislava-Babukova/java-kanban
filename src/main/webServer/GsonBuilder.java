package main.webServer;

import com.google.gson.Gson;
import main.adapter.DurationAdapter;
import main.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class
GsonBuilder {
    public GsonBuilder() {
    }

    public static Gson getGson() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
        return gson;
    }
}
