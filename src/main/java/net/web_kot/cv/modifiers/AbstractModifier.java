package net.web_kot.cv.modifiers;

import lombok.SneakyThrows;
import net.web_kot.cv.image.GreyscaleImage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractModifier<A extends ModifierArguments> {
    
    @SneakyThrows
    public final A createArguments(GreyscaleImage source, GreyscaleImage target) {
        A arguments = getGenericArgumentClass().newInstance();
        arguments.setInvocationArguments(this, source, target);
        return arguments;
    }

    @SneakyThrows
    private Class<A> getGenericArgumentClass() {
        Class clazz = getClass();
        Type genericSuperclass;
        
        for(;;) {
            genericSuperclass = clazz.getGenericSuperclass();
            if(genericSuperclass instanceof ParameterizedType) break;
            clazz = clazz.getSuperclass();
        }

        //noinspection unchecked
        return (Class<A>)((ParameterizedType)genericSuperclass).getActualTypeArguments()[0];
    }
    
    public abstract void apply(GreyscaleImage source, GreyscaleImage target, A args);
    
}
