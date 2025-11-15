package org.ivangeevo.bwt_hct.data;

import org.ivangeevo.bwt_hct.BWT_HCTMod;

public class ModDataAttachments {

    /**
    public static final AttachmentType<DragonOrbData> XP_DRAGON_ORB = AttachmentRegistry.create(
            Identifier.of(BWT_HCTMod.MOD_ID, "xp_dragon_orb"),
            builder -> builder
                    .initializer(() -> new DragonOrbData(false))
                    .persistent(DragonOrbData.CODEC)
                    .syncWith(DragonOrbData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );
     **/

    public static void register() {
        BWT_HCTMod.LOGGER.info("Registering {} attachments", BWT_HCTMod.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
}
