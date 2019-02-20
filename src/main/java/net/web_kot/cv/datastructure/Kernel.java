package net.web_kot.cv.datastructure;

import lombok.Getter;
import lombok.NonNull;

public class Kernel {
    
    @Getter
    private final double[][] data;
    @Getter
    private final int sizeX, sizeY;
    
    private Kernel(double[][] data) {
        this.data = data;
        this.sizeX = data.length / 2;
        this.sizeY = data[0].length / 2;
    }
    
    public double getWeight(int x, int y) {
        return data[x + sizeX][y + sizeY];
    }
    
    public static Kernel of(@NonNull double[][] data) {
        if(data.length % 2 == 0 || data[1].length % 2 == 0)
            throw new IllegalArgumentException("Kernel dimensions must be odd");
        return new Kernel(data);
    }
    
}
