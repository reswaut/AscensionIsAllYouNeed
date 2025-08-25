package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion.PotionRarity;

public class FewerRarePotionsAscension extends AbstractAscension implements ModifyPotionTierRollAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerRarePotionsAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 28;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public PotionRarity modifyPotionTier(int roll, int commonPotionChance, int uncommonPotionChance) {
        int offset = (100 - commonPotionChance - uncommonPotionChance) / 2;
        commonPotionChance += offset;
        if (roll < commonPotionChance) {
            return PotionRarity.COMMON;
        }
        if (roll < commonPotionChance + uncommonPotionChance) {
            return PotionRarity.UNCOMMON;
        }
        return PotionRarity.RARE;
    }

    @Override
    public Color getColor() {
        return Settings.GOLD_COLOR;
    }
}
