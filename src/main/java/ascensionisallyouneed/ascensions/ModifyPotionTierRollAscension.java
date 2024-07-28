package ascensionisallyouneed.ascensions;

import com.megacrit.cardcrawl.potions.AbstractPotion;

public interface ModifyPotionTierRollAscension {
    AbstractPotion.PotionRarity modifyPotionTier(int roll, int commonPotionChance, int uncommonPotionChance);
}
