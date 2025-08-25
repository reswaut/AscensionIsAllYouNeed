package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class TopPanelPatch {
    @SpirePatch(
            clz = TopPanel.class,
            method = "setupAscensionMode"
    )
    public static class InsertSetupAscensionMode {
        private static final String ID = AscensionIsAllYouNeed.makeID(TopPanelPatch.class.getSimpleName());
        private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        private static final String[] TEXT = uiStrings.TEXT;
        private static final String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;

        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"ascensionString", "sb"}
        )
        public static SpireReturn<Void> Insert(TopPanel __instance, @ByRef String[] ascensionString, StringBuilder sb) {
            int start = AbstractDungeon.ascensionLevel - AscensionIsAllYouNeed.modConfigs.ascensionStringRows + 1;
            if (start > 1) {
                sb.append(String.format(start - 1 == 1 ? TEXT[0] : TEXT[1], start - 1));
                sb.append(" NL ");
            } else {
                start = 1;
            }
            for (int i = start; i <= AbstractDungeon.ascensionLevel; ++i) {
                sb.append(AscensionIsAllYouNeed.getAscensionLevelInfo(i));
                if (i != AbstractDungeon.ascensionLevel) {
                    sb.append(" NL ");
                }
            }
            ascensionString[0] = sb.toString();
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(AbstractDungeon.class, "ascensionLevel");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "updateAscensionHover"
    )
    public static class InsertUpdateAscensionHover {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(TopPanel __instance) {
            if (AscensionIsAllYouNeed.modConfigs.ascensionStringRows <= 0) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(TipHelper.class, "renderGenericTip");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "renderDungeonInfo"
    )
    public static class InsertRenderDungeonInfo {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"floorX", "INFO_TEXT_Y"}
        )
        public static void Insert(TopPanel __instance, SpriteBatch sb, float floorX, float INFO_TEXT_Y) {
            if (!AbstractDungeon.isAscensionMode) {
                return;
            }
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            if (ascensionLevel <= 20) {
                return;
            }
            AbstractAscension ascension = AscensionIsAllYouNeed.getAscensionAboveA20(ascensionLevel);
            if (ascension == null) {
                return;
            }
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(AbstractDungeon.ascensionLevel), floorX + 166.0F * Settings.scale, INFO_TEXT_Y, ascension.getColor());
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(TopPanel.class, "ascensionHb");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
