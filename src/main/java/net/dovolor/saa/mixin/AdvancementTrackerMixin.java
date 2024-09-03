package net.dovolor.saa.mixin;

import net.dovolor.saa.SpawnAfterAchievement;
import net.dovolor.saa.config.EntityBlockConfig;
import net.dovolor.saa.config.EntitySpawnConfig;
import net.dovolor.saa.manager.AchivementManager;
import net.dovolor.saa.manager.EntityManager;
import net.dovolor.saa.store.CustomAdvancementStorage;
import net.minecraft.advancement.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(PlayerAdvancementTracker.class)
public class AdvancementTrackerMixin {
    public PlayerAdvancementTracker tracker = (PlayerAdvancementTracker) (Object) this;
    @Inject(method = "grantCriterion", at = @At("HEAD"), cancellable = true)
    private void onAdvancementCompleted(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = ((PlayerAdvancementTrackerAccessor) tracker).getOwner();


        // check do anybody already complete achievement to not repeat message all the time
        MinecraftServer server = player.getServer();
        if(CustomAdvancementStorage.getAllCompletedAdvancements(server).contains(advancement.getId().toString())) {
            return;
        }
        CustomAdvancementStorage.saveCustomAdvancementData(server, advancement.getId().toString());

        // To allow spawn
        for(EntitySpawnConfig.SpawnConfig config: EntitySpawnConfig.spawnConfigs) {
            if(Objects.equals(config.getAchievement(), advancement.getId().toString())) {
                if(config.getMessages() != null) {
                    Random random = new Random();
                    int index = random.nextInt(config.getMessages().size());
                    String message = config.getMessages().get(index);

                    MutableText chatMessage = Text.literal(message);

                    if(config.getMessagesColor() != null) {
                        chatMessage = chatMessage.formatted(Formatting.byName(config.getMessagesColor().toLowerCase()));
                    }
                    // player.sendMessage(chatMessage, false);
                    server.getPlayerManager().broadcast(chatMessage, false);
                }
                for (String entity: config.getEntities()) {
                    EntityManager.getInstance().allowEntitySpawn(entity);
                }
            }
        }

        // To block spawn
        for(EntityBlockConfig.BlockConfig config: EntityBlockConfig.spawnConfigs) {
            if(Objects.equals(config.getAchievement(), advancement.getId().toString())) {
                if(config.getMessages() != null) {
                    Random random = new Random();
                    int index = random.nextInt(config.getMessages().size());
                    String message = config.getMessages().get(index);

                    MutableText chatMessage = Text.literal(message);

                    if(config.getMessagesColor() != null) {
                        chatMessage = chatMessage.formatted(Formatting.byName(config.getMessagesColor().toLowerCase()));
                    }
//                    player.sendMessage(chatMessage, false);
                    server.getPlayerManager().broadcast(chatMessage, false);
                }
                for (String entity: config.getEntities()) {
                    EntityManager.getInstance().blockEntitySpawn(entity);
                }
            }
        }
    }

}

