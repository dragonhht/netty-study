package com.github.dragonhht.rpc.utils;

import com.github.dragonhht.rpc.annos.Service;
import com.github.dragonhht.rpc.model.ServiceModel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 注解@Service的工具类.
 *
 * @author: huang
 * @Date: 2019-5-29
 */
@Slf4j
public enum ServiceUtil {
    /** 实例. */
    INSTANCE;

    /**
     * 获取包下的所有class名
     * @param packageName 包名
     * @param expandChildren 是否获取子包
     * @return 包下的class名
     */
    private Set<String> getClassName(String packageName, boolean expandChildren) {
        Set<String> classes = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = classLoader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                classes = getClassFileName(url.getPath(), expandChildren);
            } else if ("jar".equals(type)) {
                classes = getClassNameByJar(url.getPath(), expandChildren);
            }
        } else {
            classes = getClassNameByJars(((URLClassLoader) classLoader).getURLs(), packagePath, expandChildren);
        }
        return classes;
    }

    /**
     * 查询包下的class
     * @param packagePath
     * @param expandChildren
     * @return
     */
    private Set<String> getClassFileName(String packagePath, boolean expandChildren) {
        Set<String> classNames = new HashSet<>();
        File file = new File(packagePath);
        File[] files = file.listFiles();
        for (File child : files) {
            if (child.isDirectory()) {
                if (expandChildren) {
                    classNames.addAll(getClassFileName(child.getPath(), expandChildren));
                }
            } else {
                String childPath = child.getPath();
                if (childPath.endsWith(".class")) {
                    childPath = childPath.substring(childPath.indexOf("\\classes") + 9,
                            childPath.lastIndexOf("."));
                    childPath = childPath.replace("\\", ".");
                    classNames.add(childPath);
                }
            }
        }
        return classNames;
    }

    /**
     * 获取jar包中某包下的所有class
     * @param jarPath
     * @param expandChildren
     * @return
     */
    private Set<String> getClassNameByJar(String jarPath, boolean expandChildren) {
        Set<String> classesName = new HashSet<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (expandChildren) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".")
                                    .substring(0, entryName.lastIndexOf("."));
                            classesName.add(entryName);
                        }
                    } else {
                        int index = entryName.lastIndexOf(".");
                        String packagePathTemp = null;
                        if (index != -1) {
                            packagePathTemp = entryName.substring(0, index);
                        } else {
                            packagePath = entryName;
                        }
                        if (packagePath.equals(packagePathTemp)) {
                            entryName = entryName.replace("/", ".")
                                    .substring(0, entryName.lastIndexOf("."));
                            classesName.add(entryName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("获取包{}下的类失败", packagePath.replace("/", "."));
        }
        return classesName;
    }

    /**
     * 从所有jar包中搜索
     * @param urls
     * @param packagePath
     * @param expandChildren
     * @return
     */
    private Set<String> getClassNameByJars(URL[] urls, String packagePath, boolean expandChildren) {
        Set<String> classNames = new HashSet<>();
        if (urls != null) {
            for (URL url : urls) {
                String path = url.getPath();
                if (packagePath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = path + "!/" + packagePath;
                classNames.addAll(getClassNameByJar(jarPath, expandChildren));
            }
        }
        return classNames;
    }

    /**
     * 获取指定包下使用com.github.dragonhht.rpc.annos.Service注解标志的类
     * @param packagePath
     * @param expandChildren
     * @return
     */
    public Set<Class> getServiceClass(String packagePath, boolean expandChildren) {
        Set<String> classNames = getClassName(packagePath, expandChildren);
        Set<Class> classes = new HashSet<>();
        try {
            for (String className : classNames) {
                Class clazz = Class.forName(className);
                if (clazz != null) {
                    if (clazz.isAnnotationPresent(Service.class)) {
                        classes.add(clazz);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("获取类失败: ", e);
        }
        return classes;
    }

    /**
     * 获取服务类信息.
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ServiceModel getServiceMsg(Class clazz) throws IllegalAccessException, InstantiationException {
        if (clazz.isAnnotationPresent(Service.class)) {
            Service service = (Service) clazz.getAnnotation(Service.class);
            String interfaceName = service.interfaceName().getName();
            ServiceModel model = new ServiceModel();
            model.setInterfaceName(interfaceName);
            Object instance = clazz.newInstance();
            model.setInstance(instance);
            return model;
        }
        return null;
    }

}
