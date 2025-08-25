package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyChestTierRollAscension;
import ascensionisallyouneed.ascensions.ModifyPotionTierRollAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.AbstractPotion.PotionRarity;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class AbstractDungeonPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateMap"
    )
    public static class PostGenerateMap {
        @SpirePostfixPatch
        public static void Postfix() {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    ascension.modifyMap(AbstractDungeon.map);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnRandomRelicTier"
    )
    public static class InsertReturnRandomRelicTier {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll", "commonRelicChance", "uncommonRelicChance"}
        )
        public static SpireReturn<RelicTier> Insert(int roll, int commonRelicChance, int uncommonRelicChance) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRelicTierRollAscension) {
                    return SpireReturn.Return(((ModifyRelicTierRollAscension) ascension).modifyRelicTier(roll, commonRelicChance, uncommonRelicChance));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRandomChest"
    )
    public static class InsertGetRandomChest {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll", "smallChestChance", "mediumChestChance"}
        )
        public static SpireReturn<AbstractChest> Insert(int roll, int smallChestChance, int mediumChestChance) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyChestTierRollAscension) {
                    return SpireReturn.Return(((ModifyChestTierRollAscension) ascension).modifyChessTier(roll, smallChestChance, mediumChestChance));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomPotion", paramtypez = boolean.class)
    public static class InsertReturnRandomPotion {
        @SpireInsertPatch(locator = Locator.class, localvars = "roll")
        public static SpireReturn<AbstractPotion> Insert(boolean limited, int roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyPotionTierRollAscension) {
                    PotionRarity rarity = ((ModifyPotionTierRollAscension) ascension).modifyPotionTier(roll, PotionHelper.POTION_COMMON_CHANCE, PotionHelper.POTION_UNCOMMON_CHANCE);
                    return SpireReturn.Return(AbstractDungeon.returnRandomPotion(rarity, limited));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "rollRarity", paramtypez = Random.class)
    public static class InsertRollRarity {
        @SpireInsertPatch(locator = Locator.class, localvars = "roll")
        public static void Insert(Random rng, @ByRef int[] roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    roll[0] = ascension.modifyCardChance(roll[0]);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class InsertDungeonTransitionSetup {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert() {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    ascension.modifyStartingDeck(AbstractDungeon.player.masterDeck);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(CardCrawlGame.class, "playtime");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class InsertGetRewardCards {
        @SpireInsertPatch(locator = Locator.class, localvars = "numCards")
        public static void Insert(@ByRef int[] numCards) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    numCards[0] = ascension.modifyNumberOfCardsInReward(numCards[0]);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(ModHelper.class, "isModEnabled");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateRoomTypes"
    )
    public static class ModifyRoomChances {
        private static float dungeonShopRoomChance = 0.0F, dungeonRestRoomChance = 0.0F, dungeonEliteRoomChance = 0.0F, dungeonEventRoomChance = 0.0F;

        @SpirePrefixPatch
        public static void Prefix(ArrayList<AbstractRoom> roomList, int availableRoomCount,
                                  @ByRef float[] ___shopRoomChance, @ByRef float[] ___restRoomChance, @ByRef float[] ___eliteRoomChance, @ByRef float[] ___eventRoomChance) {
            dungeonShopRoomChance = ___shopRoomChance[0];
            dungeonRestRoomChance = ___restRoomChance[0];
            dungeonEliteRoomChance = ___eliteRoomChance[0];
            dungeonEventRoomChance = ___eventRoomChance[0];
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    ___shopRoomChance[0] = ascension.modifyShopRoomChance(___shopRoomChance[0]);
                    ___restRoomChance[0] = ascension.modifyRestRoomChance(___restRoomChance[0]);
                    ___eliteRoomChance[0] = ascension.modifyEliteRoomChance(___eliteRoomChance[0]);
                    ___eventRoomChance[0] = ascension.modifyEventRoomChance(___eventRoomChance[0]);
                }
            }
        }

        @SpirePostfixPatch
        public static void Postfix(ArrayList<AbstractRoom> roomList, int availableRoomCount,
                                   @ByRef float[] ___shopRoomChance, @ByRef float[] ___restRoomChance, @ByRef float[] ___eliteRoomChance, @ByRef float[] ___eventRoomChance) {
            ___shopRoomChance[0] = dungeonShopRoomChance;
            ___restRoomChance[0] = dungeonRestRoomChance;
            ___eliteRoomChance[0] = dungeonEliteRoomChance;
            ___eventRoomChance[0] = dungeonEventRoomChance;
        }
    }
}
