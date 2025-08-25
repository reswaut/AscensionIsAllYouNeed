package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class FewerRareCardsAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerRareCardsAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 29;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public int modifyCardChance(int chance) {
        return chance + AscensionIsAllYouNeed.modConfigs.rareCardProb;
    }

    @Override
    public int modifyNumberOfCardsInReward(int numCards) {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
            return numCards + AscensionIsAllYouNeed.modConfigs.additionalCardInElite;
        }
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            return numCards + AscensionIsAllYouNeed.modConfigs.additionalCardInBoss;
        }
        return numCards;
    }

    @Override
    public Color getColor() {
        return Settings.GOLD_COLOR;
    }
}
