package me.nullicorn.hytale.stormserpent.ui.map;

import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarkerComponent;
import com.hypixel.hytale.protocol.packets.worldmap.TintComponent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MarkersCollector;
import com.hypixel.hytale.server.core.util.PositionUtil;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;

import javax.annotation.Nonnull;

public final class StormSerpentMapMarkerProvider implements WorldMapManager.MarkerProvider {
    public static final String ID = "stormSerpent";
    public static final StormSerpentMapMarkerProvider INSTANCE = new StormSerpentMapMarkerProvider();

    private static final String MARKER_ID_PREFIX = "StormSerpent-";
    private static final FormattedMessage MARKER_NAME = Message.translation("server.npcRoles.Serpent_Storm").getFormattedMessage();
    private static final String MARKER_ICON_NAME = "Serpent_Storm_Head_64x64.png";
    private static final Color MARKER_ICON_TINT = new Color((byte) 105, (byte) 165, (byte) 161);

    @Override
    public void update(
        @Nonnull final World world,
        @Nonnull final Player player,
        @Nonnull final MarkersCollector collector
    ) {
        final var markersResource = world.getEntityStore().getStore().getResource(StormSerpentPlugin.get().getStormSerpentMapMarkersResourceType());
        markersResource.forEachMarker((uuid, position) -> {
            final String id = MARKER_ID_PREFIX + uuid;
            final Transform transform = new Transform(PositionUtil.toPositionPacket(position), new Direction());
            collector.add(new MapMarker(id, MARKER_NAME, MARKER_ICON_NAME, transform, new ContextMenuItem[]{}, new MapMarkerComponent[]{new TintComponent(MARKER_ICON_TINT)}));
        });
    }
}
