package org.ivangeevo.bwt_hct.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

public class ModLootContextTypes
{
	private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();
	public static final Codec<LootContextType> CODEC = Identifier.CODEC
		.comapFlatMap(
			id -> Optional.ofNullable(MAP.get(id))
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + id + "'")),
			MAP.inverse()::get
		);
	public static final LootContextType EMPTY = register("empty", builder -> {
	});
	public static final LootContextType PISTON_BREAK = register(
		"piston_break", builder -> builder.require(LootContextParameters.ORIGIN).require(ModLootContextParams.IS_PISTON_BREAK).allow(LootContextParameters.THIS_ENTITY)
	);

	private static LootContextType register(String name, Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		LootContextType lootContextType = builder.build();
		Identifier identifier = Identifier.ofVanilla(name);
		LootContextType lootContextType2 = MAP.put(identifier, lootContextType);
		if (lootContextType2 != null) {
			throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
		} else {
			return lootContextType;
		}
	}
}
