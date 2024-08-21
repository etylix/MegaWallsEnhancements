package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandArcanist extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "arc";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sendChatMessage("Iron in /tc");
    }

}
