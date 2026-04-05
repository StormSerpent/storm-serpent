package me.nullicorn.hytale.stormserpent.ui.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class StormSerpentBossBarHud extends CustomUIHud {
    private static final String UI_DOCUMENT_PATH = "Hud/Boss/Serpent_Storm/BossBar.ui";
    private static final String UI_BAR_VALUE_SELECTOR = "#BossBar.Value";

    private static final float INTERPOLATION_THRESHOLD = 0.001f;

    private float currentValue = 1.0f;
    private float targetValue = 1.0f;
    @Nullable
    private final Interpolator interpolator;

    public StormSerpentBossBarHud(
        @Nonnull final PlayerRef playerRef,
        @Nonnull final String key,
        final int zOrder,
        @Nullable final Interpolator interpolator
    ) {
        super(playerRef, key, zOrder);
        this.interpolator = interpolator;
    }

    public void set(final float value) {
        this.targetValue = Math.clamp(value, 0, 1);
    }

    public void tick(final float dt) {
        if (this.interpolator == null) {
            if (this.currentValue == this.targetValue) {
                return;
            }
            this.currentValue = this.targetValue;
        } else {
            if (Math.abs(this.targetValue - this.currentValue) <= INTERPOLATION_THRESHOLD) {
                return;
            }
            this.currentValue = this.interpolator.interpolate(this.currentValue, this.targetValue, dt);
            this.currentValue = Math.clamp(this.currentValue, 0, 1);
        }
        this.update(false, new UICommandBuilder());
    }

    @Override
    protected void build(@Nonnull final UICommandBuilder commandBuilder) {
        commandBuilder.append(UI_DOCUMENT_PATH);
    }

    @Override
    public void update(final boolean clear, @Nonnull final UICommandBuilder commandBuilder) {
        commandBuilder.set(UI_BAR_VALUE_SELECTOR, this.currentValue);

        super.update(clear, commandBuilder);
    }

    @FunctionalInterface
    public interface Interpolator {
        float interpolate(float from, float to, float dt);
    }
}
