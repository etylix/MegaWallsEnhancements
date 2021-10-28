package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerStatus;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandStalk extends CommandBase {

    @Override
    public String getCommandName() {
        return "stalk";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stalk <playernames>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 1) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { // api key not setup
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
            return;
        }

        int nbcores = Math.min(args.length, Runtime.getRuntime().availableProcessors());
        ExecutorService service = Executors.newFixedThreadPool(nbcores);

        for (String name : args) {
            service.submit(new StalkTask(name));
        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        //return (GameInfoGrabber.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}

class StalkTask implements Callable<String> {

    final String name;

    public StalkTask(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {

        try {
            MojangPlayernameToUUID apiname = new MojangPlayernameToUUID(name);
            String uuid = apiname.getUuid();

            // player found on mojang's api

            String playername = apiname.getName();
            HypixelPlayerStatus apistatus = new HypixelPlayerStatus(uuid, HypixelApiKeyUtil.getApiKey());

            if (apistatus.isOnline()) { // player is online

                if (apistatus.getGamemode().equals("Mega Walls")) { // player is in MW, display currrent class and skin

                    HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))
                            + EnumChatFormatting.GREEN + " playing "
                            + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                            + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                    ));
                    return null;

                } else { // player isn't in MW

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))));
                    return null;
                }

            } else {                   // player is offline, stalk the playerdata info

                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                LoginData logindata = new LoginData(playerdata.getPlayerData());
                String formattedname = logindata.getFormattedName();

                if (playerdata.getPlayerData() == null) { // Failed to contact hypixel's API

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + EnumChatFormatting.RED + "Failed to retrieve information from Hypixel's api for : " + playername + EnumChatFormatting.RED + "."));
                    return null;

                } else if (logindata.hasNeverJoinedHypixel()) { // player never joined hypixel

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " has never joined Hypixel."));
                    return null;

                } else if (logindata.isStaffonHypixel()) { // player is a staff member

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + formattedname + EnumChatFormatting.RED + " is completely hiding their online status from the API."
                            + EnumChatFormatting.DARK_GRAY + " It happens for staff members."));
                    return null;

                } else if (logindata.isHidingFromAPI()) {

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + formattedname + EnumChatFormatting.RED + " is blocking their online status from the API."));
                    return null;

                } else if (logindata.isOnline()) { // player is online but hiding their session

                    if (logindata.getMostRecentGameType().equals("Mega Walls")) { // online and in MW

                        MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());

                        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                                + formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                + EnumChatFormatting.GREEN + " playing "
                                + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                                + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                                + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)" + "\n"
                        ));
                        return null;

                    } else { // online not in MW

                        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                                + formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                + EnumChatFormatting.GREEN + "." + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)"));
                        return null;
                    }

                } else { // offline

                    String offlinesince = DateUtil.timeSince(logindata.getLastLogout());
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW()
                            + formattedname + EnumChatFormatting.RED + " has been offline for " + EnumChatFormatting.YELLOW + offlinesince
                            + EnumChatFormatting.RED + "." + (logindata.getMostRecentGameType().equals("?") ? "" : EnumChatFormatting.RED + " Last seen in : " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType())));
                    return null;
                }

            }
        } catch (ApiException e) {
            e.printStackTrace();
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + e.getMessage()));
        }

        return null;
    }

}
