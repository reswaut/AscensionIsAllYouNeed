package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlightyAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(BlightyAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private static final List<String> easyBlights, hardBlights;

    static {
        easyBlights = new ArrayList<>(5);
        easyBlights.add(Accursed.ID);
        easyBlights.add(AncientAugmentation.ID);
        easyBlights.add(Durian.ID);
        easyBlights.add(Hauntings.ID);
        easyBlights.add(TimeMaze.ID);

        hardBlights = new ArrayList<>(4);
        hardBlights.add(GrotesqueTrophy.ID);
        hardBlights.add(Scatterbrain.ID);
        hardBlights.add(TwistingMind.ID);
        hardBlights.add(VoidEssence.ID);
    }

    private static boolean noBlight() {
        return AbstractDungeon.actNum != 3 || AscensionIsAllYouNeed.ascensions.stream().noneMatch(ascension -> ascension.getAscensionLevel() >= AbstractDungeon.ascensionLevel && ascension instanceof BlightyAscension);
    }

    @Override
    public int getAscensionLevel() {
        return 32;
    }

    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public Color getColor() {
        return getChangingColor();
    }

    @SpirePatch(
            clz = BossChest.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class BossChestPostfixConstructor {
        @SpirePostfixPatch
        public static void Postfix(BossChest __instance) {
            if (noBlight()) {
                return;
            }
            int first = AbstractDungeon.relicRng.random(4);
            int second = AbstractDungeon.relicRng.random(3);
            if (second >= first) {
                second += 1;
            }
            Collection<String> blights = new ArrayList<>(3);
            blights.add(hardBlights.get(AbstractDungeon.relicRng.random(3)));
            blights.add(easyBlights.get(first));
            blights.add(easyBlights.get(second));
            __instance.blights.clear();
            for (String s : blights) {
                __instance.blights.add(BlightHelper.getBlight(s));
            }
        }
    }

    @SpirePatch(
            clz = BossChest.class,
            method = "open"
    )
    public static class BossChestPrefixOpen {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(BossChest __instance, boolean bossChest) {
            if (noBlight()) {
                return SpireReturn.Continue();
            }
            CardCrawlGame.sound.play("CHEST_OPEN");
            AbstractDungeon.bossRelicScreen.openBlight(__instance.blights);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "openBlight"
    )
    public static class BossRelicSelectScreenInsertOpenBlight {
        @SpireInsertPatch(
                locator = Locater.class
        )
        public static SpireReturn<Void> Insert(BossRelicSelectScreen __instance, ArrayList<AbstractBlight> chosenBlights, float ___SLOT_1_X, float ___SLOT_1_Y, float ___SLOT_2_X, float ___SLOT_2_Y, float ___SLOT_3_X) {
            if (noBlight()) {
                return SpireReturn.Continue();
            }
            __instance.relics.clear();
            AbstractBlight blight1 = chosenBlights.get(0);
            blight1.spawn(___SLOT_1_X, ___SLOT_1_Y);
            blight1.hb.move(blight1.currentX, blight1.currentY);
            __instance.blights.add(blight1);
            AbstractBlight blight2 = chosenBlights.get(1);
            blight2.spawn(___SLOT_2_X, ___SLOT_2_Y);
            blight2.hb.move(blight2.currentX, blight2.currentY);
            __instance.blights.add(blight2);
            AbstractBlight blight3 = chosenBlights.get(2);
            blight3.spawn(___SLOT_3_X, ___SLOT_2_Y);
            blight3.hb.move(blight3.currentX, blight3.currentY);
            __instance.blights.add(blight3);
            return SpireReturn.Return();
        }

        private static class Locater extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(OverlayMenu.class, "showBlackScreen");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "update"
    )
    public static class BossRelicSelectScreenInsertUpdate {
        @SpireInsertPatch(
                locator = Locater.class
        )
        public static void Insert(BossRelicSelectScreen __instance) {
            if (noBlight()) {
                return;
            }
            for (AbstractBlight b : __instance.blights) {
                b.update();
                if (b.isObtained) {
                    ReflectionHacks.privateMethod(BossRelicSelectScreen.class, "blightObtainLogic", AbstractBlight.class).invoke(__instance, b);
                }
            }
        }

        private static class Locater extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(BossRelicSelectScreen.class, "updateControllerInput");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = ProceedButton.class,
            method = "goToDoubleBoss"
    )
    public static class ProceedButtonPrefixGoToDoubleBoss {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ProceedButton __instance) {
            if (noBlight()) {
                return SpireReturn.Continue();
            }
            if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
                return SpireReturn.Continue();
            }
            ReflectionHacks.privateMethod(ProceedButton.class, "goToTreasureRoom").invoke(__instance);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = ProceedButton.class,
            method = "goToNextDungeon"
    )
    public static class ProceedButtonPrefixGoToNextDungeon {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ProceedButton __instance) {
            if (noBlight()) {
                return SpireReturn.Continue();
            }
            if (!(AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss)) {
                return SpireReturn.Continue();
            }
            ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = TreasureRoomBoss.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class TreasureRoomBossPostfixConstructor {
        @SpirePostfixPatch
        public static void Postfix(TreasureRoomBoss __instance) {
            if (noBlight()) {
                return;
            }
            __instance.phase = RoomPhase.INCOMPLETE;
        }
    }
}
