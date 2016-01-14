package com.egopulse.bean.gen;

import com.egopulse.gen.Models;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information extracted from a POJO type, for now only extract getter/setter
 */
public class BeanInfo {
    private final TypeElement type;
    private final List<Property> properties = new ArrayList<>();
    private final Map<String, Property> propertyMap = new HashMap<>();

    public BeanInfo(TypeElement type) {
        this.type = type;
    }

    public TypeElement getType() {
        return type;
    }

    public boolean shouldHaveNonDefaultConstructor() {
        return properties.stream().anyMatch(p-> !p.hasBothGetterSetter());
    }

    public void addSetter(TypeMirror type, String name, ExecutableElement element) {
        Property prop = propertyMap.get(name);
        if (prop == null) {
            prop = new Property(type, name);
            properties.add(prop);
            propertyMap.put(name, prop);
        } else if (prop.type != type) {
            throw new IllegalArgumentException(String.format("A property setter with same name (%s) has been added", name));
        } else if (prop.getGetter() == null) {
            throw new IllegalArgumentException(String.format("No getter for property %s", name));
        }
        prop.setSetter(element);
    }

    public void addGetter(TypeMirror type, String name, ExecutableElement element) {
        Property prop = propertyMap.get(name);
        if (prop == null) {
            prop = new Property(type, name);
            properties.add(prop);
            propertyMap.put(name, prop);
        } else if (prop.type != type) {
            throw new IllegalArgumentException(String.format("A property getter with same name (%s) has been added", name));
        }
        prop.setGetter(element);
    }

    public List<Property> getProperties() {
        return properties;
    }

    public static BeanInfo fromType(TypeElement typeElement, Models models) {
        BeanInfo info = new BeanInfo(typeElement);
        List<ExecutableElement> methods = models.getMethods(typeElement);
        methods.stream().filter(Models::isGetter).forEach(m-> info.addGetter(m.getReturnType(), Models.getPropNameFromGetter(m), m));
        methods.stream().filter(Models::isSetter).forEach(m-> info.addSetter(m.getParameters().get(0).asType(), Models.getPropNameFromSetter(m), m));
        return info;
    }

    public class Property {
        private final String name;
        private final TypeMirror type;
        private ExecutableElement setter;
        private ExecutableElement getter;

        private Property(TypeMirror type, String name) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public TypeMirror getType() {
            return type;
        }

        public ExecutableElement getSetter() {
            return setter;
        }

        public void setSetter(ExecutableElement setter) {
            this.setter = setter;
        }

        public ExecutableElement getGetter() {
            return getter;
        }

        public void setGetter(ExecutableElement getter) {
            this.getter = getter;
        }

        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            T t = null;
            if (getter != null) {
                t = getter.getAnnotation(annotationType);
            }
            if (t == null && setter != null) {
                t = setter.getAnnotation(annotationType);
            }
            return t;
        }

        public boolean hasBothGetterSetter() {
            return getter != null && setter != null;
        }

        public String getGetterName() {
            return getter.getSimpleName().toString();
        }

        public String getSetterName() {
            return setter.getSimpleName().toString();
        }

        public boolean hasGetter() {
            return getter != null;
        }

        public boolean hasSetter() {
            return setter != null;
        }
    }
}
