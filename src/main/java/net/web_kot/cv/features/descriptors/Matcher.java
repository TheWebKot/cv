package net.web_kot.cv.features.descriptors;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Matcher {

    private final double NEXT_NEAREST_DISTANCE_RATIO = 0.8;

    public List<Pair<Descriptor, Descriptor>> match(List<Descriptor> first, List<Descriptor> second) {
        List<Pair<Descriptor, Descriptor>> matching = new ArrayList<>();
        for(Descriptor descriptor : first) {
            Descriptor best = findBestFor(descriptor, second);
            if(best != null && findBestFor(best, first) == descriptor) matching.add(Pair.of(descriptor, best));
        }
        return matching;
    }

    private Descriptor findBestFor(Descriptor descriptor, List<Descriptor> candidates) {
        ArrayList<Double> distances = new ArrayList<>();
        for(Descriptor d : candidates) distances.add(descriptor.getVector().distanceTo(d.getVector()));

        int a = getClosest(distances, -1);
        int b = getClosest(distances, a);

        double r = distances.get(a) / distances.get(b);
        if(r <= NEXT_NEAREST_DISTANCE_RATIO) return candidates.get(a);
        return null;
    }

    private int getClosest(ArrayList<Double> distances, int exclude) {
        int selectedIndex = -1;
        for(int i = 0; i < distances.size(); i++)
            if(i != exclude && (selectedIndex == -1 || distances.get(i) < distances.get(selectedIndex)))
                selectedIndex = i;

        return selectedIndex;
    }

}
