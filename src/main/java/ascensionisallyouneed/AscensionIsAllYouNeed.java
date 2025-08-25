package ascensionisallyouneed;

import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.util.ModConfigs;
import ascensionisallyouneed.util.ResourceLoader;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpireInitializer
public class AscensionIsAllYouNeed implements
        EditStringsSubscriber,
        PostInitializeSubscriber {
    public static final String MOD_ID = "ascensionisallyouneed";
    public static int maxAscension = 20;
    public static final List<AbstractAscension> ascensions = new ArrayList<>();
    public static final ModConfigs modConfigs = new ModConfigs();
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    private static final ResourceLoader resourceLoader = new ResourceLoader();

    public static String makeID(String id) {
        return MOD_ID + ':' + id;
    }

    public static void initialize() {
        BaseMod.subscribe(new AscensionIsAllYouNeed());
        modConfigs.initialize();
        logger.info("{} subscribed to BaseMod.", MOD_ID);
    }

    public static AbstractAscension getAscensionAboveA20(int level) {
        for (AbstractAscension ascension : ascensions) {
            if (ascension.getAscensionLevel() == level) {
                return ascension;
            }
        }
        return null;
    }

    private static void registerAscension(AbstractAscension ascension) {
        ascensions.add(ascension);
        ascensions.sort(Comparator.comparingInt(AbstractAscension::getAscensionLevel));
        maxAscension = Math.max(maxAscension, ascension.getAscensionLevel());
    }

    public static String makeAscensionInfo(int level, String description) {
        return level + ". " + (level == maxAscension ? AbstractAscension.TEXT[1] : "") + description;
    }

    public static String getAscensionLevelInfo(int level) {
        if (level == 0) {
            return "";
        }
        if (level >= 1 && level <= 19) {
            return CharacterSelectScreen.A_TEXT[level - 1];
        }
        if (level == 20) {
            return AbstractAscension.EXTRA_TEXT[0];
        }
        AbstractAscension ascension = getAscensionAboveA20(level);
        if (ascension == null) {
            return AbstractAscension.TEXT[0];
        }
        return makeAscensionInfo(ascension.getAscensionLevel(), ascension.getAscensionInfo());
    }

    @Override
    public void receivePostInitialize() {
        new AutoAdd(MOD_ID)
                .packageFilter("ascensionisallyouneed.ascensions")
                .any(AbstractAscension.class, (info, abstractAugment) -> registerAscension(abstractAugment));
        modConfigs.setupModPanel(resourceLoader.getTexture("badge.png"));
    }

    @Override
    public void receiveEditStrings() {
        ResourceLoader.loadStrings();
    }
}
