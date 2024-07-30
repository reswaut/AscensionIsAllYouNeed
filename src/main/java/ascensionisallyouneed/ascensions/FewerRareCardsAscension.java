package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class FewerRareCardsAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerRareCardsAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

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
        return chance + AscensionIsAllYouNeed.rareCardProb;
    }

    @Override
    public int modifyNumberOfCardsInReward(int numCards) {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
            return numCards + AscensionIsAllYouNeed.additionalCardInElite;
        }
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            return numCards + AscensionIsAllYouNeed.additionalCardInBoss;
        }
        return numCards;
    }
}
