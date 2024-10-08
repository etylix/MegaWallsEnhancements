package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.api.requests.MojangNameToUUID;
import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import fr.alexdoru.mwe.utils.UUIDUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.UUID;

public class CommandUnWDR extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "unwdr";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " <playername>");
            return;
        }
        if (args.length == 1) { // if you use /unwdr <playername>
            this.unwdrPlayer(args[0]);
        } else if (args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>
            this.unwdr(args[0], args[1]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

    private void unwdrPlayer(String playername) {
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangNameToUUID apireq = new MojangNameToUUID(playername);
                mc.addScheduledTask(() -> this.unwdr(apireq.getUuid(), apireq.getName()));
            } catch (ApiException e) {
                mc.addScheduledTask(() -> this.unwdr(null, playername));
            }
            return null;
        });
    }

    private void unwdr(String uuidstr, String playername) {
        ReportQueue.INSTANCE.removePlayerFromReportQueue(playername);
        final UUID uuid = UUIDUtil.fromString(uuidstr);
        final WDR wdr = WdrData.remove(uuid, playername);
        if (wdr == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
            return;
        }
        WdrData.saveReportedPlayers();
        ChatHandler.deleteWarningFromChat(playername);
        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + ".");
    }

}
