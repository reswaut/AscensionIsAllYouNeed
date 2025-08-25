package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRestHealAmtAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.daily.mods.NightTerrors;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CampfireSleepEffectPatch {
    @SpirePatch(
            clz = CampfireSleepEffect.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class InsertCampfireSleepEffectConstructor {
        @SpireInsertPatch(locator = Locator.class, localvars = "healAmount")
        public static void Insert(CampfireSleepEffect __instance, @ByRef int[] healAmount) {
            float healAmt;
            if (ModHelper.isModEnabled(NightTerrors.ID)) {
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
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
