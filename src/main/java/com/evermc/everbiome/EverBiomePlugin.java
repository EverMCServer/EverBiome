package com.evermc.everbiome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.tinyprotocol.TinyProtocol;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.channel.Channel;

public class EverBiomePlugin extends JavaPlugin {

    private Map<String, Integer> customBiomes = new ConcurrentHashMap<>();
    private List<Config> config = List.of();

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // load biomes after server fully started(after datapacks loaded)
            try {
                reload();
            } catch (Exception e) {
                e.printStackTrace();
                this.getLogger().severe("Failed to initialize");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }, 1);
        new TinyProtocol(this) {
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
                try {
                    if (Reflections.NMS_ClientboundLevelChunkPacket.isInstance(packet)) {
                        int[] biomes = (int[])Reflections.NMS_ClientboundLevelChunkPacket_getBiomes.invoke(packet);
                        int chunkX = (int)Reflections.NMS_ClientboundLevelChunkPacket_getX.invoke(packet);
                        int chunkZ = (int)Reflections.NMS_ClientboundLevelChunkPacket_getZ.invoke(packet);
                        int baseX = chunkX << 4;
                        int baseZ = chunkZ << 4;
                        List<Config> configs = new ArrayList<>(EverBiomePlugin.this.config);
                        configs.removeIf(config-> 
                            !config.checkDimension(receiver) ||
                            !config.checkPermission(receiver) || 
                            !config.checkChunkX(chunkX) ||
                            !config.checkChunkZ(chunkZ)
                        );
                        for (int i = 0; i < biomes.length; i ++) {
                            int y = (i >> 4) * 4;
                            int inChunkX = (i & 3) * 4;
                            int inChunkZ = ((i >> 2) & 3) * 4;
                            int x = baseX + inChunkX;
                            int z = baseZ + inChunkZ;
                            for (Config config : configs) {
                                if (!config.checkBiome(biomes[i])) continue;
                                if (!config.checkX(x)) continue;
                                if (!config.checkY(y)) continue;
                                if (!config.checkZ(z)) continue;
                                if (!config.checkInChunkX(inChunkX)) continue;
                                if (!config.checkInChunkZ(inChunkZ)) continue;
                                biomes[i] = config.destination;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return packet;
            }
        };
    }

    public void reload() throws Exception {

        // First, load biomes
        Object minecraftServer = Reflections.CB_CraftServer_getServer.invoke(Bukkit.getServer());
        Object registryAccess = Reflections.NMS_MinecraftServer_registryAccess.invoke(minecraftServer);
        Object biomes = Reflections.NMS_RegistryAccess_registryOrThrow.invoke(registryAccess, Reflections.NMS_Registry_BIOME_REGISTRY_value);
        String[] files;
        try {
            files = FileUtil.getFiles(this, "biomes", ".*\\.json$");
        } catch (Exception e) {
            // Folder not exist, initialize with example biomes first
            Arrays.stream(FileUtil.getResourceFolderFiles("/biomes"))
                .forEach(file -> {
                    FileUtil.getFile(this, "biomes/" + file);
                });
            files = FileUtil.getFiles(this, "biomes", ".*\\.json$");
        }
        for (String file : files) {
            String key = "everbiome:" + file.substring(0, file.length() - 5);
            Object mcKey = Reflections.NMS_ResourceLocation_Constructor.newInstance(key);
            boolean exists = (boolean)(Object)Reflections.NMS_Registry_containsKey.invoke(biomes, mcKey);
            Object biome = FileUtil.getBiomeFromJson(this, "biomes/" + file);
            if (exists) {
                Object oldbiome = Reflections.NMS_Registry_get.invoke(biomes, mcKey);
                int oldid = (int)(Object)Reflections.NMS_Registry_getId.invoke(biomes, oldbiome);
                Reflections.NMS_Registry_registerWithId.invoke(null, biomes, oldid, key, biome);
            } else {
                Reflections.NMS_Registry_register.invoke(null, biomes, key, biome);
            }
        }
        Set<?> entrySet = (Set<?>)Reflections.NMS_Registry_entrySet.invoke(biomes);
        customBiomes = new ConcurrentHashMap<>();
        for (Object e : entrySet) {
            Entry<?,?> entry = (Entry<?,?>)e;
            Object key = entry.getKey();
            Object biome = entry.getValue();
            int id = (int)Reflections.NMS_Registry_getId.invoke(biomes, biome);
            Object location = Reflections.NMS_ResourceKey_location.invoke(key);
            Object namespace = Reflections.NMS_ResourceLocation_getNamespace.invoke(location);
            Object path = Reflections.NMS_ResourceLocation_getPath.invoke(location);
            customBiomes.put(namespace + ":" + path, id);
        }
        Map<String, Integer> count = new HashMap<>();
        customBiomes.forEach((key, id) -> {
            String namespace = key.split(":")[0];
            if (count.containsKey(namespace)) {
                count.put(namespace, count.get(namespace) + 1);
            } else {
                count.put(namespace, 1);
            }
        });
        count.forEach((namespace, n) -> {
            this.getLogger().info("Load " + n + " biomes from " + namespace);
        });

        // Second, load transform config
        this.config = Config.getConfig(FileUtil.getJson(this, "config.json"));
        this.config.forEach(config -> config.parse(this.customBiomes));
        this.config = Collections.unmodifiableList(this.config);

        this.getLogger().info("Reload Success");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".equals(args[0]) && sender.hasPermission("everbiome.reload")) {
            try {
                reload();
                sender.sendMessage("Reload Success");
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("Reload failed");
            }
        } else {
            sender.sendMessage("EverBiome v1.0");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,String[] args) {
        if (args.length <= 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
