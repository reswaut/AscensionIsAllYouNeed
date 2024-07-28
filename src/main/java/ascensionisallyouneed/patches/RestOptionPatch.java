package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRestHealAmtAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import javassist.CtBehavior;

public class RestOptionPatch {
    @SpirePatch(
            clz = RestOption.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class InsertRestOptionConstructor {
        @SpireInsertPatch(
                locator = Locator1.class,
                localvars = {"healAmt"}
        )
        public static void Insert1(RestOption __instance, @ByRef int[] healAmt) {
            float healAmount;
            if (ModHelper.isModEnabled("Night Terrors")) {
                healAmount = AbstractDungeon.player.maxHealth;
            } else {
                healAmount = AbstractDungeon.player.maxHealth * 0.3F;
            }
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRestHealAmtAscension) {
                    healAmount = ((ModifyRestHealAmtAscension) ascension).modifyRestHealAmt(healAmount);
                }
            }
            healAmt[0] = (int) healAmount;
        }

        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasBlight");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class,
                localvars = {"healAmt", "description"}
        )
        public static void Insert2(RestOption __instance, int healAmt, @ByRef String[] description) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRestHealAmtAscension) {
                    description[0] = ((ModifyRestHealAmtAscension) ascension).getRestOptionDescription(true, healAmt);
                    return;
                }
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0]};
            }
        }



        @SpireInsertPatch(
                locator = Locator3.class,
                localvars = {"healAmt", "description"}
        )
        public static void Insert3(RestOption __instance, int healAmt, @ByRef String[] description) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRestHealAmtAscension) {
                    description[0] = ((ModifyRestHealAmtAscension) ascension).getRestOptionDescription(false, healAmt);
                    return;
                }
            }
        }

        private static class Locator3 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[1]};
            }
        }
    }
}
