package net.dovolor.saa.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dovolor.saa.SpawnAfterAchievement;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.entity.EntityType;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityBlockConfig {
    public static List<EntityBlockConfig.BlockConfig> spawnConfigs;
    private static final String CONFIG_PATH = "config/spawn-after-achievement/block-after-achievement.json";

    public static void loadConfig() {
        Gson gson = new GsonBuilder().create();
        File configFile = new File(CONFIG_PATH);

        try {
            if (!configFile.exists()) {
                createDefaultConfig(gson, configFile);
            }

            try (FileInputStream fis = new FileInputStream(configFile)) {
                Type listType = new TypeToken<List<EntityBlockConfig.BlockConfig>>() {}.getType();
                spawnConfigs = gson.fromJson(new InputStreamReader(fis, StandardCharsets.UTF_8), listType);
            }
            for (EntityBlockConfig.BlockConfig config : spawnConfigs) {
                SpawnAfterAchievement.LOGGER.info("Loaded block config: " + config.getAchievement());
                if(config.getEntities() != null) {
                    for (String entity : config.getEntities()) {
                        SpawnAfterAchievement.LOGGER.info("Loaded block config: " + entity);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig(Gson gson, File configFile) throws IOException {
        Map<String, Object> defaultConfig = Map.of(
                "achievement", "minecraft:story/cure_zombie_villager",
                "entities", List.of("minecraft:zombie_villager"),
                "messages", Map.of(
                        "en_us", List.of("He is one of the last who was infected"),
                        "uk_ua", List.of("Він один з останніх, хто заразився")
                ),
                "messagesColor", "GOLD",
                "example", true
        );

        List<Map<String, Object>> defaultConfigs = List.of(defaultConfig);

        Files.createDirectories(Paths.get(configFile.getParent()));

        try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            gson.toJson(defaultConfigs, writer);
        }
    }


    public static List<EntityType<?>> getEntitiesForAchievement(Advancement advancement) {
        for (EntityBlockConfig.BlockConfig config : spawnConfigs) {
            if (config.getAchievement().equals(advancement.getId().toString())) {
                return config.getEntityTypes();
            }
        }
        return List.of();
    }

    public static class BlockConfig {
        private String achievement;
        private List<String> entities;
        private Map<String, List<String>> messages;
        private String messagesColor;
        private Boolean example;

        public String getAchievement() {
            if (example) {
                return null;
            }
            return achievement;
        }

        public List<String> getEntities() {
            if (example) {
                return null;
            }
            return entities;
        }

        public List<String> getMessages() {
            if (example) {
                return null;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            LanguageManager languageManager = client.getLanguageManager();

            String currentLanguageCode = languageManager.getLanguage();
            if (messages.get(currentLanguageCode) != null) {
                return messages.get(currentLanguageCode);
            } else {
                return messages.get("en_us");
            }
        }

        public String getMessagesColor() {
            if (example) {
                return null;
            }
            return messagesColor;
        }

        public List<EntityType<?>> getEntityTypes() {
            List<EntityType<?>> entityTypes = new ArrayList<>();
            for (String entityName : entities) {
                Optional<EntityType<?>> optionalEntityType = EntityType.get(entityName);
                if (optionalEntityType.isPresent()) {
                    entityTypes.add(optionalEntityType.get());
                }
            }
            return entityTypes;
        }
    }
}
