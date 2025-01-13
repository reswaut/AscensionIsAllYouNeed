package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class AbstractAscension implements Comparable<AbstractAscension> {
    public static final String ID = AscensionIsAllYouNeed.makeID("AscensionModeDescriptions");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    public String getAscensionInfo() {
        return TEXT[0];
    }
    public int getAscensionLevel() {
        return -1;
    }

    public int modifyGoldRewards(AbstractRoom room, int gold) {
        return gold;
    }
    public int modifyPotionChance(AbstractRoom room, int chance) {
        return chance;
    }
    public int modifyStartingGold(AbstractPlayer player, int gold) {
        return gold;
    }
    public void modifyMap(ArrayList<ArrayList<MapRoomNode>> map) {}
    public int modifyCardChance(int chance) {
        return chance;
    }
    public void modifyStartingDeck(CardGroup masterDeck) {}

    public float modifyShopRoomChance(float chance) {
        return chance;
    }

    public float modifyRestRoomChance(float chance) {
        return chance;
    }

    public float modifyEliteRoomChance(float chance) {
        return chance;
    }

    public float modifyEventRoomChance(float chance) {
        return chance;
    }

    public int modifyNumberOfCardsInReward(int numCards) {
        return numCards;
    }

    @Override
    public int compareTo(AbstractAscension o) {
        return this.getAscensionLevel() - o.getAscensionLevel();
    }
}
