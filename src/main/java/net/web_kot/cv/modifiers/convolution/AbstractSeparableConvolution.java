package net.web_kot.cv.modifiers.convolution;

import net.web_kot.cv.datastructure.OddVector;
import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.AbstractModifier;

public abstract class AbstractSeparableConvolution extends AbstractModifier<AbstractConvolution.ConvolutionArguments> {
    
    @Override
    public void apply(GreyscaleImage source, GreyscaleImage target, AbstractConvolution.ConvolutionArguments args) {
        OddVector xVector = getVectorX(), yVector = getVectorY();
        int k = xVector.getSize(), h = yVector.getSize();
        
        GreyscaleImage tmp = new GreyscaleImage(source.getWidth(), source.getHeight());
        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;
                
                for(int dx = -k; dx <= k; dx++)
                    value += source.getPixel(x - dx, y, args.mode) * xVector.getWeight(dx);
                
                tmp.setPixel(x, y, value);
            }
        
        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;
                
                for(int dy = -h; dy <= h; dy++)
                    value += tmp.getPixel(x, y - dy, args.mode) * yVector.getWeight(dy);
                
                target.setPixel(x, y, value);
            }
    }
    
    public abstract OddVector getVectorX();
    
    public abstract OddVector getVectorY();
    
}
