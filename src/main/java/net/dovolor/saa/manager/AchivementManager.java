package net.dovolor.saa.manager;

import net.dovolor.saa.mixin.PlayerAdvancementTrackerAccessor;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AchivementManager {

    public static boolean hasAchievement(PlayerAdvancementTracker tracker, String achievementId) {
        ServerPlayerEntity player = ((PlayerAdvancementTrackerAccessor) tracker).getOwner();
        MinecraftServer server = player.getServer();
        AdvancementManager advancementManager = new AdvancementManager();
        if (server != null && achievementId != null) {
            Advancement advancement = advancementManager.get(Identifier.tryParse(achievementId));
            if (advancement != null) {
                AdvancementProgress progress = tracker.getProgress(advancement);
                return progress.isDone();
            }
        }
        return false;
    }
}
