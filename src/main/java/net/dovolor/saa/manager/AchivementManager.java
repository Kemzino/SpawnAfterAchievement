package net.dovolor.saa.manager;

import net.dovolor.saa.mixin.PlayerAdvancementTrackerAccessor;
import net.minecraft.advancement.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class AchivementManager {

    public static boolean hasAchievement(PlayerAdvancementTracker tracker, String achievementId) {
        ServerPlayerEntity player = ((PlayerAdvancementTrackerAccessor) tracker).getOwner();
        MinecraftServer server = player.getServer();
        AdvancementManager advancementManager = new AdvancementManager();
        if (server != null && achievementId != null) {
            Advancement advancement = Objects.requireNonNull(advancementManager.get(Identifier.tryParse(achievementId))).getAdvancement();
            AdvancementEntry advancementEntry = new AdvancementEntry(Identifier.tryParse(achievementId), advancement);
            if (advancement != null) {
                AdvancementProgress progress = tracker.getProgress(advancementEntry);
                return progress.isDone();
            }
        }
        return false;
    }
}
