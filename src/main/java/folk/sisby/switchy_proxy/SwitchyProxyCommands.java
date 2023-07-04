package folk.sisby.switchy_proxy;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyFeedbackStatus;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.exception.ModuleNotFoundException;
import folk.sisby.switchy.api.exception.PresetNotFoundException;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.util.Feedback;
import folk.sisby.switchy.util.SwitchyCommand;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import folk.sisby.switchy_proxy.modules.ProxyModuleConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static folk.sisby.switchy.util.Feedback.helpText;
import static folk.sisby.switchy.util.Feedback.literal;
import static folk.sisby.switchy.util.SwitchyCommand.execute;

public class SwitchyProxyCommands implements SwitchyEvents.CommandInit {
	public static final LiteralArgumentBuilder<ServerCommandSource> COMMAND_ADD = CommandManager.literal("add")
		.then(SwitchyCommand.presetArgument(true)
			.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
				.executes(c -> execute(c, (pl, pr, f) -> addProxy(pr, f, c.getArgument("preset", String.class), c.getArgument("pattern", String.class))))
			)
		);
	public static final LiteralArgumentBuilder<ServerCommandSource> COMMAND_REMOVE = CommandManager.literal("remove")
		.then(SwitchyCommand.presetArgument(true)
			.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
				.suggests(SwitchyProxyCommands::suggestPatterns)
				.executes(c -> execute(c, (pl, pr, f) -> removeProxy(pr, f, c.getArgument("preset", String.class), c.getArgument("pattern", String.class))))
			)
		);
	public static final LiteralArgumentBuilder<ServerCommandSource> COMMAND_LATCH = CommandManager.literal("latch")
		.then(CommandManager.argument("enable", BoolArgumentType.bool())
			.executes(c -> execute(c, (pl, pr, f) -> setLatch(pr, f, c.getArgument("enable", Boolean.class))))
		);


	public static CompletableFuture<Suggestions> suggestPatterns(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		SwitchyPresets presets = ((SwitchyPlayer) context.getSource().getPlayer()).switchy$getPresets();
		try {
			String name = context.getArgument("preset", String.class);
			ProxyModule module = presets.getPreset(name).getModule(ProxyModule.ID, ProxyModule.class);
			CommandSource.suggestMatching(module.getTags().stream().map(ProxyTag::toString), builder);
		} catch (IllegalArgumentException ignored) {

		}
		return builder.buildFuture();
	}

	public static SwitchyFeedbackStatus addProxy(SwitchyPresets presets, Consumer<Text> feedback, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.addTag(ProxyTag.parse(pattern));
					feedback.accept(Feedback.success("commands.switchy_proxy.add.success", literal(pattern), literal(name)));
					return SwitchyFeedbackStatus.SUCCESS;
				} catch (IllegalArgumentException ignoredParseFail) {
					feedback.accept(Feedback.invalid("commands.switchy_proxy.add.fail.invalid", literal("text")));
					return SwitchyFeedbackStatus.INVALID;
				}
			} catch (ModuleNotFoundException ignored) {
				feedback.accept(Feedback.invalidTry("commands.switchy_proxy.add.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString())));
				return SwitchyFeedbackStatus.INVALID;
			}
		} catch (PresetNotFoundException ignored) {
			feedback.accept(Feedback.invalidTry("commands.switchy_proxy.add.fail.preset", "commands.switchy.list.command"));
			return SwitchyFeedbackStatus.INVALID;
		}
	}

	public static SwitchyFeedbackStatus removeProxy(SwitchyPresets presets, Consumer<Text> feedback, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.removeTag(pattern);
					feedback.accept(Feedback.success("commands.switchy_proxy.remove.success", literal(pattern), literal(name)));
					return SwitchyFeedbackStatus.SUCCESS;
				} catch (IllegalArgumentException ignoredMissingTag) {
					feedback.accept(Feedback.invalid("commands.switchy_proxy.remove.fail.pattern"));
					return SwitchyFeedbackStatus.INVALID;
				}
			} catch (ModuleNotFoundException ignored) {
				feedback.accept(Feedback.invalidTry("commands.switchy_proxy.remove.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString())));
				return SwitchyFeedbackStatus.INVALID;
			}
		} catch (PresetNotFoundException ignored) {
			feedback.accept(Feedback.invalidTry("commands.switchy_proxy.remove.fail.preset", "commands.switchy.list.command"));
			return SwitchyFeedbackStatus.INVALID;
		}
	}

	private static SwitchyFeedbackStatus setLatch(SwitchyPresets presets, Consumer<Text> feedback, Boolean enable) {
		try {
			presets.getModuleConfig(ProxyModule.ID, ProxyModuleConfig.class).setLatchEnabled(enable);
			feedback.accept(Feedback.success("commands.switchy_proxy.latch.success.%s".formatted(enable ? "enabled" : "disabled")));
			return SwitchyFeedbackStatus.SUCCESS;
		} catch (IllegalStateException ignoredModuleDisabled) {
			feedback.accept(Feedback.invalidTry("commands.switchy_proxy.latch.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString())));
			return SwitchyFeedbackStatus.INVALID;
		}
	}

	@Override
	public void registerCommands(LiteralArgumentBuilder<ServerCommandSource> switchyArgument, BiConsumer<Text, Predicate<ServerPlayerEntity>> helpTextRegistry) {
		switchyArgument
			.then(CommandManager.literal("proxy")
				.then(COMMAND_ADD)
				.then(COMMAND_REMOVE)
				.then(COMMAND_LATCH)
			);
		List.of(
			helpText("commands.switchy_proxy.add.help", "commands.switchy_proxy.add.command", "commands.switchy_proxy.placeholder.preset", "commands.switchy_proxy.placeholder.proxy"),
			helpText("commands.switchy_proxy.remove.help", "commands.switchy_proxy.remove.command", "commands.switchy_proxy.placeholder.preset", "commands.switchy_proxy.placeholder.proxy")
		).forEach(t -> helpTextRegistry.accept(t, (p) -> true));
	}
}
