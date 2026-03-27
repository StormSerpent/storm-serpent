package me.nullicorn.hytale.stormserpent.worldgen;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.RotatorDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

// TODO: Delete me when this bug is patched!
/**
 * Replaces {@link RotatorDensityAsset}, due to {@link RotatorDensity} being broken in 2026.03.26-92489d5e7.
 */
public class HotfixRotatorDensityAsset extends DensityAsset {
    public static final BuilderCodec<HotfixRotatorDensityAsset> CODEC = BuilderCodec.builder(
            HotfixRotatorDensityAsset.class, HotfixRotatorDensityAsset::new, DensityAsset.ABSTRACT_CODEC
        )
        .append(
            new KeyedCodec<>("NewYAxis", Vector3dUtil.CODEC, true),
            (t, k) -> t.newYAxis = k,
            (t) -> t.newYAxis
        )
        .add()
        .append(
            new KeyedCodec<>("SpinAngle", Codec.DOUBLE, true),
            (t, k) -> t.spinAngle = k,
            (t) -> t.spinAngle
        )
        .add()
        .build();

    private Vector3d newYAxis = new Vector3d();
    private double spinAngle;

    @Nonnull
    @Override
    public Density build(@Nonnull final DensityAsset.Argument argument) {
        return this.isSkipped() ? new ConstantValueDensity(0.0) : new HotfixRotatorDensity(this.buildFirstInput(argument), this.newYAxis, this.spinAngle);
    }
}
