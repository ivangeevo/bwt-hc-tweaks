package org.btwr.bwt_hct.recipes.mill_stone;

import com.bwt.generation.EmiDefaultsGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ModernMillStoneRecipe implements Recipe<SingleCountMillStoneRecipeInput> {

    protected final String group;
    protected final CraftingBookCategory category;
    final Ingredient ingredient;
    protected final NonNullList<ItemStack> results;

    public ModernMillStoneRecipe(String group, CraftingBookCategory category, Ingredient ingredient, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.results = NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.modernMillStoneBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(SingleCountMillStoneRecipeInput input, Level world) {

        Optional<Integer> matchingCount = input.items().stream()
                .filter(ingredient::test)
                .map(ItemStack::getCount)
                .reduce(Integer::sum);
        return matchingCount.orElse(0) >= Arrays.stream(ingredient.getItems()).count();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
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

    public CraftingBookCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isSpecial() {
        return Recipe.super.isSpecial();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack assemble(SingleCountMillStoneRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider wrapperLookup) {
        return results.getFirst();
    }

    public static class Type implements RecipeType<ModernMillStoneRecipe> {
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
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),

                        ItemStack.CODEC
                                .listOf()
                                .fieldOf("results")
                                .forGetter(ModernMillStoneRecipe::getResults)
                ).apply(instance, ModernMillStoneRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ModernMillStoneRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<ModernMillStoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ModernMillStoneRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        public static ModernMillStoneRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            List<ItemStack> results = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return new ModernMillStoneRecipe(group, category, ingredient, results);
        }

        public static void write(RegistryFriendlyByteBuf buf, ModernMillStoneRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getResults());
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected Ingredient ingredient;
        protected NonNullList<ItemStack> results = NonNullList.create();
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder ingredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public JsonBuilder ingredient(ItemStack itemStack) {
            this.unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), RecipeProvider.has(itemStack.getItem()));
            return this.ingredient(Ingredient.of(itemStack));
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
        public JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
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
        public void addToDefaults(ResourceLocation recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getResult() {
            return results.getFirst().getItem();
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            ModernMillStoneRecipe millStoneRecipe = new ModernMillStoneRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.results
            );
            exporter.accept(recipeId, millStoneRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }

        private void validate(ResourceLocation recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }

}