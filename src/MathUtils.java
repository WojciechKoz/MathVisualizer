import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Double.*;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

/**
 * Class that implements all math functions and algorithms.
 * TODO In the future it should be divided into smaller classes.
 */
public class MathUtils {
    /**
     * function that check if value is in the [min, max] range. If it is then returns val.
     * If val > max then returns max and if val < min then returns min.
     * @param val - value that should be in range [min, max]
     * @param min - lower bound of the range
     * @param max - upper bound of the range
     * @return val if val <- [min, max], max if val > max, min if val < min
     */
    static double clamp(double val, double min, double max) {
        return min(max(val,min),max);
    }

    /**
     * Calculates the distance between two points in R^2. (x1, y1) and (x2, y2)
     * @param x1 - x coordinate of the first point
     * @param y1 - y coordinate of the first point
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @return distance between (x1, y1) and (x2, y2)
     */
    static double dist(double x1, double y1, double x2, double y2) {
        return sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }


    static double dist(Point2D A, Point2D B) {
        return sqrt((A.x-B.x)*(A.x-B.x) + (A.y-B.y)*(A.y-B.y));
    }


    static int argmax(Integer[] values) {
        int max=values[0]-1, maxInd=0;
        boolean exAequo = false;

        for(int i = 0; i < values.length; i++) {
            if(values[i] >= max) {
                exAequo = (values[i] == max);
                max = values[i];
                maxInd = i;
            }

        }
        return exAequo ? -1 : maxInd;
    }


    /**
     * Calculates the mean value of the numbers in the list.
     * @param numbers - list of numbers of which mean value is calculated
     * @return mean value of numbers
     */
    static double mean(ArrayList<Double> numbers) {
        double sum = 0;
        for(Double num: numbers) sum += num;
        return sum / numbers.size();
    }

    /**
     * Calculates the variance with formula VX = EX[(X - EX)^2]
     * @param numbers - list of numbers of which variance is calculated
     * @return variance of these numbers
     */
    static double variance(ArrayList<Double> numbers) {
        double sum = 0;
        double mean = mean(numbers);

        for(Double num: numbers) sum += (num-mean)*(num-mean);
        return sum/numbers.size();
    }

    /**
     * returns standard deviation of the list of numbers using formula std = sqrt(VX)
     * @param numbers - list of numbers of which standard deviation is calculated
     * @return standard deviation of given numbers
     */
    static double stdDev(ArrayList<Double> numbers) {
        return sqrt(variance(numbers));
    }

    /**
     *
     * @param xs - first vector
     * @param ys - second vector
     * @return dot product of two vectors
     */
    static double dotProd(List<Double> xs, List<Double> ys) {
        double sum = 0;
        for(int i = 0; i < xs.size(); i++) {
            sum += xs.get(i)*ys.get(i);
        }
        return sum;
    }

    /**
     * gets list of x coordinates of all given samples
     * @param samples - list of samples of which x coordinates are got.
     * @return - list of x coordinates of given samples
     */
    static ArrayList<Double> getXs(ArrayList<Sample> samples) {
        ArrayList<Double> xs = new ArrayList<>();

        for(Sample s: samples) {
            xs.add(s.getX());
        }
        return xs;
    }

    /**
     * gets list of y coordinates of all given samples
     * @param samples - list of samples of which y coordinates are got.
     * @return - list of y coordinates of given samples
     */
    static ArrayList<Double> getYs(ArrayList<Sample> samples) {
        ArrayList<Double> ys = new ArrayList<>();

        for(Sample s: samples) {
            ys.add(s.getY());
        }
        return ys;
    }

    /**
     * calculates mean of numbers and moves all of them on that average value.
     * That means that new numbers have mean exactly equals to 0
     * @param numbers - list of numbers to centered
     */
    static void zeroCenter(ArrayList<Double> numbers) {
        double mean = mean(numbers);

        for(int i = 0; i < numbers.size(); i++) {
            numbers.set(i, numbers.get(i) - mean);
        }
    }

    /**
     * calculates mean of x and y coordinates and moves all of samples on that average vector.
     * That means that new samples have mean exactly equals in point (0,0)
     * @param samples - list of samples to centered
     */
    static void zeroCenterSamples(ArrayList<Sample> samples) {
        double meanX = mean(getXs(samples));
        double meanY = mean(getYs(samples));

        moveAllSamples(samples, -meanX, -meanY);
    }

    /**
     * move all samples on vector (dx, dy)
     * @param samples - list of samples to moved
     * @param dx - x difference
     * @param dy - y difference
     */
    static void moveAllSamples(ArrayList<Sample> samples, double dx, double dy) {
        for (Sample sample : samples) {
            sample.move(dx, dy);
        }
    }

    /**
     * Calculates covariance of x and y coordinates using formula Cov(X, Y) = E[(X-EX)*(Y-EY)]
     * @param samples - list of samples of which covariance will be calculated
     * @return - covariance between x and y coordinates of all samples
     */
    static double covariance(ArrayList<Sample> samples) {
        if(samples.size() == 0) { return 0; }

        ArrayList<Double> xs = getXs(samples);
        ArrayList<Double> ys = getYs(samples);

        zeroCenter(xs);
        zeroCenter(ys);

        return dotProd(xs, ys) / samples.size();
    }

    /**
     * computes covariance matrix for given data in R^2
     * since Cov(X,Y) = Cov(Y,X) then covariance matrix will always be symmetric
     * if samples list is empty returns zero matrix to avoid dividing by 0
     * @param samples - current samples in the simulation
     * @return Matrix [Var(X)  , Cov(X,Y) ]
     *                [Cov(X,Y), Var(Y)   ]
     */
    static Matrix2x2 covarianceMatrix(ArrayList<Sample> samples) {
        if(samples.size() == 0) { return new Matrix2x2(0,0,0,0); }

        ArrayList<Double> xs = getXs(samples);
        ArrayList<Double> ys = getYs(samples);

        zeroCenter(xs);
        zeroCenter(ys);

        // when data is centered compute matrix A^T * A and divide all values by amount of samples to receive Cov matrix
        double varX = dotProd(xs, xs) / samples.size();
        double covXY = dotProd(xs, ys) / samples.size();
        double varY = dotProd(ys, ys) / samples.size();
        return new Matrix2x2(varX, covXY, covXY, varY);
    }

    /**
     * @param samples - list of samples in the simulation
     * @return the correlation between x and y coordinates
     */
    static double correlation(ArrayList<Sample> samples) {
        return covariance(samples)/(stdDev(getXs(samples))*stdDev(getYs(samples)));
    }

    /**
     * fit the model of linear regression with given samples
     * @param samples - the list of samples to which the straight line is determined
     * @return table of [a, b]
     */
    static double[] fitLinearRegressionModel(ArrayList<Sample> samples) {
        ArrayList<Double> xs = getXs(samples);
        ArrayList<Double> ys = getYs(samples);

        double rho = correlation(samples);
        double devX = stdDev(xs);
        double devY = stdDev(ys);

        double a = rho*devY/devX;

        return new double[] {a, mean(ys)-a*mean(xs)};
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + exp(-x));
    }

    public static double sigmoidPrime(double x) {
        return sigmoid(x)*(1 - sigmoid(x));
    }

    /**
     * fits the model of logistic regression in R^2.
     * takes only red and blue samples and performs zero centering
     * then finds the best weights using stochastic gradient descent with batch size = 1
     * last thing is to move all samples by its old mean values
     * because zero centering is only to improve efficiency of algorithm not to transform original data
     * bias is moved by that means as well.
     * that means zero centering is not noticeable outside this function.
     * @param samples - list of all samples
     * @param epochs - number of iteration of gradient descent
     * @param eta - step size of updating the weights
     * @return table [wx, wy, bias]
     */
    public static double[] fitLogisticRegressionModel(ArrayList<Sample> samples, int epochs, double eta) {
        ArrayList<Sample> trainingSamples = new ArrayList<>();

        for(Sample sample: samples) {
            if(sample.category() == 1 || sample.category() == 2) {
                trainingSamples.add(sample);
            }
        }

        double meanX = mean(getXs(trainingSamples));
        double meanY = mean(getYs(trainingSamples));
        zeroCenterSamples(trainingSamples);

        // should be random values but with constant initial values, line looks more stable
        double wx = 0.1;
        double wy = -0.1;
        double bias = 0.2;

        for(int i = 0; i < epochs; i++) {
            Collections.shuffle(trainingSamples);
            for(Sample sample: trainingSamples) {
                double input = wx*sample.getX() + wy*sample.getY() + bias;
                double output = sigmoid(input);
                double error = eta*(sample.category()-1 - output)*sigmoidPrime(input);

                wx += error*sample.getX();
                wy += error*sample.getY();
                bias += error;
            }
        }
        moveAllSamples(trainingSamples, meanX, meanY);
        return new double[] {wx, wy, bias - (wx*meanX + wy*meanY)};
    }

    /**
     * rounds a real number to the specified precision.
     * @param value - value to be rounded
     * @param precision - how many digits after the decimal point
     * @return rounded value to the given precision
     */
    public static double round(double value, int precision) {
        double factor = pow(10, precision);
        return floor(value*factor)/factor;
    }


    public static void voting(Sample neutral, List<Sample> neighbours, int k) {
        Integer[] values = new Integer[]{0,0,0,0,0,0};

        for (int i = 0; i < Integer.min(k, neighbours.size()); i++) {
            Sample neighbour = neighbours.get(i);
            values[neighbour.category() - 1] += 1;
        }

        int predictedCategory = argmax(values)+1;

        if(predictedCategory == 0) {
            voting(neutral, neighbours, k-1);
        } else {
            neutral.setPredictedColor(DrawUtils.sampleColors[predictedCategory]);
        }
    }

    public static ArrayList<KNNInterface> KNNAlgorithm(ArrayList<Sample> samples, int k) {
        ArrayList<KNNInterface> interfaces = new ArrayList<>();
        ArrayList<Sample> training = new ArrayList<>();
        ArrayList<Sample> neutrals = new ArrayList<>();

        for(Sample sample: samples) {
            if(sample.category() != 0) {
                training.add(sample);
            } else {
                neutrals.add(sample);
            }
        }

        for(Sample neutral: neutrals) {
            training.sort(new DistComparator(neutral));
            List<Sample> neighbours = training.subList(0, Integer.min(k, training.size()));

            if(neighbours.size() > 0) {
                voting(neutral, neighbours, k);
                interfaces.add(new KNNInterface(neutral, neighbours));
            }
        }
        return interfaces;
    }
}
