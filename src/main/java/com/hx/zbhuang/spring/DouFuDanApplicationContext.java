package com.hx.zbhuang.spring;

import com.hx.zbhuang.annotion.Autowired;
import com.hx.zbhuang.annotion.Component;
import com.hx.zbhuang.annotion.ComponentScan;
import com.hx.zbhuang.annotion.Scope;
import com.hx.zbhuang.spring.bean.BeanDefinition;
import com.hx.zbhuang.spring.bean.ScopeEum;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;


public class DouFuDanApplicationContext {
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private ConcurrentHashMap<String,Object> singletonMap = new ConcurrentHashMap<String, Object>();

    public DouFuDanApplicationContext(Class appConfigClass) {
        scan(appConfigClass);
        instanceSingletonBean();
    }

    private void instanceSingletonBean() {
        for (String beanName:beanDefinitionMap.keySet()) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if(beanDefinition.getScopeEum().equals(ScopeEum.singleton)) {
                    Object o = doCreateBean(beanName, beanDefinition);
                    singletonMap.put(beanName,o);
                }
        }
    }

    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getBeanClass();
        try {
            Constructor declaredConstructor = clazz.getDeclaredConstructor();
            Object o = declaredConstructor.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f: declaredFields) {
                if(f.isAnnotationPresent(Autowired.class)) {
                    String fName = f.getName();
                    Object bean = getBean(fName);
                    f.setAccessible(true);
                    f.set(o,bean);
                }
            }
            return o;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class appConfigClass) {
        ComponentScan componentScanAnnotation = (ComponentScan) appConfigClass.getAnnotation(ComponentScan.class);
        String pathPackage = componentScanAnnotation.value();
        pathPackage = pathPackage.replace(".","/");
        ClassLoader classLoader = DouFuDanApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(pathPackage);
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            for (File f:file.listFiles()){
               try {
                   String fileName = f.getAbsolutePath();
                   fileName = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class")).replace("\\",".");
                   Class clazz = classLoader.loadClass(fileName);
                   if(clazz.isAnnotationPresent(Component.class)){
                       BeanDefinition beanDefinition = new BeanDefinition();
                       Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
                       String beanName = componentAnnotation.value();
                       beanDefinition.setBeanClass(clazz);
                       if(clazz.isAnnotationPresent(Scope.class)) {
                           Scope scopeAnnotation = (Scope) clazz.getAnnotation(Scope.class);
                           String scopeValue = scopeAnnotation.value();
                           if(scopeValue.equals(ScopeEum.singleton.name())){
                               beanDefinition.setScopeEum(ScopeEum.singleton);
                           }else if (scopeValue.equals(ScopeEum.prototype)) {
                               beanDefinition.setScopeEum(ScopeEum.prototype);
                           }
                       } else {
                           beanDefinition.setScopeEum(ScopeEum.singleton);
                       }
                       beanDefinitionMap.put(beanName,beanDefinition);
                   }
               } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if(singletonMap.containsKey(beanName)) {
            return singletonMap.get(beanName);
        } else {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            return doCreateBean(beanName,beanDefinition);
        }
    }
}
