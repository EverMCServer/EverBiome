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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.bukkit.entity.Player;

public class Config {

    @Expose
    @SerializedName("condition") 
    public RawCondition rawCondition;
    @Expose
    @SerializedName("to") 
    public String rawDestination;

    public volatile Condition cond;
    public volatile int destination;
    private static Gson gson = new Gson();

    public static class RawCondition {
        public String biome;
        public String x;
        public String y;
        public String z;
        public String chunkX;
        public String chunkZ;
        public String inChunkX;
        public String inChunkZ;
        public String perm;
        public String dimension;
    }

    public static class Condition {
        public int[] biome;
        public String[] perm;
        public String[] dimension;
        public Range x;
        public Range y;
        public Range z;
        public Range chunkX;
        public Range chunkZ;
        public Range inChunkX;
        public Range inChunkZ;
    }

    public static class Range {
        public Integer equal = null;
        public int[] range = null;
        public int[] mod = null;
        private static final Pattern PATTERN = Pattern.compile("^([-]?\\d+)(?:~([-]?\\d+))?(?:/(\\d+))?$");

        public Range(String str) {
            Matcher match = PATTERN.matcher(str);
            if (!match.find()) {
                throw new IllegalArgumentException("Invalid range: " + str);
            }
            String mod = match.group(3);
            if (mod != null) {
                int modint = Integer.parseInt(mod);
                int remainder = Integer.parseInt(match.group(1));
                this.mod = new int[]{modint, remainder};
                if (match.group(2) == null) {
                    // 1/mod
                    return;
                }
            } else {
                if (match.group(2) == null) {
                    // single number
                    this.equal = Integer.parseInt(match.group(1));
                    return;
                }
            }
            int rangestart = Integer.parseInt(match.group(1));
            int rangeend = Integer.parseInt(match.group(2));
            if (rangeend <= rangestart) {
                throw new IllegalArgumentException("Range end value should larger than start value: " + str);
            }
            this.range = new int[]{rangestart, rangeend};
        }

        public boolean check(int k) {
            if (this.equal != null) {
                return this.equal == k;
            }
            if (this.range != null) {
                if (k < range[0] || k > range[1]) {
                    return false;
                }
            }
            if (this.mod != null) {
                k -= this.mod[1];
                return (k % this.mod[0]) == 0;
            }
            return true;
        }
    }

    public void parse(Map<String, Integer> biomes) {
        this.cond = new Condition();
        this.cond.biome = this.rawCondition.biome == null ? null :
            Stream.of(this.rawCondition.biome.split("\\|"))
            .mapToInt(biome -> {
                Integer b = biomes.get(biome);
                if (b == null) {
                    throw new IllegalArgumentException("No such biome: " + biome + ", " +this.rawCondition.biome);
                }
                return b.intValue();
            })
            .toArray();
        this.cond.x = this.rawCondition.x == null ? null : new Range(this.rawCondition.x);
        this.cond.y = this.rawCondition.y == null ? null : new Range(this.rawCondition.y);
        this.cond.z = this.rawCondition.z == null ? null : new Range(this.rawCondition.z);
        this.cond.chunkX = this.rawCondition.chunkX == null ? null : new Range(this.rawCondition.chunkX);
        this.cond.chunkZ = this.rawCondition.chunkZ == null ? null : new Range(this.rawCondition.chunkZ);
        this.cond.inChunkX = this.rawCondition.inChunkX == null ? null : new Range(this.rawCondition.inChunkX);
        this.cond.inChunkZ = this.rawCondition.inChunkZ == null ? null : new Range(this.rawCondition.inChunkZ);
        this.cond.dimension = this.rawCondition.dimension == null ? null : this.rawCondition.dimension.split("\\|");
        this.cond.perm = this.rawCondition.perm == null ? null : this.rawCondition.perm.split("\\|");
        Integer dest = biomes.get(this.rawDestination);
        if (dest == null) {
            throw new IllegalArgumentException("No such biome: " + this.rawDestination);
        }
        this.destination = dest.intValue();
    }

    public static List<Config> getConfig(JsonElement json) {
        Type listType = new TypeToken<ArrayList<Config>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public boolean checkPermission(Player p) {
        if (p == null || this.cond.perm == null) {
            return true;
        }
        return Stream.of(this.cond.perm).anyMatch(p::hasPermission);
    }

    public boolean checkDimension(Player p) {
        if (p == null || this.cond.dimension == null) {
            return true;
        }
        return Stream.of(this.cond.dimension).anyMatch(d -> d.equals(p.getWorld().getName()));
    }

    public boolean checkX(int x) {
        if (this.cond.x == null) {
            return true;
        }
        return this.cond.x.check(x);
    }

    public boolean checkY(int y) {
        if (this.cond.y == null) {
            return true;
        }
        return this.cond.y.check(y);
    }

    public boolean checkZ(int z) {
        if (this.cond.z == null) {
            return true;
        }
        return this.cond.z.check(z);
    }

    public boolean checkChunkX(int x) {
        if (this.cond.chunkX == null) {
            return true;
        }
        return this.cond.chunkX.check(x);
    }

    public boolean checkChunkZ(int z) {
        if (this.cond.chunkZ == null) {
            return true;
        }
        return this.cond.chunkZ.check(z);
    }

    public boolean checkInChunkX(int x) {
        if (this.cond.inChunkX == null) {
            return true;
        }
        return this.cond.inChunkX.check(x);
    }

    public boolean checkInChunkZ(int z) {
        if (this.cond.inChunkZ == null) {
            return true;
        }
        return this.cond.inChunkZ.check(z);
    }

    public boolean checkBiome(int biome) {
        if (this.cond.biome == null) {
            return true;
        }
        return Arrays.stream(this.cond.biome).anyMatch(k -> k == biome);
    }
}
