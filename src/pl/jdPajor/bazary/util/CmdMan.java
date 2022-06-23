package pl.jdPajor.bazary.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.SimplePluginManager;

import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.util.regex.Matcher;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.regex.Pattern;


import java.util.HashMap;

public class CmdMan {
    public static  HashMap<String, Cmd> commands;
    private static  Refl.FieldAccessor<SimpleCommandMap> f;
    private static CommandMap cmdMap;
    
    static {
        commands = new HashMap<String, Cmd>();
        f = Refl.getField(SimplePluginManager.class, "commandMap", SimpleCommandMap.class);
        CmdMan.cmdMap = (CommandMap)CmdMan.f.get(Bukkit.getServer().getPluginManager());
    }
    
    public static void register( Cmd cmd) {
        if (CmdMan.cmdMap == null) {
        	CmdMan.cmdMap = (CommandMap)CmdMan.f.get(Bukkit.getServer().getPluginManager());
        }
        CmdMan.cmdMap.register(cmd.getName(), (org.bukkit.command.Command)cmd);
        CmdMan.commands.put(cmd.getName(), cmd);
    }
}

class Refl {
    private static String OBC_PREFIX;
    private static String NMS_PREFIX;
    private static String VERSION;
    private static Pattern MATCH_VARIABLE;
    
    static {
        Refl.OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
        Refl.NMS_PREFIX = Refl.OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
        Refl.VERSION = Refl.OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
        Refl.MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
    }
    
    private Refl() {
    }
    
    public static <T> FieldAccessor<T> getSimpleField( Class<?> target,  String name) {
        return (FieldAccessor<T>)getField(target, name);
    }
    
    public static <T> FieldAccessor<T> getField( Class<?> target,  String name,  Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }
    
    public static <T> FieldAccessor<T> getField( String className,  String name,  Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }
    
    public static <T> FieldAccessor<T> getField( Class<?> target,  Class<T> fieldType,  int index) {
        return getField(target, null, fieldType, index);
    }
    
    public static <T> FieldAccessor<T> getField( String className,  Class<T> fieldType,  int index) {
        return getField(getClass(className), fieldType, index);
    }
    
    private static <T> FieldAccessor<T> getField( Class<?> target,  String name,  Class<T> fieldType, int index) {
        Field[] declaredFields;
        for (int length = (declaredFields = target.getDeclaredFields()).length, i = 0; i < length; ++i) {
             Field field = declaredFields[i];
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return new FieldAccessor<T>() {
                    @Override
                    public T get( Object target) {
                        try {
                            return (T)field.get(target);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access Refl.", e);
                        }
                    }
                    
                    @Override
                    public void set( Object target,  Object value) {
                        try {
                            field.set(target, value);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access Refl.", e);
                        }
                    }
                    
                    @Override
                    public boolean hasField( Object target) {
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }
        if (target.getSuperclass() != null) {
            return (FieldAccessor<T>)getField(target.getSuperclass(), name, (Class<Object>)fieldType, index);
        }
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }
    
    private static <T> FieldAccessor<T> getField( Class<?> target,  String name) {
        Field[] declaredFields;
        for (int length = (declaredFields = target.getDeclaredFields()).length, i = 0; i < length; ++i) {
             Field field = declaredFields[i];
            if (name == null || field.getName().equals(name)) {
                field.setAccessible(true);
                return new FieldAccessor<T>() {
                    @Override
                    public T get( Object target) {
                        try {
                            return (T)field.get(target);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access Refl.", e);
                        }
                    }
                    
                    @Override
                    public void set( Object target,  Object value) {
                        try {
                            field.set(target, value);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access Refl.", e);
                        }
                    }
                    
                    @Override
                    public boolean hasField( Object target) {
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }
        if (target.getSuperclass() != null) {
            return (FieldAccessor<T>)getField(target.getSuperclass(), name);
        }
        throw new IllegalArgumentException("Cannot find field with type");
    }
    
    public static MethodInvoker getMethod( String className,  String methodName,  Class<?>... params) {
        return getTypedMethod(getClass(className), methodName, null, params);
    }
    
    public static MethodInvoker getMethod( Class<?> clazz,  String methodName,  Class<?>... params) {
        return getTypedMethod(clazz, methodName, null, params);
    }
    
    public static MethodInvoker getTypedMethod( Class<?> clazz,  String methodName,  Class<?> returnType,  Class<?>... params) {
        Method[] declaredMethods;
        for (int length = (declaredMethods = clazz.getDeclaredMethods()).length, i = 0; i < length; ++i) {
             Method method = declaredMethods[i];
            if (((methodName == null || method.getName().equals(methodName)) && returnType == null) || (method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), params))) {
                method.setAccessible(true);
                return new MethodInvoker() {
                    @Override
                    public Object invoke( Object target,  Object... arguments) {
                        try {
                            return method.invoke(target, arguments);
                        }
                        catch (Exception e) {
                            throw new RuntimeException("Cannot invoke method " + method, e);
                        }
                    }
                };
            }
        }
        if (clazz.getSuperclass() != null) {
            return getMethod(clazz.getSuperclass(), methodName, params);
        }
        throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }
    
    public static ConstructorInvoker getConstructor( String className,  Class<?>... params) {
        return getConstructor(getClass(className), params);
    }
    
    public static ConstructorInvoker getConstructor( Class<?> clazz,  Class<?>... params) {
        Constructor<?>[] declaredConstructors;
        for (int length = (declaredConstructors = clazz.getDeclaredConstructors()).length, i = 0; i < length; ++i) {
             Constructor<?> constructor = declaredConstructors[i];
            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                constructor.setAccessible(true);
                return new ConstructorInvoker() {
                    @Override
                    public Object invoke( Object... arguments) {
                        try {
                            return constructor.newInstance(arguments);
                        }
                        catch (Exception e) {
                            throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                        }
                    }
                };
            }
        }
        throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
    }
    
    public static Class<Object> getUntypedClass( String lookupName) {
         Class<Object> clazz = (Class<Object>)getClass(lookupName);
        return clazz;
    }
    
    public static Class<?> getClass( String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }
    
    public static Class<?> getMinecraftClass( String name) {
        return getCanonicalClass(String.valueOf(Refl.NMS_PREFIX) + "." + name);
    }
    
    public static Class<?> getCraftBukkitClass( String name) {
        return getCanonicalClass(String.valueOf(Refl.OBC_PREFIX) + "." + name);
    }
    
    private static Class<?> getCanonicalClass( String canonicalName) {
        try {
            return Class.forName(canonicalName);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }
    
    private static String expandVariables( String name) {
         StringBuffer output = new StringBuffer();
         Matcher matcher = Refl.MATCH_VARIABLE.matcher(name);
        while (matcher.find()) {
             String variable = matcher.group(1);
            String replacement = "";
            if ("nms".equalsIgnoreCase(variable)) {
                replacement = Refl.NMS_PREFIX;
            }
            else if ("obc".equalsIgnoreCase(variable)) {
                replacement = Refl.OBC_PREFIX;
            }
            else {
                if (!"version".equalsIgnoreCase(variable)) {
                    throw new IllegalArgumentException("Unknown variable: " + variable);
                }
                replacement = Refl.VERSION;
            }
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.') {
                replacement = String.valueOf(replacement) + ".";
            }
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(output);
        return output.toString();
    }
    
    public interface ConstructorInvoker
    {
        Object invoke( Object... p0);
    }
    
    public interface FieldAccessor<T>
    {
        T get( Object p0);
        
        void set( Object p0,  Object p1);
        
        boolean hasField( Object p0);
    }
    
    public interface MethodInvoker
    {
        Object invoke( Object p0,  Object... p1);
    }
}
