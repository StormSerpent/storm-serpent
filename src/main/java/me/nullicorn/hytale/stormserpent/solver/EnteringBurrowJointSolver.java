package me.nullicorn.hytale.stormserpent.solver;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.solver.SerpentJointSolver;
import org.joml.Vector3d;

public final class EnteringBurrowJointSolver implements SerpentJointSolver {
    public static final String COMPONENT_ID = "StormSerpentEnteringBurrow";
    public static final BuilderCodec<EnteringBurrowJointSolver> CODEC = BuilderCodec.builder(EnteringBurrowJointSolver.class, EnteringBurrowJointSolver::new)
        .append(
            new KeyedCodec<>("Speed", Codec.DOUBLE, true),
            (o, s) -> o.speed = s,
            (o) -> o.speed
        )
        .addValidator(Validators.greaterThanOrEqual(0.0))
        .add()
        .build();

    private double speed;
    private int nextJoint;
    private Vector3d[] guideRail;
    private double[] jointDistances;

    public EnteringBurrowJointSolver() {
    }

    public EnteringBurrowJointSolver(final double speed) {
        this.speed = speed;
    }

    public double getDistance(final int jointIndex) {
        return this.jointDistances[jointIndex];
    }

    @Override
    public void init(final Serpent serpent) {
        final int jointCount = serpent.joints().size();
        this.guideRail = new Vector3d[jointCount];
        this.jointDistances = new double[jointCount];

        double distance = 0;
        for (int i = 0; i < jointCount; i++) {
            this.guideRail[i] = new Vector3d(serpent.joints().get(i).position());
            if (i > 0) {
                distance += this.guideRail[i - 1].distance(this.guideRail[i]);
            }
            this.jointDistances[i] = distance;
        }
    }

    @Override
    public void tick(
        final Serpent serpent,
        final Ref<EntityStore> serpentRef,
        final float dt,
        final ComponentAccessor<EntityStore> componentAccessor
    ) {
        assert this.guideRail != null && this.guideRail.length == serpent.joints().size();
        assert this.jointDistances != null && this.jointDistances.length == serpent.joints().size();

        for (int i = this.nextJoint; i < this.jointDistances.length; i++) {
            this.jointDistances[i] = Math.max(0, this.jointDistances[i] - (this.speed * dt));
            if (this.jointDistances[i] == 0) {
                this.nextJoint = i + 1;
            }
        }

        for (int i = this.nextJoint; i < serpent.joints().size(); i++) {
            double distanceLeft = this.jointDistances[i];

            int iGuideRail = 0;
            while (distanceLeft >= 0 && iGuideRail < this.guideRail.length - 1) {
                final double segmentLength = this.guideRail[iGuideRail].distance(this.guideRail[iGuideRail + 1]);
                if (distanceLeft <= segmentLength) {
                    final Vector3d newJointPosition;
                    if (segmentLength < 0.0001) {
                        newJointPosition = this.guideRail[iGuideRail];
                    } else {
                        newJointPosition = this.guideRail[iGuideRail].lerp(this.guideRail[iGuideRail + 1], distanceLeft / segmentLength, new Vector3d());
                    }
                    serpent.joints().get(i).position().set(newJointPosition);
                    break;
                }

                distanceLeft -= segmentLength;
                iGuideRail++;
            }
        }
    }

    @Override
    public SerpentJointSolver clone() {
        return new EnteringBurrowJointSolver(this.speed);
    }
}
