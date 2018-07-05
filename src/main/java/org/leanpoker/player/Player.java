package org.leanpoker.player;

import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Player {

    static final String VERSION = "0.0.3";

    public static int betRequest(JsonElement request) {
    	Iterator<JsonElement> iterator = request.getAsJsonObject().getAsJsonArray("players").iterator();
        int bet = 0;
        while(iterator.hasNext()) {
            JsonElement currentPlayer = iterator.next();
            JsonPrimitive playerName = currentPlayer.getAsJsonObject().getAsJsonPrimitive("name");
            if ("limone".equals(playerName.getAsString())){
                bet = currentPlayer.getAsJsonObject().getAsJsonPrimitive("bet").getAsInt();
            }
        }
        int currentBuyIn = request.getAsJsonObject().getAsJsonPrimitive("current_buy_in").getAsInt();
        int minimumBuyIn = currentBuyIn - bet;
        int small_blind = request.getAsJsonObject().getAsJsonPrimitive("small_blind").getAsInt();
        return minimumBuyIn < small_blind ? small_blind : minimumBuyIn;
    }

    public static void showdown(JsonElement game) {
    }
}
