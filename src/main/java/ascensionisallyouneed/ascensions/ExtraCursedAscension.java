package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class ExtraCursedAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(ExtraCursedAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 30;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public void modifyStartingDeck(CardGroup masterDeck) {
        for (AbstractCard card : masterDeck.group) {
            if (card instanceof AscendersBane) {
                card.upgrade();
            }
        }
    }
}
