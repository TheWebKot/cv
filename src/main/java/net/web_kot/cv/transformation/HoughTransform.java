package net.web_kot.cv.transformation;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.features.descriptors.impl.SIFT;
import net.web_kot.cv.features.descriptors.matcher.MatcherNNDR;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;
import net.web_kot.cv.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HoughTransform {

    private static final int COORDINATE_STEP = 30;
    private static final int ANGLE_CELLS = 12;

    private static final double SCALE_MINIMUM = 1 / 4D;
    private static final double SCALE_MULTIPLIER = 2;
    private static final int SCALE_CELLS = 4;

    private static final int VOTERS_THRESHOLD = 4;

    private final Mat sample, image;

    @Getter
    private BufferedImage votesImage;

    private double[][][][] votes;

    private static class LL extends LinkedList<Pair<Descriptor, Descriptor>> { /* typedef */ }
    private LL[][][][] voters;

    @Getter
    private List<Polygon> objects = new ArrayList<>();

    @Getter
    private List<Pair<Descriptor, Descriptor>> reverseMatching;

    public static HoughTransform find(Mat sample, Mat image) {
        HoughTransform transform = new HoughTransform(sample, image);
        transform.find();
        return transform;
    }

    private void find() {
        int cellsX = (int)Math.ceil(image.getWidth() * 1D / COORDINATE_STEP);
        int cellsY = (int)Math.ceil(image.getHeight() * 1D / COORDINATE_STEP);

        votesImage = IOUtils.toBufferedImage(image);
        for(int i = 1; i < cellsX; i++) {
            int x = COORDINATE_STEP * i;
            DrawUtils.drawLine(votesImage, x, 0, x, image.getHeight() - 1, Color.GRAY);
        }
        for(int i = 1; i < cellsY; i++) {
            int y = COORDINATE_STEP * i;
            DrawUtils.drawLine(votesImage, 0, y, image.getWidth() - 1, y, Color.GRAY);
        }

        List<Descriptor> desc1 = SIFT.calculate(sample, 300);
        List<Descriptor> desc2 = SIFT.calculate(image, 900/*, 5, 1.4*/);

        List<Pair<Descriptor, Descriptor>> matching = MatcherNNDR.INSTANCE.match(desc2, desc1);
        reverseMatching = matching.stream().map(p -> Pair.of(p.getRight(), p.getLeft())).collect(Collectors.toList());

        double sampleCenterX = sample.getWidth() / 2D;
        double sampleCenterY = sample.getHeight() / 2D;

        votes = new double[cellsX][cellsY][ANGLE_CELLS][SCALE_CELLS];
        voters = new LL[cellsX][cellsY][ANGLE_CELLS][SCALE_CELLS];

        for(Pair<Descriptor, Descriptor> match : matching) {
            PointOfInterest pointOnSample = match.getRight().getPoint();
            PointOfInterest pointOnImage = match.getLeft().getPoint();

            double scale = pointOnImage.getSize() / pointOnSample.getSize();
            double angle = match.getRight().getAngle() - match.getLeft().getAngle();

            double vX = (sampleCenterX - pointOnSample.getX()) * scale;
            double vY = (sampleCenterY - pointOnSample.getY()) * scale;

            double centerX = pointOnImage.getX() + vX * Math.cos(angle) - vY * Math.sin(angle);
            double centerY = pointOnImage.getY() + vX * Math.sin(angle) + vY * Math.cos(angle);

            vote(centerX, centerY, scale, angle, match);
            DrawUtils.drawLine(votesImage, pointOnImage.getX(), pointOnImage.getY(),
                               (int)Math.round(centerX), (int)Math.round(centerY));
        }

        for(int x = 0; x < cellsX; x++)
            for(int y = 0; y < cellsY; y++)
                for(int a = 0; a < ANGLE_CELLS; a++)
                    for(int s = 0; s < SCALE_CELLS; s++)
                        if(isVotesLocalMaximum(x, y, a, s) && voters[x][y][a][s].size() > VOTERS_THRESHOLD) {
                            DrawUtils.drawPolygon(votesImage, getPreliminary(x, y, a, s), Color.BLUE, 3, true);
                            objects.add(getLocation(RandomSampleConsensus.apply(voters[x][y][a][s])));
                        }
    }

    private void vote(double x, double y, double scale, double angle, Pair<Descriptor, Descriptor> match) {
        if(x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;

        while(angle < 0) angle += Math.PI * 2;
        while(angle >= Math.PI * 2) angle -= Math.PI * 2;

        // Coordinates
        int xPos = (int)Math.floor(x / COORDINATE_STEP);
        int yPos = (int)Math.floor(y / COORDINATE_STEP);

        // Rotation
        double angleStep = Math.PI * 2 / ANGLE_CELLS;
        int aPos = (int)Math.floor(angle / angleStep);

        // Scale
        int sPos = MathUtils.nearestGeometricProgressionElement(SCALE_MINIMUM, SCALE_MULTIPLIER, scale);
        if(scale < SCALE_MINIMUM * Math.pow(SCALE_MULTIPLIER, sPos)) sPos--;

        if(sPos < 0) sPos = 0;
        if(sPos >= SCALE_CELLS) sPos = SCALE_CELLS - 1;

        // Distribution

        double xDelta = (x % COORDINATE_STEP) / COORDINATE_STEP;

        double yDelta = (y % COORDINATE_STEP) / COORDINATE_STEP;

        double aDelta = (angle % angleStep) / angleStep;

        double scaleLeft = SCALE_MINIMUM * Math.pow(SCALE_MULTIPLIER, sPos);
        double scaleRight = SCALE_MINIMUM * Math.pow(SCALE_MULTIPLIER, sPos + 1);
        double sDelta = (scale - scaleLeft) / (scaleRight - scaleLeft);

        //noinspection SuspiciousNameCombination
        List<Pair<Integer, Double>> d = ImmutableList.of(
                Pair.of(xPos, xDelta),
                Pair.of(yPos, yDelta),
                Pair.of(aPos, aDelta),
                Pair.of(sPos, sDelta)
        );

        vote(d, 0, new int[d.size()], new double[d.size()], match);
    }

    private void vote(List<Pair<Integer, Double>> list, int step,
                      int[] indices, double[] result, Pair<Descriptor, Descriptor> match) {
        if(step == list.size()) {
            double value = 1;
            for(double d : result) value *= d;

            vote(indices[0], indices[1], indices[2], indices[3], value, match);
        } else {
            Pair<Integer, Double> current = list.get(step);
            double value = 1 - Math.abs(current.getRight() - 0.5);

            indices[step] = current.getLeft();
            result[step] = value;
            vote(list, step + 1, indices, result, match);

            indices[step] = current.getLeft() + (current.getRight() < 0.5 ? -1 : 1);
            result[step] = 1 - value;
            vote(list, step + 1, indices, result, match);
        }
    }

    private void vote(int xPos, int yPos, int aPos, int sPos, double value, Pair<Descriptor, Descriptor> match) {
        if(xPos < 0 || xPos >= votes.length) return;
        if(yPos < 0 || yPos >= votes[0].length) return;
        if(sPos < 0 || sPos >= SCALE_CELLS) return;

        if(aPos < 0) aPos += ANGLE_CELLS;
        if(aPos >= ANGLE_CELLS) aPos -= ANGLE_CELLS;

        votes[xPos][yPos][aPos][sPos] += value;

        if(voters[xPos][yPos][aPos][sPos] == null) voters[xPos][yPos][aPos][sPos] = new LL();
        voters[xPos][yPos][aPos][sPos].add(match);
    }

    private boolean isVotesLocalMaximum(int x, int y, int a, int s) {
        for(int dx = -1; dx <= 1; dx++) {
            int nX = x + dx;
            if(nX < 0 || nX >= votes.length) continue;

            for(int dy = -1; dy <= 1; dy++) {
                int nY = y + dy;
                if(nY < 0 || nY >= votes[0].length) continue;

                for(int da = -1; da <= 1; da++) {
                    int nA = a + da;

                    if(nA < 0) nA += ANGLE_CELLS;
                    if(nA >= ANGLE_CELLS) nA -= ANGLE_CELLS;

                    for(int ds = -1; ds <= 1; ds++) {
                        int nS = s + ds;
                        if(nS < 0 || nS >= SCALE_CELLS) continue;

                        if(dx == 0 && dy == 0 && da == 0 && ds == 0) continue;

                        if(votes[nX][nY][nA][nS] >= votes[x][y][a][s]) return false;
                    }
                }
            }
        }
        return true;
    }

    private Polygon getPreliminary(int pX, int pY, int pA, int pS) {
        int x = (int)Math.round((pX + 0.5) * COORDINATE_STEP);
        int y = (int)Math.round((pY + 0.5) * COORDINATE_STEP);

        double angle = (pA + 0.5) * (Math.PI * 2 / ANGLE_CELLS);
        double scale = SCALE_MINIMUM * Math.pow(SCALE_MULTIPLIER, pS + 0.5);

        double sampleCenterX = sample.getWidth() / 2D;
        double sampleCenterY = sample.getHeight() / 2D;

        Polygon polygon = new Polygon();
        for(Pair<Integer, Integer> corner : getSampleCorners()) {
            double vX = (corner.getLeft() - sampleCenterX) * scale;
            double vY = (corner.getRight() - sampleCenterY) * scale;

            double centerX = x + vX * Math.cos(angle) - vY * Math.sin(angle);
            double centerY = y + vX * Math.sin(angle) + vY * Math.cos(angle);

            polygon.addPoint((int)Math.round(centerX), (int)Math.round(centerY));
        }

        return polygon;
    }

    private Polygon getLocation(Transformation transformation) {
        Polygon polygon = new Polygon();
        double[][] matrix = transformation.getReverseMatrix();

        for(Pair<Integer, Integer> corner : getSampleCorners()) {
            Pair<Double, Double> point = ImageTransformer.apply(matrix, corner.getLeft(), corner.getRight());
            polygon.addPoint((int)Math.round(point.getLeft()), (int)Math.round(point.getRight()));
        }

        return polygon;
    }

    private List<Pair<Integer, Integer>> getSampleCorners() {
        return ImmutableList.of(
                Pair.of(0, 0),
                Pair.of(sample.getWidth(), 0),
                Pair.of(sample.getWidth(), sample.getHeight()),
                Pair.of(0, sample.getHeight())
        );
    }

}
