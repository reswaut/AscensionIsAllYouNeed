package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class PoorElitesAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(PoorElitesAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 21;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public int modifyGoldRewards(AbstractRoom room, int gold) {
        if (room instanceof MonsterRoomElite) {
            return Math.round(gold * 0.75F);
        }
        return gold;
    }

    @Override
    public Color getColor() {
        return Settings.GOLD_COLOR;
    }
}
