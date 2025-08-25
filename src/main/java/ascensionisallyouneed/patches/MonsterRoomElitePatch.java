package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class MonsterRoomElitePatch {
    @SpirePatch(
            clz = MonsterRoomElite.class,
            method = "returnRandomRelicTier"
    )
    public static class InsertReturnRandomRelicTier {
        @SpireInsertPatch(locator = Locator.class, localvars = "roll")
        public static SpireReturn<RelicTier> Insert(MonsterRoomElite __instance, int roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRelicTierRollAscension) {
                    return SpireReturn.Return(((ModifyRelicTierRollAscension) ascension).modifyRelicTier(roll, 50, 33));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(ModHelper.class, "isModEnabled");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 2};
            }
        }
    }
}
