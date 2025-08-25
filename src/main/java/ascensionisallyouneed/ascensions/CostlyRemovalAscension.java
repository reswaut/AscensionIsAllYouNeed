package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class CostlyRemovalAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(CostlyRemovalAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 31;
    }

    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public int modifyShopPurgeCostIncrement(int cost) {
        return cost + 25;
    }

    @Override
    public Color getColor() {
        return getChangingColor();
    }
}
