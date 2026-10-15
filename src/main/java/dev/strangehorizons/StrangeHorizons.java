package dev.strangehorizons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;

/**
 * Original experimental terrain zones, designed independently for Minecraft 26.3.
 * It does not reuse the Unstable Generation JAR or its implementation.
 */
public final class StrangeHorizons implements ModInitializer {
    private static final int SEA_LEVEL = 92;
    private static final int MAX_BUILD_Y = 240;
    private static final int MIN_BUILD_Y = -32;

    @Override
    public void onInitialize() {
        // This listener is called for newly generated chunks only. It deliberately
        // does not rewrite chunks when they are reloaded, protecting later builds.
        ServerChunkEvents.CHUNK_GENERATE.register(StrangeHorizons::onChunkGenerate);
    }

    private static void onChunkGenerate(ServerLevel level, LevelChunk chunk) {
        if (level.dimension() != Level.OVERWORLD) return;

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        Region region = findRegion(minX + 8, minZ + 8);
        if (region == null) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = minX + lx;
                int z = minZ + lz;
                int top = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, lx, lz) - 1;
                top = Math.max(level.getMinY() + 3, top);
                int target = region.height(x, z, top);
                target = Math.max(MIN_BUILD_Y, Math.min(MAX_BUILD_Y, target));

                // Shape the existing column into an original, region-specific profile.
                if (target > top) {
                    for (int y = top + 1; y <= target; y++) {
                        BlockState state = (y >= target - 2) ? region.surface() : region.filler();
                        chunk.setBlockState(pos.set(x, y, z), state, false);
                    }
                } else if (target < top) {
                    for (int y = target + 1; y <= top; y++) {
                        chunk.setBlockState(pos.set(x, y, z), Blocks.AIR.defaultBlockState(), false);
                    }
                }
                chunk.setBlockState(pos.set(x, target, z), region.surface(), false);

                if (region == Region.ENDLESS_WATER) {
                    for (int y = target + 1; y <= SEA_LEVEL; y++) {
                        chunk.setBlockState(pos.set(x, y, z), Blocks.WATER.defaultBlockState(), false);
                    }
                } else if (region == Region.VEILED_RIDGE && (hash(x, z) & 15) == 0) {
                    int cloudY = Math.min(MAX_BUILD_Y, target + 24 + (int) (Math.abs(Math.sin(x * 0.013) * Math.cos(z * 0.011)) * 12));
                    if (cloudY > target + 2) {
                        chunk.setBlockState(pos.set(x, cloudY, z), Blocks.COBWEB.defaultBlockState(), false);
                    }
                }
            }
        }
    }

    private static Region findRegion(int x, int z) {
        if (inside(x, z, 62_000, 0, 7_000, 5_000)) return Region.HIGHPLATEAU;
        if (inside(x, z, -250_000, 0, 6_000, 5_000)) return Region.ENDLESS_WATER;
        if (inside(x, z, -100_000, 0, 5_000, 5_000)) return Region.VEILED_RIDGE;
        if (inside(x, z, 12_550_821, 0, 4_000, 4_000)) return Region.FRACTURE;
        if (inside(x, z, 500_000, 0, 4_000, 4_000)) return Region.SPOKE_FIELDS;
        return null;
    }

    private static boolean inside(int x, int z, int centerX, int centerZ, int halfWidthX, int halfWidthZ) {
        return Math.abs((long) x - centerX) <= halfWidthX && Math.abs((long) z - centerZ) <= halfWidthZ;
    }

    private static int hash(int x, int z) {
        int h = x * 0x45d9f3b ^ z * 0x119de1f3;
        h = (h ^ (h >>> 16)) * 0x45d9f3b;
        h ^= h >>> 16;
        return h;
    }

    private enum Region {
        HIGHPLATEAU {
            @Override int height(int x, int z, int old) {
                double wave = Math.sin(x * 0.0017) * Math.cos(z * 0.0021);
                double ridge = Math.sin((x + z) * 0.00055) * 0.5;
                return 82 + (int) (wave * 58 + ridge * 30);
            }
            @Override BlockState surface() { return Blocks.GRASS_BLOCK.defaultBlockState(); }
            @Override BlockState filler() { return Blocks.STONE.defaultBlockState(); }
        },
        ENDLESS_WATER {
            @Override int height(int x, int z, int old) {
                return 43 + (int) (Math.sin(x * 0.0015) * 5 + Math.cos(z * 0.0019) * 4);
            }
            @Override BlockState surface() { return Blocks.SAND.defaultBlockState(); }
            @Override BlockState filler() { return Blocks.SANDSTONE.defaultBlockState(); }
        },
        VEILED_RIDGE {
            @Override int height(int x, int z, int old) {
                double rolling = Math.sin(x * 0.004) * 10 + Math.cos(z * 0.0033) * 12;
                double ridge = (1.0 - Math.abs(Math.sin((x - z) * 0.005))) * 29;
                return 67 + (int) (rolling + ridge);
            }
            @Override BlockState surface() { return Blocks.PACKED_MUD.defaultBlockState(); }
            @Override BlockState filler() { return Blocks.STONE.defaultBlockState(); }
        },
        FRACTURE {
            @Override int height(int x, int z, int old) {
                double wave = Math.abs(Math.sin(x * 0.008) * Math.cos(z * 0.0085));
                double saw = 1.0 - Math.abs(Math.sin((x + z) * 0.018));
                return 58 + (int) (Math.pow(wave, 1.4) * 85 + saw * 60);
            }
            @Override BlockState surface() { return Blocks.DEEPSLATE.defaultBlockState(); }
            @Override BlockState filler() { return Blocks.COBBLED_DEEPSLATE.defaultBlockState(); }
        },
        SPOKE_FIELDS {
            @Override int height(int x, int z, int old) {
                double radius = Math.sqrt((double)x * x + (double)z * z);
                double angle = Math.atan2(z, x);
                double spokes = Math.cos(angle * 10.0 + radius * 0.0004);
                double terraces = Math.sin(radius * 0.008) * 8;
                return 65 + (int) (terraces + (spokes > 0.78 ? 35 : spokes * 7));
            }
            @Override BlockState surface() { return Blocks.MOSS_BLOCK.defaultBlockState(); }
            @Override BlockState filler() { return Blocks.STONE.defaultBlockState(); }
        };

        abstract int height(int x, int z, int old);
        abstract BlockState surface();
        abstract BlockState filler();
    }
}
