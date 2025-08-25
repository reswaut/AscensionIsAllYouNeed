package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CustomModeScreenPatch {
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "updateAscension"
    )
    public static class InsertIncrementAscension1 {
        @SpireInsertPatch(
                locator = Locator1.class
        )
        public static SpireReturn<Void> Insert1(CustomModeScreen __instance) {
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if (__instance.ascensionLevel > newAscensionLevel) {
                __instance.ascensionLevel = 1;
            }
            return SpireReturn.Return();
        }

        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CustomModeScreen.class, "ascensionLevel");
                int[] tmp = LineFinder.findAllInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[8]};
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class
        )
        public static SpireReturn<Void> Insert2(CustomModeScreen __instance) {
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if (__instance.ascensionLevel < 1) {
                __instance.ascensionLevel = newAscensionLevel;
            }
            return SpireReturn.Return();
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CustomModeScreen.class, "ascensionLevel");
                int[] tmp = LineFinder.findAllInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[4]};
            }
        }
    }

    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "renderAscension"
    )
    public static class InsertRenderAscension {
        private static int trueAscensionLevel = 0;
        @SpireInsertPatch(
                locator = Locator1.class
        )
        public static void Insert1(CustomModeScreen __instance, SpriteBatch sb) {
            trueAscensionLevel = __instance.ascensionLevel;
            __instance.ascensionLevel = 0;
        }

        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(FontHelper.class, "renderSmartText");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] - 1};
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class,
                localvars = {"ASC_RIGHT_W", "ascensionModeHb"}
        )
        public static void Insert2(CustomModeScreen __instance, SpriteBatch sb, float ASC_RIGHT_W, Hitbox ascensionModeHb) {
            if (trueAscensionLevel != 0) {
                __instance.ascensionLevel = trueAscensionLevel;
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = AscensionIsAllYouNeed.getAscensionLevelInfo(__instance.ascensionLevel);
                FontHelper.renderSmartText(
                        sb,
                        FontHelper.charDescFont,
                        CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString,
                        CustomModeScreen.screenX + ASC_RIGHT_W * 1.1F + 400.0F * Settings.scale,
                        ascensionModeHb.cY + 10.0F * Settings.scale,
                        9999.0F,
                        32.0F * Settings.scale,
                        Settings.CREAM_COLOR);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(FontHelper.class, "renderSmartText");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }
}
