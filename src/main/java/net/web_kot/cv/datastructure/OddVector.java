package net.web_kot.cv.datastructure;

import lombok.Getter;
import lombok.NonNull;

public class OddVector {
    
    @Getter
    private final double[] data;
    @Getter
    private final int size;
    
    private OddVector(double[] data) {
        this.data = data;
        this.size = data.length / 2;
    }
    
    public double getWeight(int x) {
        return data[x + size];
    }
    
    public static OddVector of(@NonNull double[] data) {
        if(data.length % 2 == 0)
            throw new IllegalArgumentException("Vector size must be odd");
        return new OddVector(data);
    }
    
}
