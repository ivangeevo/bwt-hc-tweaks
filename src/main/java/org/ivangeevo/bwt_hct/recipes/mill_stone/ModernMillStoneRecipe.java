package org.ivangeevo.bwt_hct.recipes.mill_stone;

import com.bwt.generation.EmiDefaultsGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ModernMillStoneRecipe implements Recipe<SingleCountMillStoneRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final Ingredient ingredient;
    protected final DefaultedList<ItemStack> results;

    public ModernMillStoneRecipe(String group, CraftingRecipeCategory category, Ingredient ingredient, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.results = DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.modernMillStoneBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(SingleCountMillStoneRecipeInput input, World world) {

        Optional<Integer> matchingCount = input.items().stream()
                .filter(ingredient::test)
                .map(ItemStack::getCount)
                .reduce(Integer::sum);
        return matchingCount.orElse(0) >= Arrays.stream(ingredient.getMatchingStacks()).count();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(ingredient);
        return defaultedList;
    }

    public List<ItemStack> getResults() {
        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack craft(SingleCountMillStoneRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return results.getFirst();
    }

    public static class Type implements RecipeType<ModernMillStoneRecipe>
    {
        public static final Type INSTANCE = new Type();
        public static final String ID = "mill_stone";
    }

    public static class Serializer implements RecipeSerializer<ModernMillStoneRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "mill_stone";

        protected static final MapCodec<ModernMillStoneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),

                        ItemStack.CODEC
                                .listOf()
                                .fieldOf("results")
                                .forGetter(ModernMillStoneRecipe::getResults)
                ).apply(instance, ModernMillStoneRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, ModernMillStoneRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<ModernMillStoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ModernMillStoneRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static ModernMillStoneRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            List<ItemStack> results = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return new ModernMillStoneRecipe(group, category, ingredient, results);
        }

        public static void write(RegistryByteBuf buf, ModernMillStoneRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getResults());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected Ingredient ingredient;
        protected DefaultedList<ItemStack> results = DefaultedList.of();
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder ingredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public JsonBuilder ingredient(ItemStack itemStack) {
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
            return this.ingredient(Ingredient.ofStacks(itemStack));
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(new ItemStack(item));
        }


        public ModernMillStoneRecipe.JsonBuilder results(ItemStack... itemStacks) {
            this.results.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public ModernMillStoneRecipe.JsonBuilder result(ItemStack itemStack) {
            this.results.add(itemStack);
            return this;
        }

        public ModernMillStoneRecipe.JsonBuilder result(Item item, int count) {
            this.results.add(new ItemStack(item, count));
            return this;
        }

        public ModernMillStoneRecipe.JsonBuilder result(Item item) {
            return this.result(item, 1);
        }


        @Override
        public JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
            this.criteria.put(string, advancementCriterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(Identifier recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getOutputItem() {
            return results.getFirst().getItem();
        }


        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);
            ModernMillStoneRecipe millStoneRecipe = new ModernMillStoneRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.results
            );
            exporter.accept(recipeId, millStoneRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }

        private void validate(Identifier recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}
