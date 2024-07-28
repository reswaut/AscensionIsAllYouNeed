package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRestHealAmtAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import javassist.CtBehavior;

public class CampfireSleepEffectPatch {
    @SpirePatch(
            clz = CampfireSleepEffect.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class InsertCampfireSleepEffectConstructor {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"healAmount"}
        )
        public static void Insert(CampfireSleepEffect __instance, @ByRef int[] healAmount) {
            float healAmt;
            if (ModHelper.isModEnabled("Night Terrors")) {
                healAmt = AbstractDungeon.player.maxHealth;
            } else {
                healAmt = AbstractDungeon.player.maxHealth * 0.3F;
            }
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRestHealAmtAscension) {
                    healAmt = ((ModifyRestHealAmtAscension) ascension).modifyRestHealAmt(healAmt);
                }
            }
            healAmount[0] = (int) healAmt;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
