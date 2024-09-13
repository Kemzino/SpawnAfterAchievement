package net.dovolor.saa;

import net.dovolor.saa.config.EntityBlockConfig;
import net.dovolor.saa.config.EntitySpawnConfig;
import net.dovolor.saa.manager.EntityManager;
import net.dovolor.saa.store.CustomAdvancementStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
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


		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			SpawnAfterAchievement.LOGGER.info("load rules");
			entityManager = EntityManager.getInstance();
			loadRules(server);
		});
	}


	private void loadRules(MinecraftServer server) {
		for(EntitySpawnConfig.SpawnConfig config: EntitySpawnConfig.spawnConfigs) {
			if(CustomAdvancementStorage.getAllCompletedAdvancements(server).contains(config.getAchievement())) {
				if(config.getEntities() != null) {
					for (String entity : config.getEntities()) {
						entityManager.allowEntitySpawn(entity);
					}
				}
			} else {
				if (config.getEntities() != null) {
					for (String entity : config.getEntities()) {
						Identifier id = Identifier.tryParse(entity);
						EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);

						if (entityType != null && EntityManager.isNaturalSpawn(entityType)) {
							entityManager.blockEntitySpawn(entity);
						}
					}
				}
			}
		}

		for(EntityBlockConfig.BlockConfig config: EntityBlockConfig.spawnConfigs) {
			if(CustomAdvancementStorage.getAllCompletedAdvancements(server).contains(config.getAchievement())) {
				if(config.getEntities() != null) {
					for (String entity : config.getEntities()) {
						Identifier id = Identifier.tryParse(entity);
						EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);

						if (entityType != null && EntityManager.isNaturalSpawn(entityType)) {
							entityManager.blockEntitySpawn(entity);
						}
					}
				}
			}
		}
	}

}