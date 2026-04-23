package org.btwr.bwt_hct.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.shared_library.api.data.EntityAttachmentBase;
import org.btwr.shared_library.api.event.BTWREvents;

public class ModDataAttachments {

    public static final AttachmentType<RecentlyOnChoppingBlockCountdownData> RECENTLY_ON_CHOPPING_BLOCK_COUNTDOWN = AttachmentRegistry.create(
            Identifier.of(BWT_HCTMod.MOD_ID, "recently_on_chopping_block_countdown"),
            builder -> builder
                    .initializer(() -> new RecentlyOnChoppingBlockCountdownData(0))
                    .persistent(RecentlyOnChoppingBlockCountdownData.CODEC)
                    .syncWith(RecentlyOnChoppingBlockCountdownData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static void register() {
        BWT_HCTMod.LOGGER.info("Registering {} attachments", BWT_HCTMod.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized

        BTWREvents.LIVING_TICK.add(living -> {
            tickAndSync(RECENTLY_ON_CHOPPING_BLOCK_COUNTDOWN, living);
        });
    }

    private static <T extends Entity, A extends EntityAttachmentBase<T>> void tickAndSync(AttachmentType<A> type, LivingEntity entity) {
        A attachment = entity.getAttachedOrCreate(type);
        attachment.tick((T) entity);
        if (attachment.isDirty()) {
            entity.setAttached(type, attachment);
        }
    }
}
