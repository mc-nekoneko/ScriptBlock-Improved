package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SaveCommand extends Command {

    public SaveCommand() {
        super(
            "save",
            Args.empty()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        sendMessage(sender,
            "Saving scripts into '/ScriptBlock-Improved/scripts.json'");
        new Thread(() -> {
            try {
                ScriptBlockImproved.getInstance().getScriptList().getStorage().save();
                sendMessage(sender, MessageKind.SUCCESS, "Successfully saved");
            } catch (final IOException e) {
                sendMessage(sender, MessageKind.ERROR, "Save failed!");
            }
        }, "sbi-command-sbi-save").start();
    }
}