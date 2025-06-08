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
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
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

import java.io.IOException;
import java.util.*;

@SpireInitializer
public class AscensionIsAllYouNeed implements
        EditStringsSubscriber,
        PostInitializeSubscriber {
    public static ModInfo info;
    private static final String defaultLanguage = "eng";
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static String modID;
    public static int maxAscension = 20;
    public static ArrayList<AbstractAscension> ascensions = new ArrayList<>();
    public static String FILE_NAME = "AscensionIsAllYouNeedConfig";
    public static SpireConfig ascensionIsAllYouNeedConfig;
    public static int ascensionStringRows = 20, rareCardProb = 5, additionalCardInElite = 0, additionalCardInBoss = 0;
    public static boolean loseEnergy = false;

    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static void initialize() {
        new AscensionIsAllYouNeed();
    }
    public static final Logger logger = LogManager.getLogger(modID);

    @Override
    public void receivePostInitialize() {
        new AutoAdd(modID)
                .packageFilter("ascensionisallyouneed.ascensions")
                .any(AbstractAscension.class, (info, abstractAugment) -> AscensionIsAllYouNeed.registerAscension(abstractAugment));
        initializeConfig();
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
        for (AbstractAscension ascension : ascensions) {
            if (ascension.getAscensionLevel() == level) {
                return makeAscensionInfo(ascension.getAscensionLevel(), ascension.getAscensionInfo());
            }
        }
        return AbstractAscension.TEXT[0];
    }

    public AscensionIsAllYouNeed() {
        BaseMod.subscribe(this);
        logger.info("{} subscribed to BaseMod.", modID);
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty("ascensionStringRows", String.valueOf(ascensionStringRows));
        defaultSettings.setProperty("rareCardProb", String.valueOf(rareCardProb));
        defaultSettings.setProperty("additionalCardInElite", String.valueOf(additionalCardInElite));
        defaultSettings.setProperty("additionalCardInBoss", String.valueOf(additionalCardInBoss));

        try {
            ascensionIsAllYouNeedConfig = new SpireConfig(modID, FILE_NAME, defaultSettings);
            ascensionStringRows = ascensionIsAllYouNeedConfig.getInt("ascensionStringRows");
            rareCardProb = ascensionIsAllYouNeedConfig.getInt("rareCardProb");
            additionalCardInElite = ascensionIsAllYouNeedConfig.getInt("additionalCardInElite");
            additionalCardInBoss = ascensionIsAllYouNeedConfig.getInt("additionalCardInBoss");
            loseEnergy = ascensionIsAllYouNeedConfig.getBool("loseEnergy");
        } catch (IOException e) {
            logger.error("AscensionIsAllYouNeed SpireConfig initialization failed:");
            e.printStackTrace();
        }
    }

    private static String checkResourcesPath() {
        String name = AscensionIsAllYouNeed.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        name = name.substring(0, name.indexOf('.'));

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);
        if (resources.child("images").exists() && resources.child("localization").exists()) {
            return name;
        }

        throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                "\tat the top of the " + AscensionIsAllYouNeed.class.getSimpleName() + " java file.");
    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase();
    }

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
        } else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    private void loadLocalization(String lang) {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }

    @Override
    public void receiveEditStrings() {
        loadLocalization(defaultLanguage);
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeConfig() {
        String[] SettingText = CardCrawlGame.languagePack.getUIString(AscensionIsAllYouNeed.makeID("Settings")).TEXT;
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        ModPanel settingsPanel = new ModPanel();

        int textID = 0;
        float yPos = Settings.HEIGHT * 0.5f / Settings.scale + 200.0f;
        ModLabeledToggleButton loseEnergyButton = new ModLabeledToggleButton(SettingText[textID++], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, ascensionIsAllYouNeedConfig.getBool("loseEnergy"), settingsPanel, (label) -> {
        }, (button) -> {
            ascensionIsAllYouNeedConfig.setBool("loseEnergy", button.enabled);
            loseEnergy = button.enabled;

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(loseEnergyButton);

        yPos -= 50.0f;
        ModLabel ascensionStringRowsLabel = new ModLabel(SettingText[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(ascensionStringRowsLabel);

        float ascensionStringRowsSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, SettingText[textID++], 1.0F / Settings.scale) + 40.0F;
        ModMinMaxSlider ascensionStringRowsSlider = new ModMinMaxSlider("", 350.0f + ascensionStringRowsSliderOffset, yPos + 7.0F, 0.0F, maxAscension,
                (float) ascensionIsAllYouNeedConfig.getInt("ascensionStringRows"), "%.0f", settingsPanel, (slider) -> {
            ascensionIsAllYouNeedConfig.setInt("ascensionStringRows", Math.round(slider.getValue()));
            ascensionStringRows = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(ascensionStringRowsSlider);

        yPos -= 50.0f;
        ModLabel rareCardProbLabel = new ModLabel(SettingText[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(rareCardProbLabel);

        float rareCardProbSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, SettingText[textID++], 1.0F / Settings.scale) + 40.0F;
        ModMinMaxSlider rareCardProbSlider = new ModMinMaxSlider("", 350.0f + rareCardProbSliderOffset, yPos + 7.0F, 1.0F, 5.0F,
                (float) ascensionIsAllYouNeedConfig.getInt("rareCardProb"), "%.0f%%", settingsPanel, (slider) -> {
            ascensionIsAllYouNeedConfig.setInt("rareCardProb", Math.round(slider.getValue()));
            rareCardProb = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(rareCardProbSlider);

        yPos -= 50.0f;
        ModLabel additionalCardInEliteLabel = new ModLabel(SettingText[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(additionalCardInEliteLabel);

        float additionalCardInEliteSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, SettingText[textID++], 1.0F / Settings.scale) + 40.0F;
        ModMinMaxSlider additionalCardInEliteSlider = new ModMinMaxSlider("", 350.0f + additionalCardInEliteSliderOffset, yPos + 7.0F, 0.0F, 2.0F,
                (float) ascensionIsAllYouNeedConfig.getInt("additionalCardInElite"), "%.0f", settingsPanel, (slider) -> {
            ascensionIsAllYouNeedConfig.setInt("additionalCardInElite", Math.round(slider.getValue()));
            additionalCardInElite = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(additionalCardInEliteSlider);

        yPos -= 50.0f;
        ModLabel additionalCardInBossLabel = new ModLabel(SettingText[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(additionalCardInBossLabel);

        float additionalCardInBossSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, SettingText[textID++], 1.0F / Settings.scale) + 40.0F;
        ModMinMaxSlider additionalCardInBossSlider = new ModMinMaxSlider("", 350.0f + additionalCardInBossSliderOffset, yPos + 7.0F, 0.0F, 2.0F,
                (float) ascensionIsAllYouNeedConfig.getInt("additionalCardInBoss"), "%.0f", settingsPanel, (slider) -> {
            ascensionIsAllYouNeedConfig.setInt("additionalCardInBoss", Math.round(slider.getValue()));
            additionalCardInBoss = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(additionalCardInBossSlider);

        yPos -= 80.0f;
        ModLabel warningLabel = new ModLabel(SettingText[textID++], 350.0f, yPos, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                (label) -> {
                });
        settingsPanel.addUIElement(warningLabel);

        yPos -= 80.0f;
        ModLabeledButton unlockAscension21Button = new ModLabeledButton(SettingText[textID++], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                (button) -> {
                    int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
                    if (newAscensionLevel <= 20) {
                        return;
                    }
                    for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                        character.loadPrefs();
                        if (character.getPrefs().getInteger("ASCENSION_LEVEL", 1) == 20
                                && character.getPrefs().getInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), 1) <= 20) {
                            character.getPrefs().putInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), 21);
                            character.getPrefs().putInteger(AscensionIsAllYouNeed.makeID("LAST_ASCENSION_LEVEL"), 21);
                            character.getPrefs().flush();
                        }
                    }
                });
        settingsPanel.addUIElement(unlockAscension21Button);

        yPos -= 80.0f;
        ModLabeledButton unlockHighestAscensionButton = new ModLabeledButton(SettingText[textID++], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
            (button) -> {
                int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
                for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                    character.loadPrefs();
                    character.getPrefs().putInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), newAscensionLevel);
                    character.getPrefs().flush();
                }
            });
        settingsPanel.addUIElement(unlockHighestAscensionButton);

        yPos -= 80.0f;
        ModLabeledButton removeHistoryButton = new ModLabeledButton(SettingText[textID++], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                (button) -> {
                    for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                        character.loadPrefs();
                        if (character.getPrefs().getInteger("ASCENSION_LEVEL", 1) > 20) {
                            character.getPrefs().putInteger("ASCENSION_LEVEL", 20);
                        }
                        character.getPrefs().putInteger("LAST_ASCENSION_LEVEL", 1);
                        character.getPrefs().data.remove(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"));
                        character.getPrefs().data.remove(AscensionIsAllYouNeed.makeID("LAST_ASCENSION_LEVEL"));
                        character.getPrefs().flush();
                    }
                });
        settingsPanel.addUIElement(removeHistoryButton);

        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, settingsPanel);
    }
}
