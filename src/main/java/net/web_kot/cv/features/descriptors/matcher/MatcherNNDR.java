package net.web_kot.cv.features.descriptors.matcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.web_kot.cv.features.descriptors.Descriptor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatcherNNDR extends AbstractMatcher {

    public static final MatcherNNDR INSTANCE = new MatcherNNDR();

    private static final double NEXT_NEAREST_DISTANCE_RATIO = 0.8;

    @Override
    public List<Pair<Descriptor, Descriptor>> match(List<Descriptor> first, List<Descriptor> second) {
        List<Pair<Descriptor, Descriptor>> matching = new ArrayList<>();
        for(Descriptor descriptor : first) {
            Descriptor best = findBestFor(descriptor, second);
            if(best != null) matching.add(Pair.of(descriptor, best));
        }
        return matching;
    }

    private Descriptor findBestFor(Descriptor descriptor, List<Descriptor> candidates) {
        ArrayList<Double> distances = distances(descriptor, candidates);

        int a = getClosest(distances, -1);
        int b = getClosest(distances, a);

        return (distances.get(a) / distances.get(b)) <= NEXT_NEAREST_DISTANCE_RATIO ? candidates.get(a) : null;
    }

}