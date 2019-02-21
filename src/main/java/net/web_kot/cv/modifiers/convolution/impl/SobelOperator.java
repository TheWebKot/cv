package net.web_kot.cv.modifiers.convolution.impl;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.web_kot.cv.datastructure.OddVector;
import net.web_kot.cv.image.EdgeWrapMode;
import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.AbstractModifier;
import net.web_kot.cv.modifiers.ModifierArguments;
import net.web_kot.cv.modifiers.common.Normalization;
import net.web_kot.cv.modifiers.convolution.AbstractSeparableConvolution;
import net.web_kot.cv.utils.IOUtils;
import net.web_kot.cv.utils.MathUtils;

import java.io.File;

public class SobelOperator extends AbstractModifier<SobelOperator.SobelArguments> {
    
    private static final OddVector vectorA = OddVector.of(new double[] { -1, 0, 1 });
    private static final OddVector vectorB = OddVector.of(new double[] { 1, 2, 1 });

    @Accessors(chain = true)
    public static class SobelArguments extends ModifierArguments {

        @Setter
        protected EdgeWrapMode mode = EdgeWrapMode.COPY;
        @Setter
        protected File tmpFilesPath = null;

    }

    @Override
    public void apply(GreyscaleImage source, GreyscaleImage target, SobelArguments args) {
        GreyscaleImage xImage = source.modifier(SobelX.class).setMode(args.mode).apply();
        GreyscaleImage yImage = source.modifier(SobelY.class).setMode(args.mode).apply();
        
        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double val = Math.sqrt(MathUtils.sqr(xImage.getPixel(x, y)) + MathUtils.sqr(yImage.getPixel(x, y)));
                target.setPixel(x, y, val);
            }
        
        if(args.tmpFilesPath != null) {
            IOUtils.writeToPngFile(xImage.modifier(Normalization.class).apply(), new File(args.tmpFilesPath, "x.png"));
            IOUtils.writeToPngFile(yImage.modifier(Normalization.class).apply(), new File(args.tmpFilesPath, "y.png"));
        }
    }
    
    public static class SobelX extends AbstractSeparableConvolution {
        
        @Override
        public OddVector getVectorX() {
            return vectorA;
        }

        @Override
        public OddVector getVectorY() {
            return vectorB;
        }
        
    }

    public static class SobelY extends AbstractSeparableConvolution {

        @Override
        public OddVector getVectorX() {
            return vectorB;
        }

        @Override
        public OddVector getVectorY() {
            return vectorA;
        }

    }
    
}
