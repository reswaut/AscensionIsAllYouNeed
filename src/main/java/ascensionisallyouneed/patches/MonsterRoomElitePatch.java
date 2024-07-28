package ascensionisallyouneed.patches;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.ascensions.ModifyRelicTierRollAscension;
import ascensionisallyouneed.ascensions.ModifyRestHealAmtAscension;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.FlameAnimationEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MonsterRoomElitePatch {
    @SpirePatch(
            clz = MonsterRoomElite.class,
            method = "returnRandomRelicTier"
    )
    public static class InsertReturnRandomRelicTier {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"roll"}
        )
        public static SpireReturn<AbstractRelic.RelicTier> Insert(MonsterRoomElite __instance, int roll) {
            int ascensionLevel = AbstractDungeon.ascensionLevel;
            for (AbstractAscension ascension : AscensionIsAllYouNeed.ascensions) {
                if (ascensionLevel >= ascension.getAscensionLevel() && ascension instanceof ModifyRelicTierRollAscension) {
                    return SpireReturn.Return(((ModifyRelicTierRollAscension) ascension).modifyRelicTier(roll, 50, 33));
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ModHelper.class, "isModEnabled");
                int[] tmp = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0] + 2};
            }
        }
    }
}
