package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.DangerousEnemiesAscension;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.AscensionLevelUpTextEffect;

public class AscendersBanePatch {
    public static class LoseEnergyOnDrawnModifier extends AbstractCardModifier {
        @Override
        public void onDrawn(AbstractCard card) {
            if (card instanceof AscendersBane) {
                EnergyPanel.useEnergy(1);
            }
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new LoseEnergyOnDrawnModifier();
        }
    }

    @SpirePatch(
            clz = AscendersBane.class,
            method = "upgrade"
    )
    public static class PrefixUpgrade {
        public static final String ID = AscensionIsAllYouNeed.makeID("AscendersBane");
        public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

        @SpirePrefixPatch
        public static void Prefix(AscendersBane __instance) {
            if (!__instance.upgraded) {
                ReflectionHacks.privateMethod(AbstractCard.class, "upgradeName").invoke(__instance);
                __instance.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                __instance.initializeDescription();
                CardModifierManager.addModifier(__instance, new LoseEnergyOnDrawnModifier());
            }
        }
    }
}
