package net.web_kot.cv.modifiers;

import net.web_kot.cv.image.GreyscaleImage;

public abstract class NoArgsAbstractModifier extends AbstractModifier<ModifierArguments> {

    @Override
    public final void apply(GreyscaleImage source, GreyscaleImage target, ModifierArguments args) {
        apply(source, target);
    }
    
    public abstract void apply(GreyscaleImage source, GreyscaleImage target);
    
}
