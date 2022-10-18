package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.data.AliasData;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandAddAlias extends CommandBase {

    public CommandAddAlias() {
        AliasData.init();
    }

    @Override
    public String getCommandName() {
        return "addalias";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/addalias";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equals("clearall")) {
            AliasData.getMap().clear();
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Cleared alias for all players.");
            return;
        } else if (args.length == 1 && args[0].equals("list")) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "In this lobby :\n");
            for (final NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                final String alias = AliasData.getAlias(networkPlayerInfo.getGameProfile().getName());
                if (alias != null) {
                    ChatUtil.addChatMessage(NameUtil.getFormattedName(networkPlayerInfo.getGameProfile().getName()) + EnumChatFormatting.RESET + " (" + alias + ")");
                }
            }
            return;
        }
        if (args.length != 2) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : /addalias <playername> <alias>");
            return;
        }
        if (args[0].equals("remove")) {
            AliasData.removeAlias(args[1]);
            NameUtil.updateGameProfileAndName(args[1], true);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + args[1]);
            return;
        }
        AliasData.putAlias(args[0], args[1]);
        NameUtil.updateGameProfileAndName(args[0], true);
        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for " + EnumChatFormatting.GOLD + args[0] + EnumChatFormatting.GREEN + " : " + EnumChatFormatting.GOLD + args[1]);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final List<String> onlinePlayersByName = TabCompletionUtil.getOnlinePlayersByName();
        onlinePlayersByName.addAll(Arrays.asList("clearall", "list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ad");
    }

}
