package ascensionisallyouneed.util;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import basemod.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.util.Properties;

public class ModConfigs {
    private static final String ID = AscensionIsAllYouNeed.makeID(ModConfigs.class.getSimpleName());
    private static final String FILE_NAME = "AscensionIsAllYouNeedConfig";
    private static final String ASCENSION_STRING_ROWS = "ascensionStringRows";
    private static final String RARE_CARD_PROB = "rareCardProb";
    private static final String ADDITIONAL_ELITE_CARDS = "additionalCardInElite";
    private static final String ADDITIONAL_BOSS_CARDS = "additionalCardInBoss";
    private static final String LEGACY_A30 = "loseEnergy";
    public SpireConfig ascensionIsAllYouNeedConfig = null;
    public int ascensionStringRows = 20, rareCardProb = 5, additionalCardInElite = 0, additionalCardInBoss = 0;
    public boolean legacyA30 = false;

    public void initialize() {
        Properties defaultSettings = getDefaultSettings();

        try {
            ascensionIsAllYouNeedConfig = new SpireConfig(AscensionIsAllYouNeed.MOD_ID, FILE_NAME, defaultSettings);
            ascensionStringRows = ascensionIsAllYouNeedConfig.getInt(ASCENSION_STRING_ROWS);
            rareCardProb = ascensionIsAllYouNeedConfig.getInt(RARE_CARD_PROB);
            additionalCardInElite = ascensionIsAllYouNeedConfig.getInt(ADDITIONAL_ELITE_CARDS);
            additionalCardInBoss = ascensionIsAllYouNeedConfig.getInt(ADDITIONAL_BOSS_CARDS);
            legacyA30 = ascensionIsAllYouNeedConfig.getBool(LEGACY_A30);
        } catch (IOException e) {
            AscensionIsAllYouNeed.logger.error("AscensionIsAllYouNeed SpireConfig initialization failed.", e);
        }
    }

    private Properties getDefaultSettings() {
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(ASCENSION_STRING_ROWS, String.valueOf(ascensionStringRows));
        defaultSettings.setProperty(RARE_CARD_PROB, String.valueOf(rareCardProb));
        defaultSettings.setProperty(ADDITIONAL_ELITE_CARDS, String.valueOf(additionalCardInElite));
        defaultSettings.setProperty(ADDITIONAL_BOSS_CARDS, String.valueOf(additionalCardInBoss));
        defaultSettings.setProperty(LEGACY_A30, String.valueOf(legacyA30));
        return defaultSettings;
    }

    public void setupModPanel(Texture badgeTexture) {
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        String[] TEXT = uiStrings.TEXT;
        String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        ModPanel settingsPanel = new ModPanel();

        int textID = 0;
        float yPos = Settings.HEIGHT * 0.5f / Settings.scale + 200.0f;
        IUIElement loseEnergyButton = new ModLabeledToggleButton(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, ascensionIsAllYouNeedConfig.getBool(LEGACY_A30), settingsPanel, label -> {
        }, button -> {
            ascensionIsAllYouNeedConfig.setBool(LEGACY_A30, button.enabled);
            legacyA30 = button.enabled;

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        textID++;
        settingsPanel.addUIElement(loseEnergyButton);

        yPos -= 50.0f;
        IUIElement ascensionStringRowsLabel = new ModLabel(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                label -> {
                });
        settingsPanel.addUIElement(ascensionStringRowsLabel);

        float ascensionStringRowsSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, TEXT[textID], 1.0F / Settings.scale) + 40.0F;
        textID++;
        IUIElement ascensionStringRowsSlider = new ModMinMaxSlider("", 350.0f + ascensionStringRowsSliderOffset, yPos + 7.0F, 0.0F, AscensionIsAllYouNeed.maxAscension,
                ascensionIsAllYouNeedConfig.getInt(ASCENSION_STRING_ROWS), "%.0f", settingsPanel, slider -> {
            ascensionIsAllYouNeedConfig.setInt(ASCENSION_STRING_ROWS, Math.round(slider.getValue()));
            ascensionStringRows = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(ascensionStringRowsSlider);

        yPos -= 50.0f;
        IUIElement rareCardProbLabel = new ModLabel(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                label -> {
                });
        settingsPanel.addUIElement(rareCardProbLabel);

        float rareCardProbSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, TEXT[textID], 1.0F / Settings.scale) + 40.0F;
        textID++;
        IUIElement rareCardProbSlider = new ModMinMaxSlider("", 350.0f + rareCardProbSliderOffset, yPos + 7.0F, 1.0F, 5.0F,
                ascensionIsAllYouNeedConfig.getInt(RARE_CARD_PROB), "%.0f%%", settingsPanel, slider -> {
            ascensionIsAllYouNeedConfig.setInt(RARE_CARD_PROB, Math.round(slider.getValue()));
            rareCardProb = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(rareCardProbSlider);

        yPos -= 50.0f;
        IUIElement additionalCardInEliteLabel = new ModLabel(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                label -> {
                });
        settingsPanel.addUIElement(additionalCardInEliteLabel);

        float additionalCardInEliteSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, TEXT[textID], 1.0F / Settings.scale) + 40.0F;
        textID++;
        IUIElement additionalCardInEliteSlider = new ModMinMaxSlider("", 350.0f + additionalCardInEliteSliderOffset, yPos + 7.0F, 0.0F, 2.0F,
                ascensionIsAllYouNeedConfig.getInt(ADDITIONAL_ELITE_CARDS), "%.0f", settingsPanel, slider -> {
            ascensionIsAllYouNeedConfig.setInt(ADDITIONAL_ELITE_CARDS, Math.round(slider.getValue()));
            additionalCardInElite = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(additionalCardInEliteSlider);

        yPos -= 50.0f;
        IUIElement additionalCardInBossLabel = new ModLabel(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel,
                label -> {
                });
        settingsPanel.addUIElement(additionalCardInBossLabel);

        float additionalCardInBossSliderOffset = FontHelper.getWidth(FontHelper.charDescFont, TEXT[textID], 1.0F / Settings.scale) + 40.0F;
        textID++;
        IUIElement additionalCardInBossSlider = new ModMinMaxSlider("", 350.0f + additionalCardInBossSliderOffset, yPos + 7.0F, 0.0F, 2.0F,
                ascensionIsAllYouNeedConfig.getInt(ADDITIONAL_BOSS_CARDS), "%.0f", settingsPanel, slider -> {
            ascensionIsAllYouNeedConfig.setInt(ADDITIONAL_BOSS_CARDS, Math.round(slider.getValue()));
            additionalCardInBoss = Math.round(slider.getValue());

            try {
                ascensionIsAllYouNeedConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(additionalCardInBossSlider);

        yPos -= 80.0f;
        IUIElement warningLabel = new ModLabel(TEXT[textID], 350.0f, yPos, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                label -> {
                });
        textID++;
        settingsPanel.addUIElement(warningLabel);

        yPos -= 80.0f;
        IUIElement unlockAscension21Button = new ModLabeledButton(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                button -> {
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
        textID++;
        settingsPanel.addUIElement(unlockAscension21Button);

        yPos -= 80.0f;
        IUIElement unlockHighestAscensionButton = new ModLabeledButton(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                button -> {
                    int newAscensionLevel = AscensionIsAllYouNeed.maxAscension;
                    for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                        character.loadPrefs();
                        character.getPrefs().putInteger(AscensionIsAllYouNeed.makeID("ASCENSION_LEVEL"), newAscensionLevel);
                        character.getPrefs().flush();
                    }
                });
        textID++;
        settingsPanel.addUIElement(unlockHighestAscensionButton);

        yPos -= 80.0f;
        IUIElement removeHistoryButton = new ModLabeledButton(TEXT[textID], 350.0f, yPos, Settings.CREAM_COLOR, Settings.RED_TEXT_COLOR, FontHelper.charDescFont, settingsPanel,
                button -> {
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
        textID++;
        settingsPanel.addUIElement(removeHistoryButton);

        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], EXTRA_TEXT[1], EXTRA_TEXT[2], settingsPanel);
    }
}
