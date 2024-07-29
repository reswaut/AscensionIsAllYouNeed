package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class AscendersBanePatch {
    public static class LoseEnergyOnDrawnModifier extends AbstractCardModifier {
        @Override
        public void onDrawn(AbstractCard card) {
            if (card instanceof AscendersBane) {
                this.addToBot(new LoseEnergyAction(1));
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
