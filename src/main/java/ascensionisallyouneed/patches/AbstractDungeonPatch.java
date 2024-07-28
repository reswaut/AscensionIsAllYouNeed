package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyChestTierRollAscension;
import ascensionisallyouneed.ascensions.ModifyPotionTierRollAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import javassist.CtBehavior;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.returnRandomPotion;

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
        public static SpireReturn<AbstractRelic.RelicTier> Insert(int roll, int commonRelicChance, int uncommonRelicChance) {
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnRandomPotion",
            paramtypez = {boolean.class}
    )
    public static class InsertReturnRandomPotion {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll"}
        )
        public static SpireReturn<AbstractPotion> Insert(boolean limited, int roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyPotionTierRollAscension) {
                    AbstractPotion.PotionRarity rarity = ((ModifyPotionTierRollAscension) ascension).modifyPotionTier(roll, PotionHelper.POTION_COMMON_CHANCE, PotionHelper.POTION_UNCOMMON_CHANCE);
                    return SpireReturn.Return(returnRandomPotion(rarity, limited));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "rollRarity",
            paramtypez = {Random.class}
    )
    public static class InsertRollRarity {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll"}
        )
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "playtime");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
