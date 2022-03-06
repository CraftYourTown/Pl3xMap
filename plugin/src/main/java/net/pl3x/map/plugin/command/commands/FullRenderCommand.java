package net.pl3x.map.plugin.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.plugin.Pl3xMapPlugin;
import net.pl3x.map.plugin.command.CommandManager;
import net.pl3x.map.plugin.command.Pl3xMapCommand;
import net.pl3x.map.plugin.command.argument.MapWorldArgument;
import net.pl3x.map.plugin.configuration.Lang;
import net.pl3x.map.plugin.data.MapWorld;
import net.pl3x.map.plugin.task.render.FullRender;
import net.pl3x.map.plugin.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class FullRenderCommand extends Pl3xMapCommand {

    public FullRenderCommand(final @NonNull Pl3xMapPlugin plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("fullrender")
                        .argument(MapWorldArgument.optional("world"), CommandUtil.description(Lang.OPTIONAL_WORLD_ARGUMENT_DESCRIPTION))
                        .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.FULLRENDER_COMMAND_DESCRIPTION))
                        .permission("pl3xmap.command.fullrender")
                        .handler(this::executeFullRender));
    }

    private void executeFullRender(final @NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final MapWorld world = CommandUtil.resolveWorld(context);
        if (world.isRendering()) {
            Lang.send(sender, Lang.RENDER_IN_PROGRESS, Placeholder.unparsed("world", world.name()));
            return;
        }

        if (sender instanceof Player) {
            Lang.send(sender, Lang.LOG_STARTED_FULLRENDER, Placeholder.unparsed("world", world.name()));
        }
        world.startRender(new FullRender(world));
    }

}
