package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;

public class StatsScreenPatch {
    @SpirePatch(
            clz = StatsScreen.class,
            method = "isPlayingHighestAscension"
    )
    public static class PrefixIsPlayingHighestAscension {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(Prefs p) {
            return SpireReturn.Return(AbstractDungeon.ascensionLevel ==
                    Math.max(p.getInteger("ASCENSION_LEVEL", 1),
                            p.getInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), 1)));
        }
    }
}
