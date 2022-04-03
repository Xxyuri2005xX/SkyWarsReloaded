package net.gcnt.skywarsreloaded.game.kits;

import net.gcnt.skywarsreloaded.SkyWarsReloaded;
import net.gcnt.skywarsreloaded.utils.Item;
import net.gcnt.skywarsreloaded.utils.properties.FolderProperties;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractKitManager implements KitManager {

    public final SkyWarsReloaded plugin;
    public HashMap<String, SWKit> kits;

    public AbstractKitManager(SkyWarsReloaded plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
    }

    @Override
    public SWKit getKitByName(String kitId) {
        return kits.getOrDefault(kitId, null);
    }

    @Override
    public void deleteKit(String kitId) {
        // todo kit deletion here
    }

    @Override
    public void loadAllKits() {
        File dir = new File(plugin.getDataFolder(), FolderProperties.KITS_FOLDER.toString());

        // Sanity checks
        if (!dir.exists()) return;

        // Reset all currently loaded kits
        this.kits.clear();

        // Load all from directory
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {

            // Sanity checks
            if (file.isDirectory() || !file.getName().endsWith(".yml")) continue;
            String name = file.getName().replace(".yml", "");
            if (getKitByName(name) != null) continue;

            // Load data & store in cache
            SWKit kit = initKit(name);
            kits.put(name, kit);

            kit.loadData();
            plugin.getLogger().info("Loaded kit '" + name + "'.");
        }
    }

    @Override
    public List<SWKit> getKits() {
        return new ArrayList<>(kits.values());
    }

    @Override
    public void createDefaultsIfNotPresent() {
        File dir = new File(plugin.getDataFolder(), FolderProperties.KITS_FOLDER.toString());

        // Sanity checks
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        // List all from directory
        File[] files = dir.listFiles();
        if (files == null) return;

        // Add default files on first install
        if (files.length < 1) {
            this.createKit("default", null);
        }
    }

    @Override
    public SWKit createKit(String id, @Nullable Item icon) {
        if (getKitByName(id) != null) return null;
        SWKit kit = initKit(id);
        if (icon != null) kit.setIcon(icon);
        kit.saveData();
        kits.put(id, kit);
        return kit;
    }

    @Override
    public abstract SWKit initKit(String id);
}