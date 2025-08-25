package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class AbstractRoomPatch {
    @SpirePatch(
            clz = AbstractRoom.class,
            method = "addGoldToRewards"
    )
    public static class PreAddGoldToRewards {
        @SpirePrefixPatch
        public static void Prefix(AbstractRoom __instance, @ByRef int[] gold) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    gold[0] = ascension.modifyGoldRewards(__instance, gold[0]);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "addPotionToRewards",
            paramtypez = {}
    )
    public static class InsertAddPotionToRewards {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = "chance"
        )
        public static void Insert(AbstractRoom __instance, @ByRef int[] chance) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    chance[0] = ascension.modifyPotionChance(__instance, chance[0]);
                }
            }
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
