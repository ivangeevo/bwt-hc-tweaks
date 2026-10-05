package org.btwr.bwt_hct.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.btwr.bwt_hct.BWT_HCTMod;

public class ModTags {

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK = createTag(
                "increased_skull_drop_rate_from_chopping_block"
        );

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, name));
        }
    }
}
