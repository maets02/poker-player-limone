package org.leanpoker.player;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.UUID;

public class Player {

    static final String VERSION = "0.0.3";

    public static int betRequest(JsonElement request) {
        System.out.print(request);
        return 236;
    }

    public static void showdown(JsonElement game) {
    }
}
