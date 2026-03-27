package me.nullicorn.hytale.stormserpent.worldgen;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: Delete me when this bug is patched!

/**
 * Replaces {@link RotatorDensity}, which is broken in 2026.03.26-92489d5e7.
 */
public class HotfixRotatorDensity extends Density {
    @Nonnull
    private static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);

    @Nullable
    private Density input;
    private Vector3d tiltAxis;
    private double tiltAngle;
    private final double spinAngle;
    @Nonnull
    private final SpecialCase axisSpecialCase;
    @Nonnull
    private final Vector3d rChildPosition;
    @Nonnull
    private final Density.Context rChildContext;

    public HotfixRotatorDensity(@Nullable final Density input, final Vector3d newYAxis, final double spinAngle) {
        this.input = input;
        this.spinAngle = spinAngle * Math.PI / 180.0;

        final Vector3d rotationAxis = newYAxis.cross(Y_AXIS, new Vector3d());
        if (rotationAxis.length() < 0.00000001) {
            rotationAxis.set(Y_AXIS);
            if (newYAxis.dot(Y_AXIS) < 0.0) {
                this.axisSpecialCase = SpecialCase.INVERTED_Y_AXIS;
            } else {
                this.axisSpecialCase = SpecialCase.Y_AXIS;
            }
        } else {
            this.axisSpecialCase = SpecialCase.NONE;
        }
        rotationAxis.normalize();

        if (this.axisSpecialCase == SpecialCase.INVERTED_Y_AXIS || this.axisSpecialCase == SpecialCase.Y_AXIS) {
            this.tiltAxis = new Vector3d();
            this.tiltAngle = 0.0;
        }

        this.tiltAxis = Y_AXIS.cross(newYAxis, new Vector3d());
        this.tiltAngle = Math.acos(newYAxis.dot(Y_AXIS) / (newYAxis.length() * Y_AXIS.length()));
        this.rChildPosition = new Vector3d();
        this.rChildContext = new Density.Context();
    }

    @Override
    public double process(@Nonnull final Density.Context context) {
        if (this.input == null) {
            return 0.0;
        } else {
            this.rChildPosition.set(context.position);
            switch (this.axisSpecialCase) {
                case INVERTED_Y_AXIS:
                    this.rChildPosition.mul(-1.0);
                case NONE:
                    VectorUtil.rotateAroundAxis(this.rChildPosition, this.tiltAxis, this.tiltAngle);
                case Y_AXIS:
                default:
                    VectorUtil.rotateAroundAxis(this.rChildPosition, Y_AXIS, this.spinAngle);
                    this.rChildContext.assign(context);
                    this.rChildContext.position = this.rChildPosition;
                    return this.input.process(this.rChildContext);
            }
        }
    }

    @Override
    public void setInputs(@Nonnull final Density[] inputs) {
        if (inputs.length == 0) {
            this.input = null;
        }

        this.input = inputs[0];
    }

    private enum SpecialCase {
        NONE,
        Y_AXIS,
        INVERTED_Y_AXIS
    }
}
