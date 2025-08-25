package ascensionisallyouneed.util;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.HashMap;
import java.util.Locale;

public class ResourceLoader {
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String RESOURCE_FOLDER = "ascensionisallyouneed";
    private final HashMap<String, Texture> textures = new HashMap<>(16);

    private static String getLangString() {
        return Settings.language.name().toLowerCase(Locale.getDefault());
    }

    private static String imagePath(String file) {
        return RESOURCE_FOLDER + "/images/" + file;
    }

    private static String localizationPath(String lang, String file) {
        return RESOURCE_FOLDER + "/localization/" + lang + '/' + file;
    }

    public static void loadStrings() {
        loadStrings(DEFAULT_LANGUAGE);
        if (!DEFAULT_LANGUAGE.equals(getLangString())) {
            loadStrings(getLangString());
        }
    }

    private static void loadStrings(String lang) {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    public Texture getTexture(String filePath) {
        return getTexture(filePath, true);
    }

    public Texture getTexture(String filePath, boolean linearFilter) {
        String imagePath = imagePath(filePath);
        Texture texture = textures.get(imagePath);
        if (texture == null) {
            try {
                texture = loadTexture(imagePath, linearFilter);
            } catch (GdxRuntimeException e) {
                AscensionIsAllYouNeed.logger.info("Failed to find texture {}", imagePath, e);
                return null;
            }
        }
        return texture;
    }

    private Texture loadTexture(String filePath, boolean linearFilter) {
        Texture texture = new Texture(filePath);
        if (linearFilter) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        } else {
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        }
        AscensionIsAllYouNeed.logger.info("Loaded texture {}", filePath);
        textures.put(filePath, texture);
        return texture;
    }
}
