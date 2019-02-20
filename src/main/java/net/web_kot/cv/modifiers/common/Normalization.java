package net.web_kot.cv.modifiers.common;

import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.NoArgsAbstractModifier;

public class Normalization extends NoArgsAbstractModifier {
    
    @Override
    public void apply(GreyscaleImage source, GreyscaleImage target) {
        double min = source.getPixel(0, 0), max = source.getPixel(0, 0);
        for(double value : source.getData()) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        double newMin = 0, newMax = 1;
        for(int i = 0; i < source.getData().length; i++)
            target.getData()[i] = (source.getData()[i] - min) * (newMax - newMin) / (max - min) + newMin;
    }
    
}
