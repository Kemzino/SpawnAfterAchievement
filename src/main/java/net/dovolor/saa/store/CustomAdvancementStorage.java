package net.dovolor.saa.store;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomAdvancementStorage {

    public static void saveCustomAdvancementData(MinecraftServer server, String advancementId) {

        ServerWorld world = server.getOverworld();
        File worldFolder = server.getSavePath(WorldSavePath.ROOT).toFile();
        File customFile = new File(worldFolder, "saa_all_completed_advancements.txt");

        try (FileWriter writer = new FileWriter(customFile, true)) {
            writer.write(advancementId + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getAllCompletedAdvancements(MinecraftServer server) {
        File worldFolder = server.getSavePath(WorldSavePath.ROOT).toFile();
        File customFile = new File(worldFolder, "saa_all_completed_advancements.txt");

        List<String> advancements = new ArrayList<>();

        if (customFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(customFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    advancements.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return advancements;
    }
}
