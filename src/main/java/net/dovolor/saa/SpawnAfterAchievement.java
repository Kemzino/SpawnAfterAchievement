package net.dovolor.saa;

import net.dovolor.saa.config.EntityBlockConfig;
import net.dovolor.saa.config.EntitySpawnConfig;
import net.dovolor.saa.manager.AchivementManager;
import net.dovolor.saa.manager.EntityManager;
import net.dovolor.saa.mixin.AdvancementTrackerMixin;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpawnAfterAchievement implements ModInitializer {
	public static final String MOD_ID = "spawn-after-achievement";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static EntityManager entityManager;

	@Override
	public void onInitialize() {
		EntitySpawnConfig.loadConfig();
		EntityBlockConfig.loadConfig();

		entityManager = EntityManager.getInstance();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			onPlayerJoin(player);
		});
	}
	private void onPlayerJoin(ServerPlayerEntity player) {
		for (EntitySpawnConfig.SpawnConfig config : EntitySpawnConfig.spawnConfigs) {
			PlayerAdvancementTracker tracker = player.getAdvancementTracker();
			if(config.getEntities() != null) {
				if (!AchivementManager.hasAchievement(tracker, config.getAchievement())) {
					for (String entity : config.getEntities()) {
						entityManager.blockEntitySpawn(entity);
					}
				}
			}
		}
	}
}