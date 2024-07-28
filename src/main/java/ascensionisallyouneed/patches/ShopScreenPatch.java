package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

public class ShopScreenPatch {
    @SpirePatch(
            clz = ShopScreen.class,
            method = "rollRelicTier"
    )
    public static class InsertRollRelicTier {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll"}
        )
        public static SpireReturn<AbstractRelic.RelicTier> Insert(int roll) {
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }
}
