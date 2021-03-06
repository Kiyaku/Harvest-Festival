package joshie.harvest.mining;

import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static joshie.harvest.mining.HFMining.MINE_WORLD;

public class MiningProvider extends WorldProvider {
    private MineManager manager = null;

    @Override
    public void createBiomeProvider() {
        biomeProvider = new BiomeProviderSingle(Biomes.VOID);
        hasNoSky = true;
        NBTTagCompound tag = worldObj.getWorldInfo().getDimensionData(MINE_WORLD);
        manager = worldObj instanceof WorldServer ? new MineManager(tag.getCompoundTag("MineManager")) : null;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new MiningChunk(worldObj, worldObj.getSeed(), manager);
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return getSpawnCoordinateForMine(0, 1);
    }

    public BlockPos getSpawnCoordinateForMine(int mineID, int floor) {
        return manager.getSpawnCoordinateForMine(mineID, floor);
    }

    public boolean areCoordinatesGenerated(int mineID, int floor) {
        return manager.areCoordinatesGenerated(mineID, floor);
    }

    @SideOnly(Side.CLIENT)
    public boolean isSkyColored() {
        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public DimensionType getDimensionType() {
        return MINE_WORLD;
    }

    @Override
    public void onWorldSave() {
        NBTTagCompound tag = new NBTTagCompound();
        if (manager != null) {
            tag.setTag("MineManager", manager.getCompound());
        }

        worldObj.getWorldInfo().setDimensionData(MINE_WORLD, tag);
    }
}
