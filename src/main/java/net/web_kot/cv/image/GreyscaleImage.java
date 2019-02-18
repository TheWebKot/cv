package net.web_kot.cv.image;

import lombok.Getter;
import lombok.NonNull;
import net.web_kot.cv.utils.MathUtils;

import static net.web_kot.cv.utils.ColorUtils.*;

import java.awt.image.BufferedImage;

public class GreyscaleImage {
    
    @Getter
    private final int width, height;
    private final double[] data;
    
    public GreyscaleImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new double[width * height];
    }

    public static GreyscaleImage fromBufferedImage(@NonNull BufferedImage source) {
        GreyscaleImage image = new GreyscaleImage(source.getWidth(), source.getHeight());
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++)
                image.setPixel(x, y, rgbToGreyscale(source.getRGB(x, y)));
        
        return image;
    }
    
    public void setPixel(int x, int y, double value) {
        data[getIndex(x, y)] = value;
    }
    
    public double getPixel(int x, int y) {
        return data[getIndex(x, y)];
    }
    
    public double getPixel(int x, int y, @NonNull EdgeWrapMode mode) {
        switch(mode) {
            case BLACK:
                if(isCoordinatesOutOfBounds(x, y)) return 0;
                break;
            case COPY:
                x = MathUtils.normalizeNumber(0, x, width);
                y = MathUtils.normalizeNumber(0, y, height);
                break;
            case MIRROR:
                if(x < 0 || x >= width) x = x - (x % width) * 2 - 1;
                if(y < 0 || y >= height) y = y - (y % height) * 2 - 1;
                break;
            case WRAP:
                x = (x + width) % width;
                y = (y + height) % height;
                break;
            default:
                throw new IllegalArgumentException("Unknown edge wrap mode " + mode);
        }
        return getPixel(x, y);
    }
    
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++) 
                image.setRGB(x, y, greyscaleToRgb(getPixel(x, y)));
        
        return image;
    }
    
    private int getIndex(int x, int y) {
        if(isCoordinatesOutOfBounds(x, y)) throw new IllegalArgumentException("Coordinates out of bounds"); 
        return y * width + x;
    }
    
    private boolean isCoordinatesOutOfBounds(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }
    
}
