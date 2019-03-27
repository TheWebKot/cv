package net.web_kot.cv.features.descriptors.matcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.web_kot.cv.features.descriptors.Descriptor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatcherMutualChoice extends AbstractMatcher {

    public static final MatcherMutualChoice INSTANCE = new MatcherMutualChoice();

    @Override
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

        return candidates.get(getClosest(distances, -1));
    }

}
