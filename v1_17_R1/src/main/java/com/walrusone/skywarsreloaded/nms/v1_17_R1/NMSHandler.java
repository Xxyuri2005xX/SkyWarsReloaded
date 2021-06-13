package com.walrusone.skywarsreloaded.nms.v1_17_R1;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.signs.SWRSign;
import com.walrusone.skywarsreloaded.nms.NMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerPhase;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.entity.TileEntityEnderChest;
import org.bukkit.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class NMSHandler implements NMS {

    public boolean removeFromScoreboardCollection(Scoreboard scoreboard) {
        return false;
    }

    @Override
    public SWRSign createSWRSign(String name, org.bukkit.Location location) {
        return new SWRSign163(name, location);
    }

    public void respawnPlayer(Player player) {
        ((CraftServer) Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), false);
    }

    public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
        Particle particle = Particle.valueOf(type);
        for (Player player : world.getPlayers()) {
            player.spawnParticle(particle, x, y, z, amount, offsetX, offsetY, offsetZ, data);
        }
    }

    public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, FireworkEffect.Type type) {
        return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
    }

    public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        }
        if (title != null) {
            title = title.replaceAll("%player%", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
        }
        player.sendTitle(title, subtitle, fadein, stay, fadeout);
    }

    public void sendActionBar(Player player, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(msg).create());
    }

    public String getItemName(ItemStack item) {
        return item.getItemMeta() == null ? null : item.getItemMeta().getDisplayName();
    }

    public void playGameSound(Location loc, String sound, float volume, float pitch, boolean customSound) {
        if (loc.getWorld() == null) return;
        if (customSound) {
            loc.getWorld().playSound(loc, sound, volume, pitch);
        } else {
            loc.getWorld().playSound(loc, Sound.valueOf(sound), volume, pitch);
        }
    }

    public ItemStack getMainHandItem(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public ItemStack getOffHandItem(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    public ItemStack getItemStack(Material material, List<String> lore, String message) {
        ItemStack addItem = new ItemStack(material, 1);
        ItemMeta addItemMeta = addItem.getItemMeta();
        assert addItemMeta != null;
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        addItemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        addItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addItem.setItemMeta(addItemMeta);
        return addItem;
    }

    public ItemStack getItemStack(ItemStack item, List<String> lore, String message) {
        ItemStack addItem = item.clone();
        ItemMeta addItemMeta = addItem.getItemMeta();
        assert addItemMeta != null;
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        addItemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        addItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addItem.setItemMeta(addItemMeta);
        return addItem;
    }

    public boolean isValueParticle(String string) {
        try {
            Particle.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public void updateSkull(Skull skull, java.util.UUID uuid) {
        if (skull.getType().equals(Material.valueOf("SKELETON_SKULL"))) {
            Block block = skull.getBlock();
            block.setType(Material.valueOf("PLAYER_HEAD"));
            Skull s = (Skull) block.getState();
            s.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        } else {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }
    }

    public void setMaxHealth(Player player, int health) {
        AttributeInstance attr = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        assert attr != null;
        attr.setBaseValue(health);
    }

    public void spawnDragon(World world, Location loc) {
        WorldServer w = ((CraftWorld) world).getHandle();
        EntityEnderDragon dragon = new EntityEnderDragon(EntityTypes.v, w.getMinecraftWorld());
        dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.i);
        dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), w.getRandom().nextFloat() * 360.0F, 0.0F);
        w.addEntity(dragon);
    }

    public org.bukkit.entity.Entity spawnFallingBlock(Location loc, Material mat, boolean damage) {
        if (loc.getWorld() == null) return null;
        FallingBlock block = loc.getWorld().spawnFallingBlock(loc, new org.bukkit.material.MaterialData(mat));
        block.setDropItem(false);
        EntityFallingBlock fb = ((CraftFallingBlock) block).getHandle();
        fb.ap = damage;
        return block;
    }

    public void playEnderChestAction(Block block, boolean open) {
        Location location = block.getLocation();
        if (location.getWorld() == null) return;
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityEnderChest ec = (TileEntityEnderChest) world.getTileEntity(position);
        assert (ec != null);
        world.playBlockAction(position, ec.getBlock().getBlock(), 1, open ? 1 : 0);
    }

    public void setEntityTarget(org.bukkit.entity.Entity ent, Player player) {
        EntityCreature entity = (EntityCreature) ((CraftEntity) ent).getHandle();
        entity.setGoalTarget(((CraftPlayer) player).getHandle(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }

    public void updateSkull(org.bukkit.inventory.meta.SkullMeta meta1, Player player) {
        meta1.setOwningPlayer(player);
    }

    public ChunkGenerator getChunkGenerator() {
        return new ChunkGenerator() {
            @Override
            public final ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid chunkGenerator) {
                ChunkData chunkData = createChunkData(world);
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        chunkGenerator.setBiome(i, j, org.bukkit.block.Biome.valueOf("THE_VOID"));
                    }
                }
                return chunkData;
            }
        };
    }

    public boolean checkMaterial(FallingBlock fb, Material mat) {
        return fb.getMaterial().equals(mat);
    }


    public org.bukkit.scoreboard.Objective getNewObjective(Scoreboard scoreboard, String criteria, String DisplayName) {
        return scoreboard.registerNewObjective(DisplayName, criteria);
        //return scoreboard.registerNewObjective(DisplayName, criteria, DisplayName);
    }

    public void setGameRule(World world, String ruleName, String value) {
        // Handle bools
        Boolean valueBool = null;
        if (value.equalsIgnoreCase("true")) valueBool = true;
        else if (value.equalsIgnoreCase("false")) valueBool = false;
        // Handle ints
        Integer valueInt = null;
        if (valueBool == null) {
            try {
                valueInt = Integer.parseInt(value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // Apply
        try {
            if (valueBool == null) {
                    GameRule<Integer> gameRule = (GameRule<Integer>) GameRule.getByName(ruleName);
                    if (gameRule == null || valueInt == null)
                        throw new Exception("Invalid GameRule or value provided: " + ruleName + " -> " + value);
                    world.setGameRule(gameRule, valueInt);
            } else {
                    GameRule<Boolean> gameRule = (GameRule<Boolean>) GameRule.getByName(ruleName);
                    if (gameRule == null)
                        throw new Exception("Invalid GameRule: " + ruleName);
                    world.setGameRule(gameRule, valueBool);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean headCheck(Block h1) {
        return (h1.getType() == Material.valueOf("PLAYER_WALL_HEAD")) || (h1.getType() == Material.valueOf("PLAYER_HEAD")) || (h1.getType() == Material.valueOf("SKELETON_SKULL"));
    }

    public ItemStack getBlankPlayerHead() {
        return new ItemStack(Material.valueOf("PLAYER_HEAD"), 1);
    }

    public int getVersion() {
        return 13;
    }

    public ItemStack getMaterial(String item) {
        if (item.equalsIgnoreCase("SKULL_ITEM"))
            return new ItemStack(Material.valueOf("SKELETON_SKULL"), 1);
        if (item.equalsIgnoreCase("ENDER_PORTAL_FRAME"))
            return new ItemStack(Material.valueOf("END_PORTAL_FRAME"), 1);
        if (item.equalsIgnoreCase("WORKBENCH"))
            return new ItemStack(Material.valueOf("CRAFTING_TABLE"), 1);
        if (item.equalsIgnoreCase("IRON_FENCE"))
            return new ItemStack(Material.valueOf("IRON_BARS"), 1);
        if (item.equalsIgnoreCase("REDSTONE_COMPARATOR"))
            return new ItemStack(Material.valueOf("COMPARATOR"));
        if (item.equalsIgnoreCase("SIGN_POST"))
            return new ItemStack(Material.valueOf("BIRCH_SIGN"));
        if (item.equalsIgnoreCase("STONE_PLATE"))
            return new ItemStack(Material.valueOf("STONE_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("IRON_PLATE"))
            return new ItemStack(Material.valueOf("HEAVY_WEIGHTED_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("GOLD_PLATE"))
            return new ItemStack(Material.valueOf("LIGHT_WEIGHTED_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("MOB_SPAWNER"))
            return new ItemStack(Material.valueOf("SPAWNER"));
        if (item.equalsIgnoreCase("SNOW_BALL")) {
            return new ItemStack(Material.valueOf("SNOWBALL"));
        }
        return new ItemStack(Material.AIR, 1);
    }


    public ItemStack getColorItem(String mat, byte color) {
        String col = getColorFromByte(color);
        if (mat.equalsIgnoreCase("wool"))
            return new ItemStack(Material.valueOf(col + "_WOOL"), 1);
        if (mat.equalsIgnoreCase("stained_glass"))
            return new ItemStack(Material.valueOf(col + "_STAINED_GLASS"), 1);
        if (mat.equalsIgnoreCase("banner")) {
            return new ItemStack(Material.valueOf(col + "_BANNER"), 1);
        }
        return new ItemStack(Material.valueOf(col + "_STAINED_GLASS"), 1);}

    private String getColorFromByte(byte color) {
        switch (color) {
            case 0:
                return "WHITE";
            case 1:
                return "ORANGE";
            case 2:
                return "MAGENTA";
            case 3:
                return "LIGHT_BLUE";
            case 4:
                return "YELLOW";
            case 5:
                return "LIME";
            case 6:
                return "PINK";
            case 7:
                return "GRAY";
            case 8:
                return "LIGHT_GRAY";
            case 9:
                return "CYAN";
            case 10:
                return "PURPLE";
            case 11:
                return "BLUE";
            case 12:
                return "BROWN";
            case 13:
                return "GREEN";
            case 14:
                return "RED";
            case 16:
                return "BLACK";
        }
        return "WHITE";
    }


    public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte) {
        world.getBlockAt(x, y, z).setType(mat);
    }


    public void deleteCache() {
    }


    public Block getHitBlock(ProjectileHitEvent event) {
        return event.getHitBlock();
    }

    @Override
    public void sendJSON(Player sender, String json) {
        final IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a(json);
        final PacketPlayOutChat chat = new PacketPlayOutChat(icbc, net.minecraft.network.chat.ChatMessageType.a, (sender).getUniqueId());
        ((CraftPlayer) sender).getHandle().b.sendPacket(chat);
    }

    @Override
    public boolean isHoldingTotem(Player player) {
        return player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ||
                player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING);
    }

    @Override
    public void applyTotemEffect(Player player) {
        PlayerInventory pInv = player.getInventory();
        ItemStack mainHand = pInv.getItemInMainHand();
        ItemStack offHand = pInv.getItemInOffHand();
        // Consume item
        if (mainHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
            pInv.setItemInMainHand(new ItemStack(Material.AIR));
        } else if (offHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
            pInv.setItemInOffHand(new ItemStack(Material.AIR));
        }
        // Apply potion effects (version specific)
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 40, 0, false, true));
        // On screen effect
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        // Particles
        new BukkitRunnable() {
            byte count = 0;

            @Override
            public void run() {
                if (count > 30) {
                    this.cancel();
                    return;
                } else {
                    count++;
                }
                player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 10, 0.1, 0.1, 0.1, 0.5);
            }
        }.runTaskTimer(SkyWarsReloaded.get(), 0, 1);
    }
}
