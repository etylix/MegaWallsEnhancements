package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HackerDetector {

    public static final HackerDetector INSTANCE = new HackerDetector();
    private static PrintStream printStream;
    /** Field stolen from EntityLivingBase */
    public static final UUID sprintingUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;
    /** Data about blocks broken during this tick */
    public final List<BrokenBlock> brokenBlocksList = new ArrayList<>();
    public final HashSet<String> playersToLog = new HashSet<>();
    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        initPrintStream();
    }

    private HackerDetector() {
        this.checkList.add(new AutoblockCheck());
        this.checkList.add(FastbreakCheck.INSTANCE);
        this.checkList.add(new KeepsprintCheck());
        this.checkList.add(new NoSlowdownCheck());
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo && ConfigHandler.hackerDetector) {
            event.left.add("");
            event.left.add("Hacker Detector:");
            event.left.add("Player" + (playersChecked > 1 ? "s" : "") + " checked: " + playersChecked);
            event.left.add("Time elapsed (ns/s): " + ChatUtil.formatLong(timeElapsed));
            final double fpsLost = (timeElapsed / (10e9d - timeElapsed)) * Minecraft.getDebugFPS();
            event.left.add("Impact on performance : -" + String.format("%.2f", fpsLost) + "fps");
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            playersCheckedTemp = 0;
            final long timeStart = System.nanoTime();
            this.onTickStart();
            timeElapsedTemp += System.nanoTime() - timeStart;
        } else if (event.phase == TickEvent.Phase.END) {
            final long timeStart = System.nanoTime();
            this.onTickEnd();
            timeElapsedTemp += System.nanoTime() - timeStart;
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 20 == 0) {
                timeElapsed = timeElapsedTemp;
                timeElapsedTemp = 0L;
            }
            playersChecked = playersCheckedTemp;
        }
    }

    private void onTickStart() {

        if (!ConfigHandler.hackerDetector) return;

        if (mc.theWorld != null) {
            for (final EntityPlayer player : mc.theWorld.playerEntities) {
                ((EntityPlayerAccessor) player).getPlayerDataSamples().ontickStart();
            }
        }

        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                this.scheduledTasks.poll().run();
            }
        }

    }

    private void onTickEnd() {
        if (!ConfigHandler.hackerDetector) return;
        FastbreakCheck.INSTANCE.onTickEnd();
    }

    /**
     * This gets called once per entity per tick.
     * Only gets called when the client plays on a server.
     * Hook is injected at end of {@link net.minecraft.world.World#updateEntityWithOptionalForce}
     */
    public void performChecksOnPlayer(EntityPlayer player) {
        if (mc.thePlayer == null ||
                player.ticksExisted < 20 ||
                player.isDead ||
                player.capabilities.isFlying ||
                player.capabilities.isCreativeMode ||
                player.isInvisible() ||
                ScoreboardTracker.isInSkyblock ||
                (!ScoreboardTracker.isReplayMode && NameUtil.filterNPC(player.getUniqueID()))) {
            return;
        }
        final long timeStart = System.nanoTime();
        if (player == mc.thePlayer) {
            FastbreakCheck.INSTANCE.checkPlayerSP(player);
            timeElapsedTemp += System.nanoTime() - timeStart;
            return;
        }
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        playersCheckedTemp++;
        if (data.updatedThisTick) return;
        data.onTick(player);
        if (ConfigHandler.debugLogging && playersToLog.contains(player.getName())) log(player, data);
        checkList.forEach(check -> check.performCheck(player, data));
        timeElapsedTemp += System.nanoTime() - timeStart;
    }

    /**
     * Used for debuging and testing
     */
    @SuppressWarnings("unused")
    private EntityPlayer getClosestPlayer() {
        EntityPlayer closestPlayer = null;
        double distance = 1000D;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player instanceof EntityPlayerSP || player.ticksExisted < 60 || player.capabilities.isFlying || player.capabilities.isCreativeMode || NameUtil.filterNPC(player.getUniqueID())) {
                continue;
            }
            final float distanceToEntity = mc.thePlayer.getDistanceToEntity(player);
            if (distanceToEntity < distance) {
                closestPlayer = player;
                distance = distanceToEntity;
            }
        }
        return closestPlayer;
    }

    public static void addScheduledTask(Runnable runnable) {
        if (runnable == null) return;
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static void onEntitySwing(int attackerID) {
        HackerDetector.addScheduledTask(() -> {
            if (mc.theWorld == null) return;
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            if (attacker instanceof EntityPlayerAccessor) {
                final PlayerDataSamples data = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
                data.hasSwung = true;
                data.lastSwingTime = -1;
            }
        });
    }

    public static void checkPlayerAttack(int attackerID, int targetId, int attackType) {
        HackerDetector.addScheduledTask(() -> {
            if (mc.theWorld == null || mc.thePlayer == null) return;
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            final Entity target = mc.theWorld.getEntityByID(targetId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }
            // discard attacks when the target is near the
            // entity render distance since the attacker might
            // not be loaded on my client
            final double xDiff = mc.thePlayer.posX - target.posX;
            final double zDiff = mc.thePlayer.posZ - target.posZ;
            if (xDiff < -56D || xDiff > 56D || zDiff < -56D || zDiff > 56D) return;
            if (attacker.getDistanceSqToEntity(target) > 64d) {
                return;
            }
            if (ScoreboardTracker.isInMwGame && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() != '\0' && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() == ((EntityPlayerAccessor) target).getPlayerTeamColor()) {
                return;
            }
            if (attackType == 1) { // swing and hurt packet received consecutively
                onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target, "attack");
            } else if (attackType == 2) { // target hurt
                // when an ability does damage to multiple players, this can fire multiple times
                // on different players for the same attacker
                if (((EntityPlayer) attacker).swingProgressInt == -1 && ((EntityPlayer) target).hurtTime == 10) {
                    onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target, "hurt");
                }
            } else if (attackType == 4) { // target has crit particles
                if (((EntityPlayer) attacker).swingProgressInt == -1 && !attacker.onGround && attacker.ridingEntity == null) {
                    onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target, "critical");
                }
            } else if (attackType == 5) { // target has sharp particles
                if (((EntityPlayer) attacker).swingProgressInt == -1) {
                    final ItemStack heldItem = ((EntityPlayer) attacker).getHeldItem();
                    if (heldItem != null) {
                        final Item item = heldItem.getItem();
                        if ((item instanceof ItemSword || item instanceof ItemTool) && heldItem.isItemEnchanted()) {
                            onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target, "sharpness");
                        }
                    }
                }
            }
        });
    }

    private static void onPlayerAttack(EntityPlayer attacker, EntityPlayer target, String attackType) {
        final PlayerDataSamples dataAttacked = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
        if (dataAttacked.hasAttackedMultiTarget) {
            return;
        }
        if (dataAttacked.targetedPlayer != null && dataAttacked.targetedPlayer != target) {
            dataAttacked.hasAttackedMultiTarget = true;
            dataAttacked.hasAttacked = false;
            dataAttacked.targetedPlayer = null;
            ((EntityPlayerAccessor) target).getPlayerDataSamples().hasBeenAttacked = false;
            return;
        }
        dataAttacked.hasAttacked = true;
        dataAttacked.targetedPlayer = target;
        ((EntityPlayerAccessor) target).getPlayerDataSamples().hasBeenAttacked = true;
        if (ConfigHandler.debugLogging) {
            log(attacker.getName() + " attacked " + target.getName() + " [" + attackType + "]");
            //ChatUtil.debug(System.currentTimeMillis() % 1000 + " " +
            //        NameUtil.getFormattedNameWithoutIcons(attacker.getName())
            //        + EnumChatFormatting.RESET + " attacked "
            //        + NameUtil.getFormattedNameWithoutIcons(target.getName())
            //        + EnumChatFormatting.RESET + " [" + attackType + "]"
            //);
        }
    }

    public static void onEquipmentPacket(EntityPlayer player, S04PacketEntityEquipment packet) {
        final long timeStart = System.nanoTime();
        final ItemStack currentItemStack = player.inventory.armorInventory[packet.getEquipmentSlot() - 1];
        final ItemStack newItemStack = packet.getItemStack();
        if (currentItemStack != null && newItemStack != null) {
            if (currentItemStack.getItem() == newItemStack.getItem()) {
                final int newItemDamage = newItemStack.getItemDamage();
                final int currentItemDamage = currentItemStack.getItemDamage();
                if (newItemDamage > currentItemDamage || (newItemDamage == 0 && newItemDamage == currentItemDamage)) {
                    HackerDetector.addScheduledTask(() -> ((EntityPlayerAccessor) player).getPlayerDataSamples().armorDamaged = true);
                }
            }
        }
        INSTANCE.timeElapsedTemp += System.nanoTime() - timeStart;
    }

    private static void initPrintStream() {
        final File logsFolder = new File(Minecraft.getMinecraft().mcDataDir, "logs");
        //noinspection ResultOfMethodCallIgnored
        logsFolder.mkdirs();
        final File logFile = new File(logsFolder, "HackerDetector.log");
        if (logFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logFile.delete();
        }
        if (!logFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            printStream = new PrintStream(new FileOutputStream(logFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        if (printStream == null) return;
        final String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
        printStream.println("[" + time + "] " + message);
    }

    private static void log(EntityPlayer player, PlayerDataSamples data) {
        log(player.getName()
                + " | onGround " + player.onGround
                + " | speedXZ (m/s) " + String.format("%.4f", data.getSpeedXZ())
                + " | speedXYZ (m/s) " + data.speedToString()
                + " | position " + new Vector3D(player.posX, player.posY, player.posZ)
                + " | rotationPitch " + String.format("%.4f", player.rotationPitch)
                + " | rotationYawHead " + String.format("%.4f", player.rotationYawHead)
                //+ " | look Vector " + data.lookVector
                //+ " | lookAngleDiff " + String.format("%.4f", data.lookAngleDiff)
                //+ " | dYaw " + String.format("%.4f", data.dYaw)
                //+ " | lastTime_dYawChangedSign " + data.lastTime_dYawChangedSign
                + " | is sprinting " + (player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(sprintingUUID) != null)
                + " | sprintTime " + data.sprintTime
                + " | lastHurtTime " + data.lastHurtTime
                + " | isSwingInProgress " + player.isSwingInProgress
                + " | useItemTime " + data.useItemTime
                + " | lastSwingTime " + data.lastSwingTime
                + " | lastEatDrinkTime " + data.lastEatDrinkTime
                + " | isUsingItem " + player.isUsingItem()
                + " | ticksExisted " + player.ticksExisted
                + " | isRidingEntity " + player.isRiding()
        );
    }

}
