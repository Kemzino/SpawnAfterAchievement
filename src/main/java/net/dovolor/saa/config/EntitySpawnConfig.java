package net.dovolor.saa.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dovolor.saa.SpawnAfterAchievement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.entity.EntityType;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntitySpawnConfig {
    public static List<SpawnConfig> spawnConfigs;
    private static final String CONFIG_PATH = "config/spawn-after-achievement/spawn-after-achievement.json";

    public static void loadConfig() {
        Gson gson = new GsonBuilder().create();
        File configFile = new File(CONFIG_PATH);

        try {
            if (!configFile.exists()) {
                createDefaultConfig(gson, configFile);
            }

            try (FileInputStream fis = new FileInputStream(configFile)) {
                Type listType = new TypeToken<List<SpawnConfig>>() {}.getType();
                spawnConfigs = gson.fromJson(new InputStreamReader(fis, StandardCharsets.UTF_8), listType);
            }
            for (SpawnConfig config : spawnConfigs) {
                SpawnAfterAchievement.LOGGER.info("Loaded spawn config: " + config.getAchievement());
                for (String entity : config.getEntities()) {
                    SpawnAfterAchievement.LOGGER.info("Loaded spawn config: " + entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig(Gson gson, File configFile) throws IOException {
        Map<String, Object> defaultConfig = Map.of(
                "achievement", "minecraft:story/enter_the_nether",
                "entities", List.of("minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk"),
                "messages", Map.of(
                        "en_us", List.of("Now zombie arise in the dark", "Beware of the night!"),
                        "uk_ua", List.of("Тепер зомбі з'являються в темряві", "Остерігайтесь ночі!")
                ),
                "messagesColor", "GOLD"
        );

        List<Map<String, Object>> defaultConfigs = List.of(defaultConfig);

        Files.createDirectories(Paths.get(configFile.getParent()));

        try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            gson.toJson(defaultConfigs, writer);
        }
    }


    public static List<EntityType<?>> getEntitiesForAchievement(Advancement advancement) {
        for (SpawnConfig config : spawnConfigs) {
            if (config.getAchievement().equals(advancement.getId().toString())) {
                return config.getEntityTypes();
            }
        }
        return List.of();
    }

    public static class SpawnConfig {
        private String achievement;
        private List<String> entities;
        private Map<String, List<String>> messages;
        private String messagesColor;

        public String getAchievement() {
            return achievement;
        }

        public List<String> getEntities() {
            return entities;
        }

        public List<String> getMessages() {
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
