package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;

public class AbstractPlayerPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "initializeClass"
    )
    public static class PostInitializeClass {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel()) {
                    __instance.gold = ascension.modifyStartingGold(__instance, __instance.gold);
                    __instance.displayGold = __instance.gold;
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "preBattlePrep"
    )
    public static class InsertPreBattlePrep {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractPlayer __instance) {
            if (MapRoomNodePatch.IsBurningField.isBurning.get(AbstractDungeon.getCurrMapNode())) {
                AbstractRoom abstractRoom = AbstractDungeon.getCurrRoom();
                boolean hasByrd = false;
                for (AbstractMonster m : abstractRoom.monsters.monsters) {
                    if (m instanceof Byrd) {
                        hasByrd = true;
                        break;
                    }
                }
                int modifierIndex = AbstractDungeon.miscRng.random(hasByrd ? 1 : 0, 3);
                switch (modifierIndex) {
                    case 0:
                        for (AbstractMonster m : abstractRoom.monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, AbstractDungeon.actNum), AbstractDungeon.actNum));
                        }
                        break;
                    case 1:
                        for (AbstractMonster m : abstractRoom.monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction(m, 0.25F, true));
                        }
                        break;
                    case 2:
                        for (AbstractMonster m : abstractRoom.monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new MetallicizePower(m, AbstractDungeon.actNum * 2), AbstractDungeon.actNum * 2));
                        }
                        break;
                    case 3:
                        for (AbstractMonster m : abstractRoom.monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new RegenerateMonsterPower(m, AbstractDungeon.actNum * 2 - 1), AbstractDungeon.actNum * 2 - 1));
                        }
                        break;
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(MapRoomNode.class, "hasEmeraldKey");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
