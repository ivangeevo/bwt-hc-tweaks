package org.btwr.bwt_hct;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class ModAttachments
{
    private static final Identifier DRAGON_ORB_ID = Identifier.of(BWT_HCTMod.MOD_ID, "dragon_orb");
    public static final AttachmentType<Boolean> DRAGON_ORB = AttachmentRegistry.createPersistent(DRAGON_ORB_ID, Codec.BOOL);

}
