package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CharStatPatch {
    @SpirePatch(
            clz = CharStat.class,
            method = "incrementAscension"
    )
    public static class InsertIncrementAscension {
        @SpireInsertPatch(locator = Locator.class, localvars = "pref")
        public static void Insert(CharStat __instance, Prefs pref) {
            int derp = Math.max(pref.getInteger("ASCENSION_LEVEL", 1),
                    pref.getInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), 1));
            ++derp;
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if (derp <= newAscensionLevel) {
                pref.putInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), derp);
                pref.putInteger(AscensionIsAllYouNeed.makeID("LAST_ASCENSION_LEVEL"), derp);
                pref.flush();
            } else {
                pref.putInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), newAscensionLevel);
                pref.putInteger(AscensionIsAllYouNeed.makeID("LAST_ASCENSION_LEVEL"), newAscensionLevel);
                pref.flush();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Prefs.class, "getInteger");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
