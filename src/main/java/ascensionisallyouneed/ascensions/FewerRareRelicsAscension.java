package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.LargeChest;
import com.megacrit.cardcrawl.rewards.chests.MediumChest;
import com.megacrit.cardcrawl.rewards.chests.SmallChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class FewerRareRelicsAscension extends AbstractAscension implements ModifyRelicTierRollAscension, ModifyChestTierRollAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(FewerRareRelicsAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 27;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public AbstractRelic.RelicTier modifyRelicTier(int roll, int commonRelicChance, int uncommonRelicChance) {
        int offset = (100 - commonRelicChance - uncommonRelicChance) / 2;
        commonRelicChance += offset;
        if (roll < commonRelicChance) {
            return AbstractRelic.RelicTier.COMMON;
        }
        if (roll < commonRelicChance + uncommonRelicChance) {
            return AbstractRelic.RelicTier.UNCOMMON;
        }
        return AbstractRelic.RelicTier.RARE;
    }

    @Override
    public AbstractChest modifyChessTier(int roll, int smallChestChance, int mediumChestChance) {
        int offset = (100 - smallChestChance - mediumChestChance) / 2;
        smallChestChance += offset;
        if (roll < smallChestChance) {
            return new SmallChest();
        }
        if (roll < smallChestChance + mediumChestChance) {
            return new MediumChest();
        }
        return new LargeChest();
    }
}
