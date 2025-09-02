package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;

public class AscendersBanePatch {
    public static class UpgradeAscendersBaneModifier extends AbstractCardModifier {
        @Override
        public void onDrawn(AbstractCard card) {
            if (card instanceof AscendersBane && AscensionIsAllYouNeed.modConfigs.legacyA30) {
                addToBot(new LoseEnergyAction(1));
            }
        }

        @Override
        public void onExhausted(AbstractCard card) {
            if (card instanceof AscendersBane && !AscensionIsAllYouNeed.modConfigs.legacyA30) {
                addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 1));
            }
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new UpgradeAscendersBaneModifier();
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
                if (AscensionIsAllYouNeed.modConfigs.legacyA30) {
                    __instance.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                } else {
                    __instance.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                }
                __instance.initializeDescription();
                CardModifierManager.addModifier(__instance, new UpgradeAscendersBaneModifier());
            }
        }
    }
}
