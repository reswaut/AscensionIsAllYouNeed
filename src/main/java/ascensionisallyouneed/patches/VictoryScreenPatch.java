package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.vfx.AscensionLevelUpTextEffect;
import javassist.CtBehavior;

public class VictoryScreenPatch {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "updateAscensionAndBetaArtProgress"
    )
    public static class PrefixUpdateAscensionAndBetaArtProgress {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(VictoryScreen __instance) {
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if (AbstractDungeon.isAscensionMode && !Settings.seedSet && !Settings.isTrial && AbstractDungeon.ascensionLevel < newAscensionLevel && StatsScreen.isPlayingHighestAscension(AbstractDungeon.player.getPrefs())) {
                StatsScreen.incrementAscension(AbstractDungeon.player.getCharStat());
                AbstractDungeon.topLevelEffects.add(new AscensionLevelUpTextEffect());
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
