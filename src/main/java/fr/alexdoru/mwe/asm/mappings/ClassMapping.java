package fr.alexdoru.mwe.asm.mappings;

import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;

public enum ClassMapping {

    ABSTRACTCLIENTPLAYER("bet", "net/minecraft/client/entity/AbstractClientPlayer"),
    AXISALIGNEDBB("aug", "net/minecraft/util/AxisAlignedBB"),
    BLOCKPOS("cj", "net/minecraft/util/BlockPos"),
    CHATCOMPONENTTEXT("fa", "net/minecraft/util/ChatComponentText"),
    CHATLINE("ava", "net/minecraft/client/gui/ChatLine"),
    CLICKEVENT("et", "net/minecraft/event/ClickEvent"),
    CLIENTCOMMANDHANDLER("net/minecraftforge/client/ClientCommandHandler"),
    COLLECTION("java/util/Collection"),
    EFFECTRENDERER("bec", "net/minecraft/client/particle/EffectRenderer"),
    ENTITY("pk", "net/minecraft/entity/Entity"),
    ENTITYANIMAL("tm", "net/minecraft/entity/passive/EntityAnimal"),
    ENTITYARROW("wq", "net/minecraft/entity/projectile/EntityArrow"),
    ENTITYFX("beb", "net/minecraft/client/particle/EntityFX"),
    ENTITYITEM("uz", "net/minecraft/entity/item/EntityItem"),
    ENTITYITEMFRAME("uo", "net/minecraft/entity/item/EntityItemFrame"),
    ENTITYLIVINGBASE("pr", "net/minecraft/entity/EntityLivingBase"),
    ENTITYOTHERPLAYERMP("bex", "net/minecraft/client/entity/EntityOtherPlayerMP"),
    ENTITYPLAYER("wn", "net/minecraft/entity/player/EntityPlayer"),
    ENTITYPLAYERSP("bew", "net/minecraft/client/entity/EntityPlayerSP"),
    ENTITYWITHER("uk", "net/minecraft/entity/boss/EntityWither"),
    ENUMCHATFORMATTING("a", "net/minecraft/util/EnumChatFormatting"),
    FLOATBUFFER("java/nio/FloatBuffer"),
    FONTRENDERER("avn", "net/minecraft/client/gui/FontRenderer"),
    FRAMEBUFFER("bfw", "net/minecraft/client/shader/Framebuffer"),
    GAMEPROFILE("com/mojang/authlib/GameProfile"),
    GAMESETTINGS("avh", "net/minecraft/client/settings/GameSettings"),
    GLSTATEMANAGER("bfl", "net/minecraft/client/renderer/GlStateManager"),
    GUICHAT("awv", "net/minecraft/client/gui/GuiChat"),
    GUICHATHOOKSEARCHBOX("fr/alexdoru/mwe/asm/hooks/GuiChatHook_SearchBox"),
    GUICONTAINER("ayl", "net/minecraft/client/gui/inventory/GuiContainer"),
    GUIINGAME("avo", "net/minecraft/client/gui/GuiIngame"),
    GUIINGAMEFORGE("net/minecraftforge/client/GuiIngameForge"),
    GUINEWCHAT("avt", "net/minecraft/client/gui/GuiNewChat"),
    GUINEWCHATHOOKSEARCHBOX("fr/alexdoru/mwe/asm/hooks/GuiNewChatHook_SearchBox"),
    GUIPLAYERTABOVERLAY("awh", "net/minecraft/client/gui/GuiPlayerTabOverlay"),
    GUISCREEN("axu", "net/minecraft/client/gui/GuiScreen"),
    GUISCREENBOOK("ayo", "net/minecraft/client/gui/GuiScreenBook"),
    GUITEXTFIELD("avw", "net/minecraft/client/gui/GuiTextField"),
    IBLOCKSTATE("alz", "net/minecraft/block/state/IBlockState"),
    ICAMERA("bia", "net/minecraft/client/renderer/culling/ICamera"),
    ICHATCOMPONENT("eu", "net/minecraft/util/IChatComponent"),
    ICOMMAND("k", "net/minecraft/command/ICommand"),
    ICOMMANDSENDER("m", "net/minecraft/command/ICommandSender"),
    INETHANDLER("ep", "net/minecraft/network/INetHandler"),
    INVENTORYPLAYER("wm", "net/minecraft/entity/player/InventoryPlayer"),
    ITEMSTACK("zx", "net/minecraft/item/ItemStack"),
    ITHREADLISTENER("od", "net/minecraft/util/IThreadListener"),
    KEYBINDING("avb", "net/minecraft/client/settings/KeyBinding"),
    MAP("java/util/Map"),
    MATH("java/lang/Math"),
    MINECRAFT("ave", "net/minecraft/client/Minecraft"),
    MWCLASS("fr/alexdoru/mwe/enums/MWClass"),
    NETHANDLERPLAYCLIENT("bcy", "net/minecraft/client/network/NetHandlerPlayClient"),
    NETWORKMANAGER("ek", "net/minecraft/network/NetworkManager"),
    NETWORKPLAYERINFO("bdc", "net/minecraft/client/network/NetworkPlayerInfo"),
    NETWORKPLAYERINFO$1("bdc$1", "net/minecraft/client/network/NetworkPlayerInfo$1"),
    PACKET("ff", "net/minecraft/network/Packet"),
    PACKETTHREADUTIL("fh", "net/minecraft/network/PacketThreadUtil"),
    PLAYERDATASAMPLES("fr/alexdoru/mwe/hackerdetector/data/PlayerDataSamples"),
    POTION("pe", "net/minecraft/potion/Potion"),
    RENDER("biv", "net/minecraft/client/renderer/entity/Render"),
    RENDERENTITYITEM("bjf", "net/minecraft/client/renderer/entity/RenderEntityItem"),
    RENDERERLIVINGENTITY("bjl", "net/minecraft/client/renderer/entity/RendererLivingEntity"),
    RENDERGLOBAL("bfr", "net/minecraft/client/renderer/RenderGlobal"),
    RENDERITEM("bjh", "net/minecraft/client/renderer/entity/RenderItem"),
    RENDERMANAGER("biu", "net/minecraft/client/renderer/entity/RenderManager"),
    RESOURCELOCATION("jy", "net/minecraft/util/ResourceLocation"),
    S19PACKETENTITYSTATUS("gi", "net/minecraft/network/play/server/S19PacketEntityStatus"),
    S1CPACKETENTITYMETADATA("hk", "net/minecraft/network/play/server/S1CPacketEntityMetadata"),
    S22PACKETMULTIBLOCKCHANGE("fz", "net/minecraft/network/play/server/S22PacketMultiBlockChange"),
    S23PACKETBLOCKCHANGE("fv", "net/minecraft/network/play/server/S23PacketBlockChange"),
    S25PACKETBLOCKBREAKANIM("fs", "net/minecraft/network/play/server/S25PacketBlockBreakAnim"),
    S38PACKETPLAYERLISTITEM("gz", "net/minecraft/network/play/server/S38PacketPlayerListItem"),
    S3EPACKETTEAMS("hr", "net/minecraft/network/play/server/S3EPacketTeams"),
    SCALEDRESOLUTION("avr", "net/minecraft/client/gui/ScaledResolution"),
    SCORE("aum", "net/minecraft/scoreboard/Score"),
    SCOREBOARD("auo", "net/minecraft/scoreboard/Scoreboard"),
    SCOREOBJECTIVE("auk", "net/minecraft/scoreboard/ScoreObjective"),
    SCOREPLAYERTEAM("aul", "net/minecraft/scoreboard/ScorePlayerTeam"),
    SHADERGROUP("blr", "net/minecraft/client/shader/ShaderGroup"),
    SKINCHATHEAD("fr/alexdoru/mwe/chat/SkinChatHead"),
    SLOT("yg", "net/minecraft/inventory/Slot"),
    STRINGBUILDER("java/lang/StringBuilder"),
    TEAM("auq", "net/minecraft/scoreboard/Team"),
    TESSELLATOR("bfx", "net/minecraft/client/renderer/Tessellator"),
    TEXTUREMANAGER("bmj", "net/minecraft/client/renderer/texture/TextureManager"),
    THREADQUICKEXITEXCEPTION("ki", "net/minecraft/network/ThreadQuickExitException"),
    WORLD("adm", "net/minecraft/world/World"),
    WORLDCLIENT("bdb", "net/minecraft/client/multiplayer/WorldClient"),
    WORLDRENDERER("bfd", "net/minecraft/client/renderer/WorldRenderer"),
    WORLDSETTINGS$GAMETYPE("adp$a", "net/minecraft/world/WorldSettings$GameType");

    public final String name;

    ClassMapping(String clearName) {
        this.name = clearName;
    }

    ClassMapping(String obfName, String clearName) {
        this.name = ASMLoadingPlugin.isObf() ? obfName : clearName;
    }

    @Override
    public String toString() {
        return name;
    }

}
