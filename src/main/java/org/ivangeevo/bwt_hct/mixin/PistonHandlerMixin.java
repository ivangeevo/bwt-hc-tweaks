package org.ivangeevo.bwt_hct.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.loot.ModLootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {

    @Shadow @Final private Direction motionDirection;

    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;", shift = At.Shift.AFTER))
    private void afterPistonDestroyBlock(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        World world = ((PistonHandlerAccessor) this).getWorld();

        // --- CUSTOM LOOT DROP START ---
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockState state = serverWorld.getBlockState(pos);
            LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(state.getBlock().getLootTableKey());
            LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
                    .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                    .add(LootContextParameters.BLOCK_STATE, state)
                    .add(ModLootContextParams.IS_PISTON_BREAK, true)
                    .build(LootContextTypes.EMPTY);

            List<ItemStack> drops = lootTable.generateLoot(lootContextParameterSet);
            for (ItemStack stack : drops) {
                Block.dropStack(world, pos.offset(this.motionDirection), stack);
            }
        }
        // --- CUSTOM LOOT DROP END ---
    }

}
