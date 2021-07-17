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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.Bukkit;

public class ReflectionUtil {
    private static final String SERVER_VERSION = getServerVersion();

    private static String getServerVersion() {
        Class<?> server = Bukkit.getServer().getClass();
        if (!server.getSimpleName().equals("CraftServer")) {
            return ".";
        }
        if (server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            return ".";
        } else {
            String version = server.getName().substring("org.bukkit.craftbukkit".length());
            return version.substring(0, version.length() - "CraftServer".length());
        }
    }

    public static Class<?> NMSClass(String... className) throws ClassNotFoundException {
        
        for(String name : className) {
            try {
                if (name.contains(".")) {
                    return Class.forName("net.minecraft." + name);
                } else {
                    return Class.forName("net.minecraft.server" + SERVER_VERSION + name);
                }
            } catch (Exception ignored) {}
        }
        throw new ClassNotFoundException(String.join(",", className));
    }

    public static Class<?> ArrayClass(Class<?> input) throws ClassNotFoundException {
        return Class.forName("[L" + input.getName() + ";");
    }

    public static Class<?> CBClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit" + SERVER_VERSION + className);
    }

    public static Method getTypedMethod(Class<?> clazz, String methodName, boolean skipParams, Class<?>... params) throws NoSuchMethodException {
        return getTypedMethod(clazz, methodName, null, null, null, 0, skipParams, params);
    }

    public static Method getTypedMethod(Class<?> clazz, Class<?> returnType, boolean skipParams, Class<?>... params) throws NoSuchMethodException {
        return getTypedMethod(clazz, null, returnType, null, null, 0, skipParams, params);
    }

    public static Method getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, boolean skipParams, Class<?>... params) throws NoSuchMethodException {
        return getTypedMethod(clazz, methodName, returnType, null, null, 0, skipParams, params);
    }

    public static Method getTypedMethod(Class<?> clazz, Class<?> returnType, List<Object> genericReturnType, boolean skipParams, Class<?>... params) throws NoSuchMethodException {
        return getTypedMethod(clazz, null, returnType, genericReturnType, null, 0, skipParams, params);
    }

    public static Method getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, List<Object> genericReturnType, Predicate<Method> custom, int skip, boolean skipParams, Class<?>... params) throws NoSuchMethodException {
        return Stream.of(clazz.getDeclaredMethods()).filter(method -> 
            (returnType == null || method.getReturnType() == returnType) &&
            (skipParams || Arrays.equals(params, method.getParameterTypes())) &&
            (methodName == null || method.getName().equals(methodName)) &&
            (genericReturnType == null || (
                method.getGenericReturnType() instanceof ParameterizedType &&
                check(genericReturnType, (ParameterizedType)method.getGenericReturnType())
            )) &&
            (custom == null || custom.test(method))
        )
        .skip(skip)
        .findFirst()
        .map(method -> {
            method.setAccessible(true);
            return method;
        })
        .orElseThrow(NoSuchMethodException::new);
    }

    public static Field getTypedField(Class<?> clazz, Class<?> fieldType) throws NoSuchFieldException {
        return getTypedField(clazz, null, fieldType, null, null, 0);
    }

    public static Field getTypedField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return getTypedField(clazz, fieldName, null, null, null, 0);
    }

    public static Field getTypedField(Class<?> clazz, Class<?> fieldType, List<Object> generic) throws NoSuchFieldException {
        return getTypedField(clazz, null, fieldType, generic, null, 0);
    }

    public static Field getTypedField(Class<?> clazz, String fieldName, Class<?> fieldType, List<Object> generic, Predicate<Field> custom, int skip) throws NoSuchFieldException, IllegalArgumentException {
        return Stream.of(clazz.getDeclaredFields()).filter(field -> 
            (fieldType == null || field.getType() == fieldType) &&
            (fieldName == null || field.getName().equals(fieldName)) &&
            (generic == null || (
                field.getGenericType() instanceof ParameterizedType &&
                check(generic, (ParameterizedType)field.getGenericType())
            )) &&
            (custom == null || custom.test(field))
        )
        .skip(skip)
        .findFirst()
        .map(field -> {
            field.setAccessible(true);
            return field;
        })
        .orElseThrow(NoSuchFieldException::new);
    }

    /**
     * @param generic List of Classes. Elements could be replaced with Entry<Class<?>, List<Class<?>>> for nested generic types
     * 
     * Example:
     * Set<String>:
     *      fieldType = Set.class
     *      generic = [String.class]
     * Map<String, Integer>:
     *      fieldType = Map.class
     *      generic = [String.class, Integer.class]
     * Map<Integer, Map<String, Long>>:
     *      fieldType = Map.class
     *      generic = [Integer.class, Entry<Class<?>, List<Class<?>>>(Map.class, [String.class, Long.class])]
     */
    private static boolean check(List<?> generic, ParameterizedType type) throws IllegalArgumentException {
        Type[] types = type.getActualTypeArguments();
        if (generic.size() != types.length) {
            return false;
        }
        for (int i = 0; i < generic.size(); i ++) {
            Object k = generic.get(i);
            if (k == null) {
                // skip check 
            } else if (k instanceof Class<?>) {
                if (types[i] instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType)types[i];
                    if (t.getRawType() != (Class<?>)k) {
                        return false;
                    }
                } else if (types[i] != (Class<?>)k) {
                    return false;
                }
            } else if (k instanceof Entry<?,?>) {
                Object key = ((Entry<?,?>)k).getKey();
                Object value = ((Entry<?,?>)k).getValue();
                if (!(key instanceof Class<?> && value instanceof List<?>)) {
                    throw new IllegalArgumentException();
                }
                Type current = types[i];
                if (current instanceof ParameterizedType) {
                    if (((ParameterizedType)current).getRawType() != (Class<?>)key) {
                        return false;
                    }
                    if (!check((List<?>)value, (ParameterizedType)current)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        return true;
    }
}
