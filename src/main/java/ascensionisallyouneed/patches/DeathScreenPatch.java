package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.vfx.AscensionLevelUpTextEffect;

public class DeathScreenPatch {
    @SpirePatch(
            clz = DeathScreen.class,
            method = "updateAscensionProgress"
    )
    public static class PrefixUpdateAscensionProgress {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DeathScreen __instance) {
            int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
            if ((GameOverScreen.isVictory || AbstractDungeon.actNum >= 4) && AbstractDungeon.isAscensionMode && Settings.isStandardRun() && AbstractDungeon.ascensionLevel < newAscensionLevel && StatsScreen.isPlayingHighestAscension(AbstractDungeon.player.getPrefs())) {
                StatsScreen.incrementAscension(AbstractDungeon.player.getCharStat());
                AbstractDungeon.topLevelEffects.add(new AscensionLevelUpTextEffect());
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
