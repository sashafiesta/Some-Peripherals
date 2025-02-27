package net.spaceeye.someperipherals.blocks.raycaster

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.spaceeye.someperipherals.SomePeripheralsConfig
import net.spaceeye.someperipherals.blockentities.RaycasterBlockEntity
import net.spaceeye.someperipherals.raycasting.PosCache
import java.util.*

class RaycasterBaseBlock(properties: Properties): BaseEntityBlock(properties) {
    val pos_cache = PosCache()
    private var ticks = 0

    init {
        //TODO learn how to make a new blockstate property instead of whatever this is
        registerDefaultState(defaultBlockState()
            .setValue(BlockStateProperties.FACING, Direction.SOUTH)
            .setValue(BlockStateProperties.POWERED, false) // Yeah im not sure how to make a new block state lol
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
               .add(BlockStateProperties.POWERED)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val direction = if (ctx.player?.isCrouching == true) { ctx.nearestLookingDirection } else { ctx.nearestLookingDirection.opposite }
        return defaultBlockState().setValue(BlockStateProperties.FACING, direction)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return RaycasterBlockEntity(pos, state)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: Random) {
        super.tick(state, level, pos, random)
        if (ticks >= SomePeripheralsConfig.SERVER.COMMON.RAYCASTER_SETTINGS.save_cache_for_ticks) {
            pos_cache.clear()
            ticks = 0
        }
        ticks += 1
    }
}