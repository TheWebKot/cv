package net.web_kot.cv.utils;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class PointUtils {

    private static ImmutableList<Delta> neighborhood = null;

    public static List<Delta> neighborhood3D() {
        if(neighborhood == null) {
            ArrayList<Delta> list = new ArrayList<>(3 * 3 * 3 - 1);

            for(int dx = -1; dx <= 1; dx++)
                for(int dy = -1; dy <= 1; dy++)
                    for(int dz = -1; dz <= 1; dz++) {
                        if(dx == dy && dy == dz && dz == 0) continue;
                        list.add(new Delta(dx, dy, dz));
                    }

            neighborhood = ImmutableList.copyOf(list);
        }

        return neighborhood;
    }

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Delta {

        private final int x, y, z;

    }

}
