package net.dovolor.saa.manager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class EntityManager {
    private static EntityManager instance;

    private final Set<EntityType<?>> blockedEntities = new HashSet<>();

    private EntityManager() {
        ServerEntityEvents.ENTITY_LOAD.register(this::onEntityJoinWorld);
    }

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    private void onEntityJoinWorld(Entity entity, World world) {
        if (blockedEntities.contains(entity.getType())) {
            entity.remove(Entity.RemovalReason.DISCARDED); // Remove the entity if it's blocked
        }
    }

    public void blockEntitySpawn(String entityId) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(entityId));
        blockedEntities.add(entityType);
    }

    public void allowEntitySpawn(String entityId) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(entityId));
        blockedEntities.remove(entityType);
    }
}