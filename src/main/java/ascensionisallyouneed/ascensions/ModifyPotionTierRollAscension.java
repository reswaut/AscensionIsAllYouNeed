package ascensionisallyouneed.ascensions;

import com.megacrit.cardcrawl.potions.AbstractPotion.PotionRarity;

public interface ModifyPotionTierRollAscension {
    PotionRarity modifyPotionTier(int roll, int commonPotionChance, int uncommonPotionChance);
}
