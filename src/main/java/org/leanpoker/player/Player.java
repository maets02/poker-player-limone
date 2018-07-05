package org.leanpoker.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Player {

    static final String VERSION = "0.0.12";

    public static int betRequest(JsonElement request) {
        int round = request.getAsJsonObject().getAsJsonPrimitive("round").getAsInt();
        System.out.println("limone_Round " + round);
        Iterator<JsonElement> iterator = request.getAsJsonObject().getAsJsonArray("players").iterator();
        int bet = 0;
        int stack = 0;

        List<Card> holeCardRanks = new ArrayList<>();
        List<Card> publicCardRanks = new ArrayList<>();

        while (iterator.hasNext()) {
            JsonElement currentPlayer = iterator.next();
            JsonPrimitive playerName = currentPlayer.getAsJsonObject().getAsJsonPrimitive("name");
            if ("limone".equals(playerName.getAsString())) {
                bet = currentPlayer.getAsJsonObject().getAsJsonPrimitive("bet").getAsInt();
                Iterator<JsonElement> cardsIterator = currentPlayer.getAsJsonObject().getAsJsonArray("hole_cards").iterator();
                while (cardsIterator.hasNext()) {
                    JsonElement nextCard = cardsIterator.next();
                    holeCardRanks.add(readCardRank(nextCard));
                }
                stack = currentPlayer.getAsJsonObject().getAsJsonPrimitive("stack").getAsInt();
            }
        }
        Iterator<JsonElement> communityCardIter = request.getAsJsonObject().getAsJsonArray("community_cards").iterator();
        while (communityCardIter.hasNext()) {
            JsonElement nextCard = communityCardIter.next();
            publicCardRanks.add(readCardRank(nextCard));
        }
        if (holeCardRanks.size() == 2) {
            Card card1 = holeCardRanks.get(0);
            Card card2 = holeCardRanks.get(1);
            if (keepPlaying(card1.rank, card2.rank)) {
                int currentBuyIn = request.getAsJsonObject().getAsJsonPrimitive("current_buy_in").getAsInt();
                int minimumBuyIn = currentBuyIn - bet;
                int small_blind = request.getAsJsonObject().getAsJsonPrimitive("small_blind").getAsInt();
                int bettingAmount = minimumBuyIn < small_blind ? small_blind : minimumBuyIn + 5;
                if (publicCardRanks.size() == 5) {
                    HashMap<String, Integer> countsByRank = new HashMap<>();
                    if (card1.rank.equals(card2.rank))
                        countsByRank.put(card1.rank, 2);
                    else {
                        countsByRank.put(card1.rank, 1);
                        countsByRank.put(card2.rank, 2);
                    }
                    for (Card s : publicCardRanks) {
                        Integer r = countsByRank.get(s.rank);
                        int c = r == null ? 0 : r;
                        countsByRank.put(s.rank, c + 1);
                    }

                    if (card1.suite.equals(card2.suite)) {
                        int suitCountPublic = 0;
                        for (Card c : publicCardRanks) {
                            if (c.suite.equals(card1.suite)) suitCountPublic++;
                        }
                        if (suitCountPublic == 3) {
                            System.out.println("limone_All in " + stack);
                            return stack;
                        }
                    }

                    if (publicCardRanks.size() == 5) {
                        for (int x : countsByRank.values()) {
                            if (x >= 3) {
                                System.out.println("limone_All in " + stack);
                                return stack;
                            }
                        }
                    }
                }
                System.out.println("limone_Betting " + bettingAmount);
                return bettingAmount;
            }
        }
        System.out.println("limone_fold");
        return 0;
    }


    private static Card readCardRank(JsonElement nextCard) {
        JsonObject nextCardAsJsonObject = nextCard.getAsJsonObject();
        String rank = nextCardAsJsonObject.getAsJsonPrimitive("rank").getAsString();
        String suite = nextCardAsJsonObject.getAsJsonPrimitive("suit").getAsString();
        return new Card(rank, suite);
    }

    private static boolean keepPlaying(String rank1, String rank2) {
        System.out.println("limone_holding cards " + rank1 + " - " + rank2);
        List<String> l = Arrays.asList("AA", "KK", "QQ", "JJ", "1010", "99", "88", "77", "AK", "AQ", "AJ", "A10", "KQ", "KJ", "K10", "QJ", "Q10", "J10", "J9", "109");
        return l.contains(rank1 + rank2) || l.contains(rank2 + rank1);
    }

    private static class Card {
        final String rank;
        final String suite;

        private Card(String rank, String suite) {
            this.rank = rank;
            this.suite = suite;
        }
    }

    public static void showdown(JsonElement game) {
    }
}