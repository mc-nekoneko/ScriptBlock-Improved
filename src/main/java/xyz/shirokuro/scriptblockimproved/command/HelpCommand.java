package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class HelpCommand extends Command {

    private static final ImmutableList<String> ROOT_MESSAGE = ImmutableList.of(
        ChatColor.BOLD + "[modify] modify scripts",
        "  create, createat, remove, removeat",
        ChatColor.BOLD + "[view] view scripts",
        "  view, viewat, list",
        ChatColor.BOLD + "[other] other",
        "  availables, save, map-perm, migrate",
        "",
        "'/sbi help <topic>' to show details about topic",
        "Examples:",
        "/sbi help modify",
        "/sbi help view"
    );
    private static final ImmutableList<String> TOPIC_MODIFY_MESSAGE = ImmutableList.of(
        ChatColor.BOLD + "create a script to clicked/specified block",
        "  create <trigger> <options>",
        "  createat <world> <x> <y> <z> <trigger> <options>",
        ChatColor.BOLD + "remove all scripts in clicked/specified block",
        "  remove",
        "  removeat <world> <x> <y> <z>"
    );
    private static final ImmutableList<String> TOPIC_VIEW_MESSAGE = ImmutableList.of(
        ChatColor.BOLD + "view script info in clicked/specified block",
        "  view",
        "  viewat <world> <x> <y> <z>",
        ChatColor.BOLD + "view list of scripts",
        "  list [world]"
    );
    private static final ImmutableList<String> TOPIC_OTHER_MESSAGE = ImmutableList.of(
        ChatColor.BOLD + "list available options and triggers",
        "  availables",
        ChatColor.BOLD + "save scripts into scripts.json",
        "  save",
        ChatColor.BOLD + "associate permission for command",
        "  map-perm <permission> <command>",
        ChatColor.BOLD + "migrate scripts from ScriptBlock",
        "  migrate"
    );

    public HelpCommand() {
        super("help", Args.builder().optional("topic").build());
    }

    @Override
    public void execute(@NonNull final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final Optional<String> topicOptional = data.getArgs().get("topic");
        if (topicOptional.isPresent()) {
            helpTopic(sender, topicOptional.get());
            return;
        }
        MessageUtils.sendMessage(sender, ROOT_MESSAGE);
    }

    private void helpTopic(@NonNull final CommandSender sender, @NonNull final String topicStr) {
        final Topic topic = Topic.getByName(topicStr).orElse(null);
        if (topic == null) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Unknown topic");
            return;
        }
        MessageUtils.sendMessage(sender, topic.getMessage());
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("topic", CandidateFactories.filter(ignored -> {
                return Arrays.stream(Topic.values())
                    .map(Topic::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }

    private enum Topic {
        MODIFY(TOPIC_MODIFY_MESSAGE),
        VIEW(TOPIC_VIEW_MESSAGE),
        OTHER(TOPIC_OTHER_MESSAGE);

        private static final ImmutableMap<String, Topic> BY_NAME;

        static {
            final Map<String, Topic> byName = new HashMap<>();
            for (final Topic topic : Topic.values()) {
                byName.put(topic.name().toLowerCase(), topic);
            }
            BY_NAME = ImmutableMap.copyOf(byName);
        }

        @NonNull
        @Getter
        private final ImmutableList<String> message;

        private Topic(@NonNull final List<String> message) {
            this.message = ImmutableList.copyOf(message);
        }

        /**
         * Returns Topic by name.<br>
         * This operation is case-insensitive.
         */
        public static Optional<Topic> getByName(@NonNull final String name) {
            return Optional.ofNullable(BY_NAME.get(name.toLowerCase()));
        }
    }
}
