package ascensionisallyouneed;

import ascensionisallyouneed.ascensions.AbstractAscension;
import ascensionisallyouneed.util.GeneralUtils;
import ascensionisallyouneed.util.TextureLoader;
import basemod.*;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.util.*;

@SpireInitializer
public class AscensionIsAllYouNeed implements
        EditStringsSubscriber,
        PostInitializeSubscriber {
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.
    public static int maxAscension = 20;
    public static ArrayList<AbstractAscension> ascensions = new ArrayList<>();
    private ModPanel settingsPanel;

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new AscensionIsAllYouNeed();
    }

    public AscensionIsAllYouNeed() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info("{} subscribed to BaseMod.", modID);
    }

    @Override
    public void receivePostInitialize() {
        initializeConfig();
        new AutoAdd(modID)
                .packageFilter("ascensionisallyouneed.ascensions")
                .any(AbstractAscension.class, (info, abstractAugment) -> AscensionIsAllYouNeed.registerAscension(abstractAugment));
    }

    private static void registerAscension(AbstractAscension ascension) {
        ascensions.add(ascension);
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
        for (AbstractAscension ascension : ascensions) {
            if (ascension.getAscensionLevel() == level) {
                return makeAscensionInfo(ascension.getAscensionLevel(), ascension.getAscensionInfo());
            }
        }
        return AbstractAscension.TEXT[0];
    }

    public void initializeConfig() {
        String[] SettingText = CardCrawlGame.languagePack.getUIString(AscensionIsAllYouNeed.makeID("Settings")).TEXT;
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        settingsPanel = new ModPanel();
        ModLabel warningLabel = new ModLabel(SettingText[0], 350.0f, 650.0f, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(warningLabel);
        ModLabeledButton unlockAscensionsButton = new ModLabeledButton(SettingText[1], 350.0f, 550.0f, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
            (button) -> {
                int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
                for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                    character.loadPrefs();
                    character.getPrefs().putInteger("ascensionisallyouneed:ASCENSION_LEVEL", newAscensionLevel);
                    character.getPrefs().flush();
                }
            });
        settingsPanel.addUIElement(unlockAscensionsButton);
        ModLabeledButton removeHistoryButton = new ModLabeledButton(SettingText[2], 350.0f, 450.0f, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                (button) -> {
                    for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                        character.loadPrefs();
                        if (character.getPrefs().getInteger("ASCENSION_LEVEL") > 20) {
                            character.getPrefs().putInteger("ASCENSION_LEVEL", 20);
                        }
                        character.getPrefs().putInteger("LAST_ASCENSION_LEVEL", 1);
                        character.getPrefs().data.remove("ascensionisallyouneed:ASCENSION_LEVEL");
                        character.getPrefs().data.remove("ascensionisallyouneed:LAST_ASCENSION_LEVEL");
                        character.getPrefs().flush();
                    }
                });
        settingsPanel.addUIElement(removeHistoryButton);
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, settingsPanel);
    }

    /*----------Localization----------*/

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng";

    @Override
    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage); //no exception catching for default localization; you better have at least one that works.
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = AscensionIsAllYouNeed.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);
        if (resources.child("images").exists() && resources.child("localization").exists()) {
            return name;
        }

        throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                "\tat the top of the " + AscensionIsAllYouNeed.class.getSimpleName() + " java file.");
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(AscensionIsAllYouNeed.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }
}
