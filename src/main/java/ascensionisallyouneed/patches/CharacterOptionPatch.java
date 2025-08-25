package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CharacterOptionPatch {
    @SpirePatch(
            clz = CharacterOption.class,
            method = "updateHitbox"
    )
    public static class InsertUpdateHitbox {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"pref", "maxAscensionLevel", "ascensionLevel"}
        )
        public static SpireReturn<Void> Insert(CharacterOption __instance, Prefs pref, @ByRef int[] maxAscensionLevel, @ByRef int[] ascensionLevel) {
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel =
                    Math.max(pref.getInteger("LAST_ASCENSION_LEVEL", 1),
                            pref.getInteger(AscensionIsAllYouNeed.makeID("LAST_ASCENSION_LEVEL"), 1));
            if (newAscensionLevel < CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel) {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel = newAscensionLevel;
            }

            maxAscensionLevel[0] =
                    Math.max(pref.getInteger("ASCENSION_LEVEL", 1),
                            pref.getInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), 1));
            if (newAscensionLevel < maxAscensionLevel[0]) {
                maxAscensionLevel[0] = newAscensionLevel;
            }

            ascensionLevel[0] = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
            if (ascensionLevel[0] > maxAscensionLevel[0]) {
                ascensionLevel[0] = maxAscensionLevel[0];
            }

            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = AscensionIsAllYouNeed.getAscensionLevelInfo(ascensionLevel[0]);

            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CharacterSelectScreen.class, "ascLevelInfoString");
                int[] tmp = LineFinder.findAllInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] - 1};
            }
        }
    }

    @SpirePatch(
            clz = CharacterOption.class,
            method = "incrementAscensionLevel"
    )
    public static class InsertIncrementAscensionLevel {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(CharacterOption __instance, int level) {
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = AscensionIsAllYouNeed.getAscensionLevelInfo(level);
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CharacterSelectScreen.class, "ascLevelInfoString");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CharacterOption.class,
            method = "decrementAscensionLevel"
    )
    public static class InsertDecrementAscensionLevel {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(CharacterOption __instance, int level) {
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = AscensionIsAllYouNeed.getAscensionLevelInfo(level);
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CharacterSelectScreen.class, "ascLevelInfoString");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
