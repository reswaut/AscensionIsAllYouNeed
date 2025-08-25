package ascensionisallyouneed.ascensions;

import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

public interface ModifyRelicTierRollAscension {
    RelicTier modifyRelicTier(int roll, int commonRelicChance, int uncommonRelicChance);
}
