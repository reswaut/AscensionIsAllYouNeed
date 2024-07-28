package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class FewerRarePotionsAscension extends AbstractAscension implements ModifyPotionTierRollAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerRarePotionsAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 28;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public AbstractPotion.PotionRarity modifyPotionTier(int roll, int commonPotionChance, int uncommonPotionChance) {
        int offset = (100 - commonPotionChance - uncommonPotionChance) / 2;
        commonPotionChance += offset;
        if (roll < commonPotionChance) {
            return AbstractPotion.PotionRarity.COMMON;
        }
        if (roll < commonPotionChance + uncommonPotionChance) {
            return AbstractPotion.PotionRarity.UNCOMMON;
        }
        return AbstractPotion.PotionRarity.RARE;
    }
}
