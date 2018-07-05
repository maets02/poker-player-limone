package org.leanpoker.player;

import com.google.gson.JsonElement;

import java.util.Map;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        System.out.println("Wir sind dabei, Hurra!");
        System.out.println(request.getAsJsonObject().toString());
        System.out.println("Wir sind dabei, Hurra! die zweite");
        return 0;
    }

    public static void showdown(JsonElement game) {
    }
}
