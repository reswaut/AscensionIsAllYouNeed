package ascensionisallyouneed.ascensions;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public interface ModifyRelicTierRollAscension {
    AbstractRelic.RelicTier modifyRelicTier(int roll, int commonRelicChance, int uncommonRelicChance);
}
