/**
 *  EverBiome - Use custom biomes without datapacks or affecting your map data
 *  Copyright (C) 2021 djytw
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.evermc.everbiome;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import static com.evermc.everbiome.ReflectionUtil.CBClass;
import static com.evermc.everbiome.ReflectionUtil.NMSClass;
import static com.evermc.everbiome.ReflectionUtil.getTypedField;
import static com.evermc.everbiome.ReflectionUtil.getTypedMethod;

public class Reflections {
    
    public static final Class<?> mojang_Codec;
    public static final Class<?> mojang_Decoder;
    public static final Class<?> mojang_DataResult;
    public static final Class<?> mojang_DynamicOps;
    public static final Class<?> mojang_JsonOps;
    public static final Class<?> CB_CraftServer;
    public static final Class<?> NMS_Biome;
    public static final Class<?> NMS_ClientboundLevelChunkPacket;
    public static final Class<?> NMS_ClientboundLoginPacket;
    public static final Class<?> NMS_MinecraftServer;
    public static final Class<?> NMS_Registry;
    public static final Class<?> NMS_RegistryAccess;
    public static final Class<?> NMS_RegistryAccess_RegistryHolder;
    public static final Class<?> NMS_ResourceKey;
    public static final Class<?> NMS_ResourceLocation;

    public static final Method mojang_Codec_Parse;
    public static final Method mojang_DataResult_getOrThrow;
    public static final Method CB_CraftServer_getServer;
    public static final Method NMS_MinecraftServer_registryAccess;
    public static final Method NMS_RegistryAccess_registryOrThrow;
    public static final Method NMS_Registry_entrySet;
    public static final Method NMS_Registry_get;
    public static final Method NMS_Registry_getId;
    public static final Method NMS_Registry_register;
    public static final Method NMS_Registry_registerWithId;
    public static final Method NMS_ResourceKey_location;
    public static final Method NMS_ResourceLocation_getNamespace;
    public static final Method NMS_ResourceLocation_getPath;

    public static final Constructor<?> NMS_ResourceLocation_Constructor;

    public static final Field NMS_ClientboundLevelChunkPacket_biomes;
    public static final Field NMS_ClientboundLevelChunkPacket_getX;
    public static final Field NMS_ClientboundLevelChunkPacket_getZ;

    public static final Object mojang_JsonOps_INSTANCE_value;
    public static final Object NMS_Biome_NETWORK_CODEC_value;
    public static final Object NMS_Registry_BIOME_REGISTRY_value;

    static {
        try {
            mojang_Codec = Class.forName("com.mojang.serialization.Codec");
            mojang_Decoder = Class.forName("com.mojang.serialization.Decoder");
            mojang_DataResult = Class.forName("com.mojang.serialization.DataResult");
            mojang_DynamicOps = Class.forName("com.mojang.serialization.DynamicOps");
            mojang_JsonOps = Class.forName("com.mojang.serialization.JsonOps");
            CB_CraftServer = CBClass("CraftServer");
            NMS_Biome = NMSClass("BiomeBase", "world.level.biome.BiomeBase", "world.level.biome.Biome");
            NMS_ClientboundLevelChunkPacket = NMSClass("PacketPlayOutMapChunk", "network.protocol.game.PacketPlayOutMapChunk", "network.protocol.game.ClientboundLevelChunkPacket");
            NMS_ClientboundLoginPacket = NMSClass("PacketPlayOutLogin", "network.protocol.game.PacketPlayOutLogin", "network.protocol.game.ClientboundLoginPacket");
            NMS_MinecraftServer = NMSClass("MinecraftServer", "server.MinecraftServer");
            NMS_Registry = NMSClass("IRegistry", "core.IRegistry", "core.Registry");
            NMS_RegistryAccess = NMSClass("IRegistryCustom", "core.IRegistryCustom", "core.RegistryAccess");
            NMS_RegistryAccess_RegistryHolder = NMSClass("IRegistryCustom$Dimension", "core.IRegistryCustom$Dimension", "core.RegistryAccess$RegistryHolder");
            NMS_ResourceKey = NMSClass("ResourceKey", "resources.ResourceKey");
            NMS_ResourceLocation = NMSClass("MinecraftKey", "resources.MinecraftKey", "resources.ResourceLocation");

            mojang_Codec_Parse = getTypedMethod(mojang_Decoder, "parse", mojang_DataResult, null, null, 0, false, mojang_DynamicOps, Object.class);
            mojang_DataResult_getOrThrow = getTypedMethod(mojang_DataResult, "getOrThrow", true);
            CB_CraftServer_getServer = getTypedMethod(CB_CraftServer, "getServer", false);
            NMS_MinecraftServer_registryAccess = getTypedMethod(NMS_MinecraftServer, NMS_RegistryAccess, false);
            NMS_RegistryAccess_registryOrThrow = getTypedMethod(NMS_RegistryAccess_RegistryHolder, Optional.class, false, NMS_ResourceKey);
            NMS_Registry_entrySet = getTypedMethod(NMS_Registry, Set.class, 
            List.of(
                new AbstractMap.SimpleEntry<>(
                    Entry.class,
                    Arrays.asList(NMS_ResourceKey, null)
                )
            ), false);
            NMS_Registry_get = getTypedMethod(NMS_Registry, "get", Object.class, false, NMS_ResourceLocation);
            NMS_Registry_getId = getTypedMethod(NMS_Registry, null, int.class, true);
            NMS_Registry_register = getTypedMethod(NMS_Registry, null, Object.class, false, NMS_Registry, String.class, Object.class);
            NMS_Registry_registerWithId = getTypedMethod(NMS_Registry, null, Object.class, false, NMS_Registry, int.class, String.class, Object.class);
            NMS_ResourceKey_location = getTypedMethod(NMS_ResourceKey, NMS_ResourceLocation, null, false);
            NMS_ResourceLocation_getNamespace = getTypedMethod(NMS_ResourceLocation, "getNamespace", String.class, null, null, 0, false);
            NMS_ResourceLocation_getPath = getTypedMethod(NMS_ResourceLocation, "getKey", String.class, null, null, 0, false);

            Field mojang_JsonOps_INSTANCE = getTypedField(mojang_JsonOps, "INSTANCE");
            Field NMS_Registry_BIOME_REGISTRY = getTypedField(NMS_Registry, NMS_ResourceKey, List.of(
                new AbstractMap.SimpleEntry<>(
                    NMS_Registry,
                    List.of(NMS_Biome)
                )
            ));
            NMS_ClientboundLevelChunkPacket_biomes = getTypedField(NMS_ClientboundLevelChunkPacket, int[].class);
            NMS_ClientboundLevelChunkPacket_getX = getTypedField(NMS_ClientboundLevelChunkPacket, null, int.class, null, field -> !Modifier.isStatic(field.getModifiers()), 0);
            NMS_ClientboundLevelChunkPacket_getZ = getTypedField(NMS_ClientboundLevelChunkPacket, null, int.class, null, field -> !Modifier.isStatic(field.getModifiers()), 1);
            List<Field> NMS_Biome_Codecs = new ArrayList<>();
            for (int i = 0;; i ++) {
                try {
                    NMS_Biome_Codecs.add(getTypedField(NMS_Biome, null, mojang_Codec, List.of(NMS_Biome), null, i));
                } catch (NoSuchFieldException e) {
                    break;
                }
            }

            NMS_ResourceLocation_Constructor = NMS_ResourceLocation.getConstructor(String.class);

            mojang_JsonOps_INSTANCE_value = mojang_JsonOps_INSTANCE.get(null);
            NMS_Biome_NETWORK_CODEC_value = NMS_Biome_Codecs.stream().map(field -> {
                try {
                    return (Object)field.get(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return (Object)null;
                }
            })
            .filter(k -> k != null && !k.toString().contains("surface_builder"))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
            NMS_Registry_BIOME_REGISTRY_value = NMS_Registry_BIOME_REGISTRY.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError();
        }
    }
}
