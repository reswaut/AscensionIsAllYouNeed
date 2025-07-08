package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class FewerPotionsAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerPotionsAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 23;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public int modifyPotionChance(AbstractRoom room, int chance) {
        return chance - (AbstractDungeon.actNum - 1) * 10;
    }
}
