package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class ShopScreenPatch {
    private static int getPurgeCostIncrement() {
        int ascensionLevel = AbstractDungeon.ascensionLevel;
        int increment = 25;
        for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
            if (ascensionLevel >= ascension.getAscensionLevel()) {
                increment = ascension.modifyShopPurgeCostIncrement(increment);
            }
        }
        return increment;
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "rollRelicTier"
    )
    public static class InsertRollRelicTier {
        @SpireInsertPatch(locator = Locator.class, localvars = "roll")
        public static SpireReturn<RelicTier> Insert(int roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRelicTierRollAscension) {
                    return SpireReturn.Return(((ModifyRelicTierRollAscension) ascension).modifyRelicTier(roll, 48, 34));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "purgeCard"
    )
    public static class PrefixPurgeCard {
        @SpirePrefixPatch
        public static void Prefix() {
            ShopScreen.purgeCost += getPurgeCostIncrement() - 25;
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "updatePurgeCard"
    )
    public static class InsertUpdatePurgeCard {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert() {
            int increment = getPurgeCostIncrement();
            if (increment == 25) {
                return SpireReturn.Continue();
            }
            TipHelper.renderGenericTip(InputHelper.mX - 360.0F * Settings.scale, InputHelper.mY - 70.0F * Settings.scale, ShopScreen.LABEL[0], ShopScreen.MSG[0] + increment + ShopScreen.MSG[1]);
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(TipHelper.class, "renderGenericTip");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
