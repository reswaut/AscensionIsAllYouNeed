package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.vfx.AscensionLevelUpTextEffect;
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
            for(int i = 1; i <= AbstractDungeon.ascensionLevel; ++i) {
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
}
