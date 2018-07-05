package org.leanpoker.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Player {

	static final String VERSION = "0.0.9";

	public static int betRequest(JsonElement request) {
		int round = request.getAsJsonObject().getAsJsonPrimitive("round").getAsInt();
		Iterator<JsonElement> iterator = request.getAsJsonObject().getAsJsonArray("players").iterator();
		int bet = 0;
		int stack = 0;

		List<String> holeCardRanks = new ArrayList<>();
		List<String> publicCardRanks = new ArrayList<>();

		while (iterator.hasNext()) {
			JsonElement currentPlayer = iterator.next();
			JsonPrimitive playerName = currentPlayer.getAsJsonObject().getAsJsonPrimitive("name");
			if ("limone".equals(playerName.getAsString())) {
				bet = currentPlayer.getAsJsonObject().getAsJsonPrimitive("bet").getAsInt();
				Iterator<JsonElement> cardsIterator = currentPlayer.getAsJsonObject().getAsJsonArray("hole_cards")
						.iterator();
				while (cardsIterator.hasNext()) {
					JsonElement nextCard = cardsIterator.next();
					holeCardRanks.add(readCardRank(nextCard));
				}
				stack = currentPlayer.getAsJsonObject().getAsJsonPrimitive("stack").getAsInt();
			}
		}
		Iterator<JsonElement> communityCardIter = request.getAsJsonObject().getAsJsonArray("community_cards")
				.iterator();
		while (communityCardIter.hasNext()) {
			JsonElement nextCard = communityCardIter.next();
			publicCardRanks.add(readCardRank(nextCard));
		}
		if (holeCardRanks.size() == 2) {
			String rank1 = holeCardRanks.get(0);
			String rank2 = holeCardRanks.get(1);
			if (keepPlaying(rank1, rank2, publicCardRanks)) {
				int currentBuyIn = request.getAsJsonObject().getAsJsonPrimitive("current_buy_in").getAsInt();
				int minimumBuyIn = currentBuyIn - bet;
				int small_blind = request.getAsJsonObject().getAsJsonPrimitive("small_blind").getAsInt();
				int bettingAmount = minimumBuyIn < small_blind ? small_blind : minimumBuyIn + 5;
				if (publicCardRanks.size() == 5) {
					HashMap<String, Integer> countsByRank = new HashMap<>();
					if (rank1.equals(rank2))
						countsByRank.put(rank1, 2);
					else {
						countsByRank.put(rank1, 1);
						countsByRank.put(rank2, 2);
					}
					for (String s : publicCardRanks) {
						Integer r = countsByRank.get(s);
						int c = r == null ? 0 : r;
						countsByRank.put(s, c + 1);
					}

					if (publicCardRanks.size() == 5) {
						for (int x : countsByRank.values()) {
							if (x >= 3)
								return stack;
						}
					}
				}
				System.out.println("limone_Betting " + bettingAmount);
				return bettingAmount;
			}
		}
		return 0;
	}

	private static String readCardRank(JsonElement nextCard) {
		return nextCard.getAsJsonObject().getAsJsonPrimitive("rank").getAsString();
	}

	private static boolean keepPlaying(String rank1, String rank2, List<String> publicCardRanks) {
		System.out.println("limone_holding cards " + rank1 + " - " + rank2);
		List<String> l = Arrays.asList("AA", "KK", "QQ", "JJ", "1010", "99", "88", "77", "AK", "AQ", "AJ", "A10", "KQ",
				"KJ", "K10", "QJ", "Q10", "J10", "J9", "109");
		return l.contains(rank1 + rank2) || l.contains(rank2 + rank1);
	}

	public static void showdown(JsonElement game) {
	}
}