package com.safedjio.internship.task4_minispring;

import com.safedjio.internship.task4_minispring.annotation.Autowired;
import com.safedjio.internship.task4_minispring.annotation.Component;
import com.safedjio.internship.task4_minispring.lifecycle.InitializingBean;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MiniApplicationContext {
    private static final Logger logger = Logger.getLogger(MiniApplicationContext.class.getName());

    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();

    public MiniApplicationContext(String basePackage) {
        logger.info("Context is starting... Scanning package: " + basePackage);
        try {
            List<Class<?>> classes = scanPackage(basePackage);
            createBeans(classes);
            injectDependencies();
            initializeBeans();

        } catch (Exception e) {
            logger.severe("Failed to initialize context: " + e.getMessage());
            throw new RuntimeException("Context initialization failed", e);
        }
        logger.info("Context started successfully! Beans loaded: " + beans.size());
    }

    public <T> T getBean(Class<T> type) {
        Object bean = beans.get(type);
        if (bean == null) {
            throw new RuntimeException("No bean found of type: " + type.getName());
        }
        return type.cast(bean);
    }

    private void createBeans(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(clazz, instance);
                logger.info("Created bean: " + clazz.getSimpleName());
            }
        }
    }

    private void injectDependencies() throws Exception {
        for (Object bean : beans.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> dependencyType = field.getType();
                    Object dependencyInstance = beans.get(dependencyType);

                    if (dependencyInstance == null) {
                        throw new RuntimeException("Unsatisfied dependency: " + dependencyType.getName() + " for bean " + bean.getClass().getName());
                    }
                    field.setAccessible(true);
                    field.set(bean, dependencyInstance);
                    logger.info("Injected " + dependencyType.getSimpleName() + " into " + bean.getClass().getSimpleName());
                }
            }
        }
    }

    private void initializeBeans() {
        for (Object bean : beans.values()) {
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
                logger.info("Initialized bean: " + bean.getClass().getSimpleName());
            }
        }
    }

    private List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource != null) {
            String decodedPath = java.net.URLDecoder.decode(resource.getFile(), "UTF-8");
            File directory = new File(decodedPath);

            if (directory.exists()) {
                for (File file : directory.listFiles()) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        classes.add(Class.forName(className));
                    }
                }
            }
        }
        return classes;
    }
}
