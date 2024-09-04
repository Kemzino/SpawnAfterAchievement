package net.dovolor.saa;

import net.dovolor.saa.config.EntityBlockConfig;
import net.dovolor.saa.config.EntitySpawnConfig;
import net.dovolor.saa.manager.AchivementManager;
import net.dovolor.saa.manager.EntityManager;
import net.dovolor.saa.mixin.AdvancementTrackerMixin;
import net.dovolor.saa.store.CustomAdvancementStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.Registry;


public class SpawnAfterAchievement implements ModInitializer {
	public static final String MOD_ID = "spawn-after-achievement";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static EntityManager entityManager;

	@Override
	public void onInitialize() {
		EntitySpawnConfig.loadConfig();
		EntityBlockConfig.loadConfig();

		entityManager = EntityManager.getInstance();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
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
						Identifier id = new Identifier(entity);
						EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);

						if (entityType != null) {
							ServerWorld world = server.getWorld(World.OVERWORLD);

							if (world != null) {
								Entity entityInstance = entityType.create(world);

								if (entityInstance != null) {
									NbtCompound nbt = new NbtCompound();
									entityInstance.writeNbt(nbt);

									if (EntityManager.isNaturalSpawn(entityInstance, nbt)) {
										entityManager.blockEntitySpawn(entity);
									}
								}
							}
						}
					}
				}
			}
		}

		for(EntityBlockConfig.BlockConfig config: EntityBlockConfig.spawnConfigs) {
			if(CustomAdvancementStorage.getAllCompletedAdvancements(server).contains(config.getAchievement())) {
				if(config.getEntities() != null) {
					for (String entity : config.getEntities()) {
						Identifier id = new Identifier(entity);
						EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);

						if (entityType != null) {
							ServerWorld world = server.getWorld(World.OVERWORLD);

							if (world != null) {
								Entity entityInstance = entityType.create(world);

								if (entityInstance != null) {
									NbtCompound nbt = new NbtCompound();
									entityInstance.writeNbt(nbt);

									if (EntityManager.isNaturalSpawn(entityInstance, nbt)) {
										entityManager.blockEntitySpawn(entity);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}