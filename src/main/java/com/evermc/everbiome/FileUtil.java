package com.evermc.everbiome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FileUtil {

    private static final JsonParser parser = new JsonParser();

    public static File getFile(EverBiomePlugin plugin, String filename) {
        if (!new File(plugin.getDataFolder(), filename).exists()) {
            plugin.saveResource(filename, false);
        }
        return new File(plugin.getDataFolder(), filename);
    }

    public static JsonElement getJson(EverBiomePlugin plugin, String filename) throws Exception {
        File file = getFile(plugin, filename);
        return parser.parse(new FileReader(file));
    }

    public static Object getBiomeFromJson(EverBiomePlugin plugin, String filename) throws Exception {
        JsonElement json = getJson(plugin, filename);
        Object dataResult = Reflections.mojang_Codec_Parse.invoke(
            Reflections.NMS_Biome_NETWORK_CODEC_value,
            Reflections.mojang_JsonOps_INSTANCE_value,
            json
        );
        return Reflections.mojang_DataResult_getOrThrow.invoke(
            dataResult, 
            false, 
            (Consumer<String>)ex -> {
                new IllegalArgumentException(ex).printStackTrace();
            });
    }

    public static String[] getFiles(EverBiomePlugin plugin, String directory, String regex) throws FileNotFoundException, IOException {
        File folder = new File(plugin.getDataFolder(), directory);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException();
        }
        return Files.list(folder.toPath())
            .filter(path -> 
                regex == null || path.getFileName().toString().matches(regex)
            )
            .map(Path::getFileName)
            .map(Path::toString)
            .toArray(String[]::new);
    }

    public static String[] getResourceFolderFiles(String folder) {
        try {
            URI uri = EverBiomePlugin.class.getResource(folder).toURI();
            try(FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())){
                Path folderRootPath = fileSystem.getPath(folder);
                return Files.walk(folderRootPath, 1)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toArray(String[]::new);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }
}
