package me.nullicorn.hytale.wip.npc;

import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import me.nullicorn.serpentine.asset.SerpentConfig;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public final class SerpentConfigExistsValidator extends AssetValidator {
    private static final SerpentConfigExistsValidator DEFAULT_INSTANCE = new SerpentConfigExistsValidator();

    public static SerpentConfigExistsValidator required() {
        return DEFAULT_INSTANCE;
    }

    @Nonnull
    public static SerpentConfigExistsValidator withConfig(final EnumSet<Config> config) {
        return new SerpentConfigExistsValidator(config);
    }

    private SerpentConfigExistsValidator() {
    }

    private SerpentConfigExistsValidator(final EnumSet<AssetValidator.Config> config) {
        super(config);
    }

    @Override
    public String getDomain() {
        return "SerpentConfig";
    }

    @Override
    public boolean test(final String serpentConfig) {
        return SerpentConfig.getAssetMap().getAsset(serpentConfig) != null;
    }

    @Override
    public String errorMessage(final String serpentConfig, final String attributeName) {
        return "The serpent config with the name \"" + serpentConfig + "\" does not exist for attribute \"" + attributeName + "\"";// 38
    }

    @Override
    public String getAssetName() {
        return "SerpentConfig";
    }
}
