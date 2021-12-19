package net.gcnt.skywarsreloaded.bukkit.game.loader;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import net.gcnt.skywarsreloaded.SkyWarsReloaded;
import net.gcnt.skywarsreloaded.bukkit.game.BukkitGameWorld;
import net.gcnt.skywarsreloaded.game.GameTemplate;
import net.gcnt.skywarsreloaded.game.GameWorld;
import net.gcnt.skywarsreloaded.utils.SWCoord;
import net.gcnt.skywarsreloaded.utils.properties.FolderProperties;
import net.gcnt.skywarsreloaded.utils.properties.InternalProperties;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class SchemWorldLoader extends BukkitWorldLoader {

    private Biome voidBiome;

    public SchemWorldLoader(SkyWarsReloaded plugin) {
        super(plugin);

        try {
            voidBiome = Biome.valueOf("THE_VOID");
        } catch (Exception e) {
            voidBiome = Biome.valueOf("VOID");
        }

    }

    @Override
    public CompletableFuture<Boolean> generateWorldInstance(GameWorld gameWorld) throws IllegalStateException, IllegalArgumentException {
        this.createEmptyWorld(gameWorld);
        return CompletableFuture.completedFuture(loadSchematic(gameWorld)); // todo check if this works
    }

    @Override
    public void createEmptyWorld(GameWorld gameWorld) {
        WorldCreator creator = new WorldCreator(gameWorld.getWorldName());
        creator.generateStructures(false);
        creator.generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                ChunkData chunkData = createChunkData(world);
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        biome.setBiome(i, j, voidBiome);
                    }
                }
                return chunkData;
            }
        });
        creator.createWorld();
    }

    /**
     * Pastes the schematic into the world.
     *
     * @param gameWorld GameWorld to paste into
     * @return true if the schematic existed
     */
    public boolean loadSchematic(GameWorld gameWorld) throws IllegalStateException, IllegalArgumentException {
        File schemFolder = new File(plugin.getDataFolder(), FolderProperties.WORLD_SCHEMATICS_FOLDER.toString());
        String schemFileName = gameWorld.getTemplate().getName() + ".schem";

        File schemFile = new File(schemFolder, schemFileName);
        if (!schemFile.exists()) throw new IllegalArgumentException("Schematic file does not exist.");

        Clipboard clip = plugin.getSchematicManager().getSchematic(schemFolder, schemFileName);
        if (clip == null) return false; // todo throw error?

        World world = ((BukkitGameWorld) gameWorld).getBukkitWorld();
        if (world == null) {
            throw new IllegalStateException(String.format(
                    "GameWorld %s$ doesn't have a valid minecraft world. Check the console for other errors!",
                    gameWorld.getId()
            ));
        }

        plugin.getSchematicManager().pasteSchematic(clip, new BukkitWorld(world), BlockVector3.at(0, 0, 0), true);
        return true;
    }

    @Override
    public void deleteWorldInstance(GameWorld gameWorld) {
        World world = ((BukkitGameWorld) gameWorld).getBukkitWorld();
        if (world == null) {
            return;
        }

        final SWCoord loc = plugin.getDataConfig().getCoord("lobby");
        final Location bukkitLoc = (loc != null && loc.world() != null) ? new Location(Bukkit.getWorld(loc.world().getName()), loc.xPrecise(), loc.yPrecise(), loc.zPrecise(), loc.yaw(), loc.pitch()) : new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

        for (Player player : world.getPlayers()) {
            player.teleport(bukkitLoc);
        }

        Bukkit.unloadWorld(world, false);
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException e) {
            plugin.getLogger().error(String.format("Failed to delete world %s. (%s)", world.getName(), e.getClass().getName() + ": " + e.getLocalizedMessage()));
        }
    }

    @Override
    public void deleteMap(GameTemplate gameTemplate, boolean forceUnloadInstances) {
        File schemFolder = new File(plugin.getDataFolder(), FolderProperties.WORLD_SCHEMATICS_FOLDER.toString());
        String schemFileName = gameTemplate.getName() + ".schem";

        File schemFile = new File(schemFolder, schemFileName);
        if (schemFile.exists()) {
            try {
                FileUtils.forceDelete(schemFile);
            } catch (IOException e) {
                plugin.getLogger().error(String.format("Failed to delete schematic file %s. (%s)", schemFileName, e.getClass().getName() + ": " + e.getLocalizedMessage()));
            }
        }
    }

    @Override
    public void createBasePlatform(GameWorld gameWorld) {
        World world = ((BukkitGameWorld) gameWorld).getBukkitWorld();
        if (world == null) return;

        world.getBlockAt(
                InternalProperties.MAP_CREATE_PLATFORM_X,
                InternalProperties.MAP_CREATE_PLATFORM_Y,
                InternalProperties.MAP_CREATE_PLATFORM_Z
        ).setType(Material.STONE);
    }

}