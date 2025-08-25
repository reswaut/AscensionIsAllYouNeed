package ascensionisallyouneed.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.vfx.FlameAnimationEffect;

import java.util.ArrayList;

public class MapRoomNodePatch {
    @SpirePatch(
            clz = MapRoomNode.class,
            method = "<class>"
    )
    public static class FEffectsField {
        public static final SpireField<ArrayList<FlameAnimationEffect>> fEffects = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "<class>"
    )
    public static class FlameVfxTimerField {
        public static final SpireField<Float> flameVfxTimer = new SpireField<>(() -> 0.0F);
    }

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "<class>"
    )
    public static class IsBurningField {
        public static final SpireField<Boolean> isBurning = new SpireField<>(() -> false);
    }

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "render"
    )
    public static class PreRender {
        @SpirePrefixPatch
        public static void Prefix(MapRoomNode __instance, SpriteBatch sb) {
            if (IsBurningField.isBurning.get(__instance)) {
                for (FlameAnimationEffect e : FEffectsField.fEffects.get(__instance)) {
                    e.render(sb, ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale"));
                }
            }
        }
    }

    @SpirePatch(
            clz = MapRoomNode.class,
            method = "update"
    )
    public static class PreUpdate {
        @SpirePrefixPatch
        public static void Prefix(MapRoomNode __instance) {
            if (IsBurningField.isBurning.get(__instance)) {
                FlameVfxTimerField.flameVfxTimer.set(__instance, FlameVfxTimerField.flameVfxTimer.get(__instance) - Gdx.graphics.getDeltaTime());
                if (FlameVfxTimerField.flameVfxTimer.get(__instance) < 0.0F) {
                    FlameVfxTimerField.flameVfxTimer.set(__instance, MathUtils.random(0.2F, 0.4F));
                    FEffectsField.fEffects.get(__instance).add(new FlameAnimationEffect(__instance.hb));
                }
                for (FlameAnimationEffect e : FEffectsField.fEffects.get(__instance)) {
                    if (e.isDone) {
                        e.dispose();
                    }
                }
                FEffectsField.fEffects.get(__instance).removeIf(e -> e.isDone);
                for (FlameAnimationEffect e : FEffectsField.fEffects.get(__instance)) {
                    e.update();
                }
            }
        }
    }
}
