package ascensionisallyouneed.ascensions;

import com.megacrit.cardcrawl.rewards.chests.AbstractChest;

public interface ModifyChestTierRollAscension {
    AbstractChest modifyChessTier(int roll, int smallChestChance, int mediumChestChance);
}
