package org.leanpoker.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Player {

    static final String VERSION = "0.0.5";

    public static int betRequest(JsonElement request) {
        int round = request.getAsJsonObject().getAsJsonPrimitive("round").getAsInt();
        Iterator<JsonElement> iterator = request.getAsJsonObject().getAsJsonArray("players").iterator();
        int bet = 0;

        List<String> holeCardRanks = new ArrayList<>();

        while (iterator.hasNext()) {
            JsonElement currentPlayer = iterator.next();
            JsonPrimitive playerName = currentPlayer.getAsJsonObject().getAsJsonPrimitive("name");
            if ("limone".equals(playerName.getAsString())) {
                bet = currentPlayer.getAsJsonObject().getAsJsonPrimitive("bet").getAsInt();
                Iterator<JsonElement> cardsIterator = currentPlayer.getAsJsonObject().getAsJsonArray("hole_cards").iterator();
                while(cardsIterator.hasNext()) {
                    JsonElement nextCard = cardsIterator.next();
                    holeCardRanks.add(nextCard.getAsJsonObject().getAsJsonPrimitive("rank").getAsString());
                }
            }
        }
        if (holeCardRanks.size() == 2) {
            String rank1 = holeCardRanks.get(0);
            String rank2 = holeCardRanks.get(1);
            if (rank1.equals(rank2)) {
                int currentBuyIn = request.getAsJsonObject().getAsJsonPrimitive("current_buy_in").getAsInt();
                int minimumBuyIn = currentBuyIn - bet;
                int small_blind = request.getAsJsonObject().getAsJsonPrimitive("small_blind").getAsInt();
                return minimumBuyIn < small_blind ? small_blind : minimumBuyIn;
            }
        }
       return 0;
    }

    public static void showdown(JsonElement game) {
    }
}
