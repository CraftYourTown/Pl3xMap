package net.pl3x.map.plugin.configuration;

import com.google.common.collect.ImmutableSet;
import net.pl3x.map.plugin.Logger;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Advanced extends AbstractConfig {
    private Advanced() {
        super("advanced.yml");
    }

    static Advanced config;
    static int version;

    public static void reload() {
        config = new Advanced();

        version = config.getInt("config-version", 1);
        config.set("config-version", 1);

        config.readConfig(Advanced.class, null);

        WorldAdvanced.reload();
    }

    private static final Map<Class<? extends Event>, Boolean> eventListenerToggles = new HashMap<>();

    public static boolean listenerEnabled(final @NonNull Class<? extends Event> eventClass) {
        final Boolean enabled = eventListenerToggles.get(eventClass);
        if (enabled == null) {
            Logger.warn(String.format("No configuration option found for event listener: %s, the listener will not be enabled.", eventClass.getSimpleName()));
            return false;
        }
        return enabled;
    }

    private static void listenerToggles() {
        ImmutableSet.of(
                BlockPlaceEvent.class,
                BlockBreakEvent.class,
                LeavesDecayEvent.class,
                BlockBurnEvent.class,
                BlockExplodeEvent.class,
                BlockGrowEvent.class,
                BlockFormEvent.class,
                EntityBlockFormEvent.class,
                BlockSpreadEvent.class,
                FluidLevelChangeEvent.class,
                EntityExplodeEvent.class,
                EntityChangeBlockEvent.class,
                StructureGrowEvent.class,
                ChunkPopulateEvent.class
        ).forEach(clazz -> eventListenerToggles.put(clazz, config.getBoolean("settings.event-listeners." + clazz.getSimpleName(), true)));

        ImmutableSet.of(
                BlockFromToEvent.class,
                PlayerJoinEvent.class,
                PlayerQuitEvent.class,
                PlayerMoveEvent.class,
                BlockPhysicsEvent.class,
                BlockPistonExtendEvent.class,
                BlockPistonRetractEvent.class,
                ChunkLoadEvent.class
        ).forEach(clazz -> eventListenerToggles.put(clazz, config.getBoolean("settings.event-listeners." + clazz.getSimpleName(), false)));
    }

}
