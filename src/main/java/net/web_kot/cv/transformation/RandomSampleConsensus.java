package net.web_kot.cv.transformation;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.*;

public class RandomSampleConsensus {

    private static final int ITERATIONS = 100;
    private static final int MATCHES_TO_PEEK = 4;

    private static final int INLIERS_DISTANCE = 5;

    public static Transformation apply(List<Pair<Descriptor, Descriptor>> matching) {
        return apply(matching, new Random(RandomSampleConsensus.class.getSimpleName().hashCode()));
    }

    public static Transformation apply(List<Pair<Descriptor, Descriptor>> matching, Random random) {
        BitSet best = null;
        for(int i = 0; i < ITERATIONS; i++) {
            Set<Integer> picked = pickMatches(matching, random);
            double[][] h = findMatrix(matching, picked);

            BitSet inliers = new BitSet(matching.size());
            for(int j = 0; j < matching.size(); j++) {
                Pair<Descriptor, Descriptor> match = matching.get(j);
                PointOfInterest a = match.getLeft().getPoint(), b = match.getRight().getPoint();

                Pair<Double, Double> nw = ImageTransformer.apply(h, a.getX(), a.getY());

                double distanceSq = MathUtils.sqr(nw.getLeft() - b.getX()) + MathUtils.sqr(nw.getRight() - b.getY());
                if(distanceSq < INLIERS_DISTANCE * INLIERS_DISTANCE) inliers.set(j, true);
            }

            if(best == null || best.cardinality() < inliers.cardinality()) best = inliers;
        }

        ArrayList<Pair<Descriptor, Descriptor>> filtered = new ArrayList<>();
        for(int i = 0; i < matching.size(); i++)
            if(best.get(i)) filtered.add(matching.get(i));

        HashSet<Integer> picked = new HashSet<>();
        for(int i = 0; i < filtered.size(); i++) picked.add(i);

        return new Transformation(filtered, findMatrix(filtered, picked), findMatrix(filtered, picked, true));
    }

    private static Set<Integer> pickMatches(List<Pair<Descriptor, Descriptor>> matching, Random random) {
        if(matching.size() < MATCHES_TO_PEEK)
            throw new IllegalArgumentException("Matches count must be greater or equal to " + MATCHES_TO_PEEK);

        HashSet<Integer> picked = new HashSet<>();
        while(picked.size() < MATCHES_TO_PEEK) picked.add(random.nextInt(matching.size()));

        return picked;
    }

    private static double[][] findMatrix(List<Pair<Descriptor, Descriptor>> matching, Set<Integer> picked) {
        return findMatrix(matching, picked, false);
    }

    private static double[][] findMatrix(List<Pair<Descriptor, Descriptor>> matching, Set<Integer> picked, boolean r) {
        double[][] matrix = new double[picked.size() * 2][];

        int index = 0;
        for(int value : picked) {
            Pair<Descriptor, Descriptor> match = matching.get(value);
            PointOfInterest a = match.getLeft().getPoint(), b = match.getRight().getPoint();

            if(r) {
                PointOfInterest x = a;
                a = b;
                b = x;
            }

            matrix[index * 2] = new double[] {
                    a.getX(), a.getY(), 1, 0, 0, 0, -b.getX() * a.getX(), -b.getX() * a.getY(), -b.getX()
            };
            matrix[index * 2 + 1] = new double[] {
                    0, 0, 0, a.getX(), a.getY(), 1, -b.getY() * a.getX(), -b.getY() * a.getY(), -b.getY()
            };

            index++;
        }

        RealMatrix mat = new Array2DRowRealMatrix(matrix);
        mat = mat.transpose().multiply(mat);

        SingularValueDecomposition decomposition = new SingularValueDecomposition(mat);
        double[] singular = decomposition.getSingularValues();
        double[][] U = decomposition.getU().getData();

        int min = -1;
        for(int i = 0; i < singular.length; i++)
            if(min == -1 || singular[min] > singular[i]) min = i;

        double[][] res = new double[][] {
                { U[0][min], U[1][min], U[2][min] },
                { U[3][min], U[4][min], U[5][min] },
                { U[6][min], U[7][min], U[8][min] }
        };

        double scale = 1 / res[2][2];
        for(int i = 0; i < res.length; i++)
            for(int j = 0; j < res[i].length; j++) res[i][j] *= scale;

        return res;
    }

}
