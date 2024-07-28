package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
