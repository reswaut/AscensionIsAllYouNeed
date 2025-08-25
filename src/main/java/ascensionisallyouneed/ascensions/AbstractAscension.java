package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public abstract class AbstractAscension implements Comparable<AbstractAscension> {
    public static final String ID = AscensionIsAllYouNeed.makeID(AbstractAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    public static Color getChangingColor() {
        return new Color(
                (MathUtils.cosDeg(((System.currentTimeMillis()) / 10L % 360L)) + 1.25F) / 2.3F,
                (MathUtils.cosDeg(((System.currentTimeMillis() + 1000L) / 10L % 360L)) + 1.25F) / 2.3F,
                (MathUtils.cosDeg(((System.currentTimeMillis() + 2000L) / 10L % 360L)) + 1.25F) / 2.3F,
                1.0F);
    }

    public abstract String getAscensionInfo();

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

    public abstract int getAscensionLevel();

    public int modifyShopPurgeCostIncrement(int cost) {
        return cost;
    }

    public abstract Color getColor();

    @Override
    public int compareTo(AbstractAscension o) {
        return Integer.compare(getAscensionLevel(), o.getAscensionLevel());
    }
}
