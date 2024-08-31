package net.dovolor.saa;

import net.dovolor.saa.config.EntitySpawnConfig;
import net.dovolor.saa.manager.EntityManager;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpawnAfterAchievement implements ModInitializer {
	public static final String MOD_ID = "spawn-after-achievement";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static EntityManager entityManager;

	@Override
	public void onInitialize() {
		EntitySpawnConfig.loadConfig();

		entityManager = EntityManager.getInstance();

		for(EntitySpawnConfig.SpawnConfig config: EntitySpawnConfig.spawnConfigs) {
			for(String entity: config.getEntities()) {
				entityManager.blockEntitySpawn(entity);
			}
		}
	}
}