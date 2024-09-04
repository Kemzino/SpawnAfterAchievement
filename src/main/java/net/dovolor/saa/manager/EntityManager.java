package net.dovolor.saa.manager;

import net.dovolor.saa.SpawnAfterAchievement;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class EntityManager {
    private static final String NON_NATURAL_TAG = "NonNaturalSpawn";
    private static EntityManager instance;

    private final Set<EntityType<?>> blockedEntities = new HashSet<>();

    private EntityManager() {
        ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
    }

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    private void onEntityLoad(Entity entity, World world) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        if(!isNaturalSpawn(entity, nbt)) {
            nbt.putBoolean(NON_NATURAL_TAG, true);
            entity.readNbt(nbt);
        }
        else if (blockedEntities.contains(entity.getType()) && isNaturalSpawn(entity, nbt) && entity.getCustomName() == null) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public static boolean isNaturalSpawn(Entity entity, NbtCompound nbt) {
        entity.writeNbt(nbt);

        if (nbt.contains(NON_NATURAL_TAG)) {
            return false;
        }

        boolean noPlayerNearby = entity.getWorld().getClosestPlayer(entity, 16) == null;
        return noPlayerNearby;
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