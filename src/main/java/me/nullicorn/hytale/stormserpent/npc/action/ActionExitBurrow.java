package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionExitBurrow;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.solver.DefaultSerpentJointSolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActionExitBurrow extends ActionBase {
    private final int targetSlot;
    private final double[] distanceRange;

    public ActionExitBurrow(final BuilderActionExitBurrow builder, final BuilderSupport support) {
        super(builder);
        this.targetSlot = builder.getTargetSlot(support);
        this.distanceRange = builder.getDistanceRange(support);
    }

    @Override
    public boolean canExecute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.canExecute(ref, role, sensorInfo, dt, store)) {
            return false;
        }

        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        return stormSerpent != null && stormSerpent.burrowStatus == StormSerpent.BurrowStatus.IN_BURROW;
    }

    @Override
    public boolean execute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        super.execute(ref, role, sensorInfo, dt, store);

        final Serpent serpent = store.getComponent(ref, Serpent.getComponentType());
        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        final TransformComponent serpentTransform = store.getComponent(ref, TransformComponent.getComponentType());
        final HeadRotation serpentHeadRotation = store.getComponent(ref, HeadRotation.getComponentType());
        assert serpent != null && stormSerpent != null && serpentTransform != null && serpentHeadRotation != null;

        Ref<EntityStore> targetRef = role.getMarkedEntitySupport().getMarkedEntityRef(this.targetSlot);
        if (targetRef == null || !ref.isValid()) {
            targetRef = ref;
        }
        final TransformComponent targetTransform = store.getComponent(targetRef, TransformComponent.getComponentType());
        assert targetTransform != null;

        final double angle = Math.random() * Math.TAU;
        final double distance = MathUtil.randomDouble(this.distanceRange[0], this.distanceRange[1]);
        final double x = targetTransform.getPosition().x + Math.cos(angle) * distance;
        final double z = targetTransform.getPosition().z + Math.sin(angle) * distance;
        final double y = Math.max(0, getTerrainHeight(x, z, store.getExternalData().getWorld()) + 10);

        serpentTransform.getPosition().set(x, y, z);
        // Look straight up.
        serpentHeadRotation.getRotation().setPitch((float) (Math.PI / 2));
        // Look toward the target.
        serpentHeadRotation.getRotation().setYaw(NPCPhysicsMath.headingFromDirection(targetTransform.getPosition().x - x, targetTransform.getPosition().z - z, 0.0f));

        double jointY = y + serpent.bones().getFirst().baseLength() * serpent.bones().getFirst().scale() * 0.5;
        for (int i = 0; i < serpent.joints().size(); i++) {
            serpent.joints().get(i).position().set(x, jointY, z);
            if (i < serpent.bones().size()) {
                jointY -= serpent.bones().get(i).baseLength() * serpent.bones().get(i).scale();
            }
        }

        for (final Serpent.Bone bone : serpent.bones()) {
            bone.setAutoSpawn(true);
        }

        // Remove the override scale from when we entered the burrow.
        store.tryRemoveComponent(ref, EntityScaleComponent.getComponentType());
        // We also need to reset the model to apply its original scaling.
        final var modelRef = serpent.bones().getFirst().model();
        if (modelRef != null) {
            final var model = modelRef.toModel();
            if (model != null) {
                store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
            }
        }

        serpent.setJointSolver(new DefaultSerpentJointSolver());
        stormSerpent.burrowStatus = StormSerpent.BurrowStatus.NOT_IN_BURROW;
        return true;
    }

    private static short getTerrainHeight(final double x, final double z, final World world) {
        final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
        if (chunk == null) {
            return 0;
        }
        final BlockChunk blockChunk = chunk.getBlockChunk();
        if (blockChunk == null) {
            return 0;
        }
        return blockChunk.getHeight(MathUtil.floor(x), MathUtil.floor(z));
    }
}
