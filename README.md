# EverBiome

[![GitHub Actions](https://github.com/EverMCServer/EverBiome/actions/workflows/ci.yml/badge.svg)](https://github.com/EverMCServer/EverBiome/actions/workflows/ci.yml)

Use custom biomes without datapacks or affecting your map data in Minecraft 1.16.2+

Tested versions: `1.16.5`, `1.17.1`

## Highlights

- Fully supported Minecraft custom biome (worldgen format) (see [Wiki](https://minecraft.fandom.com/wiki/Custom_world_generation) for more information)
- No datapacks are needed!
- Reloadable! No more server restarts to apply your new biomes
- Per-player biome display (based on permissions)
- Fake biome: this plugin does NOT modify your maps, so no worry about corrupting your savedata even if you made mistakes in biomes
- Powerful filters (see below)

## Showcase

### The doomsday
[![The doomsday biome](https://github.com/EverMCServer/EverBiome/blob/gh-pages/doomsday.min.jpg?raw=true)](https://github.com/EverMCServer/EverBiome/blob/gh-pages/doomsday.png?raw=true)

<details>
  <summary>Config for the doomsday</summary>
  
  <details>
    <summary>config.json</summary>
  
  ```json
[
    {
        "condition": {
        },
        "to": "everbiome:doomsday"
    }
]
  ```

</details>

<details>
    <summary>biomes/doomsday.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 7798784,
    "fog_color": 8912896,
    "water_color": 16711680,
    "water_fog_color": 16711680,
    "grass_color": 7798784,
    "foliage_color": 7798784,
    "particle": {
      "probability": 0.01,
      "options": {
        "type": "minecraft:lava"
      }
    },
    "additions_sound": {
      "sound": "minecraft:ambient.cave",
      "tick_chance": 0.01
    }
  },
  "precipitation": "rain",
  "temperature": 2.0,
  "downfall": 0.0,
  "category": "the_end",
  "parent": "the_end",
  "depth": -1.0
}
```

</details>
  
</details>


### The rainbow (partially applied every 280 blocks)

[![The rainbow](https://github.com/EverMCServer/EverBiome/blob/gh-pages/rainbow.min.jpg?raw=true)](https://github.com/EverMCServer/EverBiome/blob/gh-pages/rainbow.png?raw=true)
<details>
  <summary>Config for the rainbow</summary>
  
  <details>
    <summary>config.json</summary>
  
  ```json
[
    {
        "condition": {
            "x": "0/280"
        },
        "to": "everbiome:red"
    },
    {
        "condition": {
            "x": "4/280"
        },
        "to": "everbiome:orange"
    },
    {
        "condition": {
            "x": "8/280"
        },
        "to": "everbiome:yellow"
    },
    {
        "condition": {
            "x": "12/280"
        },
        "to": "everbiome:green"
    },
    {
        "condition": {
            "x": "16/280"
        },
        "to": "everbiome:blue"
    },
    {
        "condition": {
            "x": "20/280"
        },
        "to": "everbiome:indigo"
    },
    {
        "condition": {
            "x": "24/280"
        },
        "to": "everbiome:violet"
    }
]
  ```

</details>

<details>
    <summary>biomes/red.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 16711680,
    "water_color": 16711680,
    "water_fog_color": 16711680,
    "grass_color": 16711680,
    "foliage_color": 16711680
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/orange.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 16744192,
    "water_color": 16744192,
    "water_fog_color": 16744192,
    "grass_color": 16744192,
    "foliage_color": 16744192
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/yellow.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 16776960,
    "water_color": 16776960,
    "water_fog_color": 16776960,
    "grass_color": 16776960,
    "foliage_color": 16776960
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/green.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 65280,
    "water_color": 65280,
    "water_fog_color": 65280,
    "grass_color": 65280,
    "foliage_color": 65280
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/blue.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 255,
    "water_color": 255,
    "water_fog_color": 255,
    "grass_color": 255,
    "foliage_color": 255
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/indigo.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 3025759,
    "water_color": 3025759,
    "water_fog_color": 3025759,
    "grass_color": 3025759,
    "foliage_color": 3025759
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

<details>
    <summary>biomes/violet.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 9109759,
    "water_color": 9109759,
    "water_fog_color": 9109759,
    "grass_color": 9109759,
    "foliage_color": 9109759
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>

</details>

### The toxic river (replaces all `minecraft:river` biome)

[![The toxic water](https://github.com/EverMCServer/EverBiome/blob/gh-pages/toxic_river.min.jpg?raw=true)](https://github.com/EverMCServer/EverBiome/blob/gh-pages/toxic_river.png?raw=true)

<details>
  <summary>Config for the toxic river</summary>
  
  <details>
    <summary>config.json</summary>
  
  ```json
[
    {
        "condition": {
            "biome": "minecraft:river"
        },
        "to": "everbiome:ocean_toxic"
    }
]
  ```

</details>

<details>
    <summary>biomes/ocean_toxic.json</summary>
  
```json
{
  "scale": 0.1,
  "effects": {
    "sky_color": 8103167,
    "fog_color": 12638463,
    "water_color": 11523840,
    "water_fog_color": 4675328
  },
  "precipitation": "rain",
  "temperature": 0.5,
  "downfall": 0.5,
  "category": "ocean",
  "depth": -1.0
}
```

</details>
  
</details>

## Biome transformation

This plugin use biome transformations to change what players actually receive & see. 

Each transformation contains a `condition` that decides which part of the map should be changed, and a destination biome (`to` field in config). Positions that satisfy *all* filters will be changed to the `destination` biome. 

The `destination` biome could be any biome you like: a vanilla Minecraft biome, or a custom biome in any datapacks, or the biome you created with EverBiome. (Notice: only custom biomes created by EverBiome could be modified and reloaded without restarting the server)

The `condition` supports the following parameters:

- **`perm`**: The permission (or multiple permissions separated by `|`) needed for showing the current biome. The player will not see this biome if he has none of the permissions.
- **`biome`**: The original biome. (eg. you can change all ocean biomes to a custom toxic ocean biome) 
- **`dimension`**: The world name (or multiple worlds separated by `|`)
- **`x`**: The x-axis coordinate. Use `Range` format (See below) to select multiple coordinates. Notice: biomes in minecraft have a maximum resolution of 4x4x4, so the coordinate should be divisible by 4.
- **`y`, `z`**: Similar to **`x`**
- **`chunkX`, `chunkZ`**: The `x`(or `z`) axis coordinate of the chunk position. It's faster to use this if you want to change the biome of a whole chunk. Notice the chunk position is the block position divided by 16. Use `Range` format (See below) to select multiple coordinates. 
- **`inChunkX`, `inChunkZ`**: The `x`(or `z`) axis inside the chunk. Both in the range [0,15], (but also need to be divisible by 4). Use `Range` format (See below) to select multiple coordinates. 

## The `Range` format

- Select a single value

Example: select the (100,64,100) block:
```json
{"x": "100", "y": "64", "z": "100"}
```

- Select a range: `rangestart~rangeend`

Example: select all blocks from x:-100 to x:100
```json
{"x": "-100~100"}
```

- Select multiples of a number `remainder/divisor`

Example: select all blocks with x-axis coordinate divisible by 100
```json
{"x": "0/100"}
```

Example: select all blocks with x-axis coordinate divide by 100 remains 64
```json
{"x": "64/100"}
```

- Select multiples of a number in range `rangestart~rangeend/divisor`

Example: select all blocks with x-axis coordinate divisible by 100 and between 1000~1200 (will select 1000,1100,1200)
```json
{"x": "1000~1200/100"}
```

Example: select all blocks with x-axis coordinate divide by 100 remains 64 and between 1000~1200 (Will select 1064, 1164)
```json
{"x": "1064~1200/100"}
```

## LICENCE

[GPLv2 License](https://github.com/EverMCServer/EverBiome/blob/master/LICENSE) as we based on (shadowed) the [TinyProtocol](https://github.com/aadnk/ProtocolLib/tree/master/TinyProtocol) library.

## Compiling

Notice that TinyProtocol does not support 1.17 currently. If you want to build this plugin from source and want to support 1.17, consider using [this version of TinyProtocol](https://github.com/aadnk/ProtocolLib/pull/194) before it's officially supported.

