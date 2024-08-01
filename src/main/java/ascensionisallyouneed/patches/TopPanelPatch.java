package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;

public class TopPanelPatch {
    @SpirePatch(
            clz = TopPanel.class,
            method = "setupAscensionMode"
    )
    public static class InsertSetupAscensionMode {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"ascensionString", "sb"}
        )
        public static SpireReturn<Void> Insert(TopPanel __instance, @ByRef String[] ascensionString, StringBuilder sb) {
            String[] SettingExtraText = CardCrawlGame.languagePack.getUIString(AscensionIsAllYouNeed.makeID("Settings")).EXTRA_TEXT;

            int start = AbstractDungeon.ascensionLevel - AscensionIsAllYouNeed.ascensionStringRows + 1;
            if (start > 1) {
                sb.append(String.format(((start - 1) == 1) ? SettingExtraText[0] : SettingExtraText[1], start - 1));
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "ascensionLevel");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
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
            if (AscensionIsAllYouNeed.ascensionStringRows <= 0) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "renderGenericTip");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
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
            if (AbstractDungeon.isAscensionMode && AbstractDungeon.ascensionLevel > 20) {
                Color color = new Color(
                        (MathUtils.cosDeg((float) (System.currentTimeMillis() / 10L % 360L)) + 1.25F) / 2.3F,
                        (MathUtils.cosDeg((float) ((System.currentTimeMillis() + 1000L) / 10L % 360L)) + 1.25F) / 2.3F,
                        (MathUtils.cosDeg((float) ((System.currentTimeMillis() + 2000L) / 10L % 360L)) + 1.25F) / 2.3F,
                        1.0F);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(AbstractDungeon.ascensionLevel), floorX + 166.0F * Settings.scale, INFO_TEXT_Y, color);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(TopPanel.class, "ascensionHb");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
