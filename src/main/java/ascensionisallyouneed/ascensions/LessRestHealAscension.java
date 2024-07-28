package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LessRestHealAscension extends AbstractAscension implements ModifyRestHealAmtAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(LessRestHealAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

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
}
