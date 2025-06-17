package org.ivangeevo.bwt_hct.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.ivangeevo.bwt_hct.loot.ModLootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.block.PistonExtensionBlock.FACING;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {


    //@Inject(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.BEFORE))
    private void beforeMoveCall(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
        //PistonBreakTracker.setPistonBreak(true);

    }

    //@Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private void beforeMoveCall(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        //PistonBreakTracker.setPistonBreak(true);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(state.getBlock().getLootTableKey());
            LootContextParameterSet lootContextParams = new LootContextParameterSet.Builder(serverWorld)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .add(LootContextParameters.BLOCK_STATE, state)
                    .add(ModLootContextParams.IS_PISTON_BREAK, true)
                    .build(LootContextTypes.EMPTY);

            LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld).build(LootContextTypes.BLOCK);
            //ObjectArrayList<ItemStack> objectArrayList = lootTable2.generateLoot(lootContextParameterSet);

            List<ItemStack> drops = lootTable.generateLoot(lootContextParams);
            for (ItemStack stack : drops) {
                Block.dropStack(world, pos.offset(state.get(PistonBlock.FACING)), stack);
            }
        }
    }


}
