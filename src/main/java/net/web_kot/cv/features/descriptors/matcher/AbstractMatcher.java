package net.web_kot.cv.features.descriptors.matcher;

import net.web_kot.cv.features.descriptors.Descriptor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMatcher {

    public abstract List<Pair<Descriptor, Descriptor>> match(List<Descriptor> first, List<Descriptor> second);

    protected ArrayList<Double> distances(Descriptor descriptor, List<Descriptor> candidates) {
        ArrayList<Double> distances = new ArrayList<>();
        for(Descriptor d : candidates) distances.add(descriptor.getVector().distanceTo(d.getVector()));
        return distances;
    }

    protected int getClosest(ArrayList<Double> distances, int exclude) {
        int selectedIndex = -1;
        for(int i = 0; i < distances.size(); i++)
            if(i != exclude && (selectedIndex == -1 || distances.get(i) < distances.get(selectedIndex)))
                selectedIndex = i;

        return selectedIndex;
    }

}
