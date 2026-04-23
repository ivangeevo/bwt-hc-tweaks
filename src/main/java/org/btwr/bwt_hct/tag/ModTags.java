package org.btwr.bwt_hct.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;

public class ModTags {

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK = createTag(
                "increased_skull_drop_rate_from_chopping_block"
        );

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BWT_HCTMod.MOD_ID, name));
        }
    }
}
