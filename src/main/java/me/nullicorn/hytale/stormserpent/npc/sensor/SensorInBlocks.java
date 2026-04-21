package me.nullicorn.hytale.stormserpent.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorInBlocks;
import org.joml.Vector3d;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SensorInBlocks extends SensorBase {
    // TODO: This is really inefficient.
    // TODO: Account for rotation.

    private static final double THRESHOLD_EPSILON = 0.001;

    private final double threshold;

    public SensorInBlocks(final BuilderSensorInBlocks builder, final BuilderSupport support) {
        super(builder);
        this.threshold = builder.getThreshold(support);
    }

    @Nullable
    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }

    @Override
    public boolean matches(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.matches(ref, role, dt, store)) {
            return false;
        }

        final World world = store.getExternalData().getWorld();

        final TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        final BoundingBox boundingBoxComponent = store.getComponent(ref, BoundingBox.getComponentType());
        if (boundingBoxComponent == null) {
            return false;
        }

        final EntityScaleComponent entityScaleComponent = store.getComponent(ref, EntityScaleComponent.getComponentType());
        final float scale;
        if (entityScaleComponent != null) {
            scale = entityScaleComponent.getScale();
        } else {
            scale = 1.0f;
        }

        final Box boundingBox = new Box(boundingBoxComponent.getBoundingBox())
            .scale(scale)
            .offset(transform.getPosition());

        final double totalVolume = boundingBox.getVolume();
        double filledVolume = 0.0;

        final Vector3i minBlock = Vector3dUtil.toVector3i(boundingBox.min);
        final Vector3i maxBlock = Vector3dUtil.toVector3i(boundingBox.max);
        for (int x = minBlock.x; x <= maxBlock.x; x++) {
            for (int y = minBlock.y; y <= maxBlock.y; y++) {
                for (int z = minBlock.z; z <= maxBlock.z; z++) {
                    final int blockTypeIdx = world.getBlock(x, y, z);
                    if (blockTypeIdx == BlockType.EMPTY_ID) {
                        continue; // Block is empty.
                    }
                    final BlockType blockType = BlockType.getAssetMap().getAsset(blockTypeIdx);
                    if (blockType == null || blockType.getMaterial() == BlockMaterial.Empty) {
                        continue; // Block is empty.
                    }

                    final Box blockBox = new Box(x, y, z, x + 1, y + 1, z + 1);
                    final Box overlap = new Box(blockBox.min.max(boundingBox.min, new Vector3d()), blockBox.max.min(boundingBox.max, new Vector3d()));
                    filledVolume += overlap.getVolume();

                    if (Math.abs(this.threshold - filledVolume / totalVolume) < THRESHOLD_EPSILON) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
