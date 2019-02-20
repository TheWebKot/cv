package net.web_kot.cv.modifiers.convolution;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.web_kot.cv.image.EdgeWrapMode;
import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.AbstractModifier;
import net.web_kot.cv.modifiers.ModifierArguments;
import net.web_kot.cv.datastructure.Kernel;

public abstract class AbstractConvolution extends AbstractModifier<AbstractConvolution.ConvolutionArguments> {

    @Accessors(chain = true)
    public static class ConvolutionArguments extends ModifierArguments {
        
        @Setter
        protected EdgeWrapMode mode = EdgeWrapMode.COPY;
        
    }
    
    @Override
    public void apply(GreyscaleImage source, GreyscaleImage target, ConvolutionArguments args) {
        Kernel kernel = getKernel();
        int k = kernel.getSizeX(), h = kernel.getSizeY();
        
        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;
                
                for(int dx = -k; dx <= k; dx++)
                    for(int dy = -h; dy <= h; dy++)
                        value += source.getPixel(x + dx, y + dy, args.mode) * kernel.getWeight(dx, dy);
                
                target.setPixel(x, y, value);
            }
    }
    
    public abstract Kernel getKernel();
    
}
