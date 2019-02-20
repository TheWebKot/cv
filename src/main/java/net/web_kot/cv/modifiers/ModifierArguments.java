package net.web_kot.cv.modifiers;

import net.web_kot.cv.image.GreyscaleImage;

public class ModifierArguments {
    
    protected AbstractModifier parent;
    protected GreyscaleImage source, target;
    
    protected final void setInvocationArguments(AbstractModifier parent, GreyscaleImage source, GreyscaleImage target) {
        this.parent = parent;
        this.source = source;
        this.target = target;
    }
    
    @SuppressWarnings("unchecked")
    public final GreyscaleImage apply() {
        parent.apply(source, target, this);
        return target;
    }
    
}
