package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import javassist.CtBehavior;

public class CharStatPatch {
    @SpirePatch(
            clz = CharStat.class,
            method = "incrementAscension"
    )
    public static class InsertIncrementAscension {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"pref"}
        )
        public static void Insert(CharStat __instance, Prefs pref) {
            int derp = Math.max(pref.getInteger("ASCENSION_LEVEL", 1),
                    pref.getInteger("ascensionisallyouneed:ASCENSION_LEVEL", 1));
            ++derp;
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if (derp <= newAscensionLevel) {
                pref.putInteger("ascensionisallyouneed:ASCENSION_LEVEL", derp);
                pref.putInteger("ascensionisallyouneed:LAST_ASCENSION_LEVEL", derp);
                pref.flush();
            } else {
                pref.putInteger("ascensionisallyouneed:ASCENSION_LEVEL", newAscensionLevel);
                pref.putInteger("ascensionisallyouneed:LAST_ASCENSION_LEVEL", newAscensionLevel);
                pref.flush();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Prefs.class, "getInteger");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
