package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

public class LessRestHealAscension extends AbstractAscension implements ModifyRestHealAmtAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(LessRestHealAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 24;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public float modifyRestHealAmt(float healAmt) {
        return healAmt * 0.8F;
    }

    @Override
    public String getRestOptionDescription(boolean nightTerrorsModEnabled, int healAmt) {
        if (nightTerrorsModEnabled) {
            return String.format(EXTRA_TEXT[0], healAmt);
        }
        return String.format(EXTRA_TEXT[1], healAmt);
    }

    @Override
    public Color getColor() {
        return Settings.GOLD_COLOR;
    }
}
