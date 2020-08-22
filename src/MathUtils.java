import java.util.*;

import static java.lang.Double.*;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class MathUtils {
    static double clamp(double val, double min, double max) {
        return min(max(val,min),max);
    }

    static double dist(double x1, double y1, double x2, double y2) {
        return sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    static double mean(ArrayList<Double> numbers) {
        double sum = 0;
        for(Double num: numbers) sum += num;
        return sum / numbers.size();
    }

    static double variance(ArrayList<Double> numbers) {
        double sum = 0;
        double mean = mean(numbers);

        for(Double num: numbers) sum += (num-mean)*(num-mean);
        return sum/numbers.size();
    }

    static double stdDev(ArrayList<Double> numbers) {
        return sqrt(variance(numbers));
    }

    static double dotProd(List<Double> xs, List<Double> ys) {
        double sum = 0;
        for(int i = 0; i < xs.size(); i++) {
            sum += xs.get(i)*ys.get(i);
        }
        return sum;
    }

    static ArrayList<Double> getXs(ArrayList<Sample> samples) {
        ArrayList<Double> xs = new ArrayList<>();

        for(Sample s: samples) {
            xs.add(s.getX());
        }
        return xs;
    }

    static ArrayList<Double> getYs(ArrayList<Sample> samples) {
        ArrayList<Double> ys = new ArrayList<>();

        for(Sample s: samples) {
            ys.add(s.getY());
        }
        return ys;
    }

    static void zeroCenter(ArrayList<Double> xs) {
        double mean = mean(xs);

        for(int i = 0; i < xs.size(); i++) {
            xs.set(i, xs.get(i) - mean);
        }
    }

    static void zeroCenterSamples(ArrayList<Sample> samples) {
        double meanX = mean(getXs(samples));
        double meanY = mean(getYs(samples));

        moveAllSamples(samples, -meanX, -meanY);
    }

    static void moveAllSamples(ArrayList<Sample> samples, double dx, double dy) {
        for(int i = 0; i < samples.size(); i++) {
            samples.get(i).move(dx, dy);
        }
    }

    static double covariance(ArrayList<Sample> samples) {
        if(samples.size() == 0) { return 0; }

        ArrayList<Double> xs = getXs(samples);
        ArrayList<Double> ys = getYs(samples);

        zeroCenter(xs);
        zeroCenter(ys);

        return dotProd(xs, ys) / samples.size();
    }

    static Matrix2x2 covarianceMatrix(ArrayList<Sample> samples) {
        /**
         * computes covariance matrix for given data in R^2
         * returns [Var(X), Cov(X,Y), Var(Y)]
         * since Cov(X,Y) = Cov(Y,X) then covariance matrix will always be symmetric
         * so we can return only values on the diagonal and above
         * if samples list is empty returns [0,0,0] to avoid dividing by 0
         */
        if(samples.size() == 0) { return new Matrix2x2(0,0,0,0); }

        ArrayList<Double> output = new ArrayList<>();
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

    static double correlation(ArrayList<Sample> samples) {
        return covariance(samples)/(stdDev(getXs(samples))*stdDev(getYs(samples)));
    }

    static ArrayList<Double> linearRegressionCoefficients(ArrayList<Sample> samples) {
        ArrayList<Double> output = new ArrayList<>();
        ArrayList<Double> xs = getXs(samples);
        ArrayList<Double> ys = getYs(samples);

        double rho = correlation(samples);
        double devX = stdDev(xs);
        double devY = stdDev(ys);

        double a = rho*devY/devX;

        output.add(a);
        output.add(mean(ys) - a*mean(xs));
        return output;
    }

    static ArrayList<Double> symmetricMatrixEigenvaluesAndVectors(double a, double b, double c) {
        /**
         * computes eigenvectors and eigenvalues of symmetric matrix | a b |
         *                                                           | b c |
         * returns [ V1x, V1y, V2x, V2y, lambda1, lambda 2 ]
         * where V1 and V2 are eigenvectors and lambda1 and lambda2 are corresponding eigenvalues
         * V1x is x-value of V1 etc ...
         * if a==b==c==0 returns [0,0,0,0,0,0] to avoid NaN values
         * lengths of eigenvectors are equal to corresponding eigenvalues (not 1!) for better visualization
         *
         * Please note that since given matrix is symmetric, eigenvectors will always be orthogonal to each other
         **/

        if(a == 0 && b == 0 && c == 0) { return new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0)); }

        ArrayList<Double> output = new ArrayList<>();

        // first, compute eigenvalues using characteristic equation of given matrix
        double delta = sqrt((a+c)*(a+c) - 4*(a*c - b*b));
        double lambda_1 = (a+c+delta)/2;
        double lambda_2 = (a+c-delta)/2;

        // compute lengths of eigenvectors where x value = 1
        double len_x1 = sqrt(1+((lambda_1-a)/b)*((lambda_1-a)/b));
        double len_x2 = sqrt(1+((lambda_2-a)/b)*((lambda_2-a)/b));

        // compute eigenvectors divided by their lengths and multiply by lambda values to achieve lengths = lambda
        output.add(lambda_1/len_x1);
        output.add(lambda_1*(lambda_1-a)/(b*len_x1));
        output.add(lambda_2/len_x2);
        output.add(lambda_2*(lambda_2-a)/(b*len_x2));

        // add eigenvalues to output
        output.add(lambda_1);
        output.add(lambda_2);

        return output;
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + exp(-x));
    }

    public static double sigmoidPrime(double x) {
        return sigmoid(x)*(1 - sigmoid(x));
    }

    public static double[] LogisticRegressionParameters(ArrayList<Sample> samples, int epochs, double eta) {
        // should be random values but with constant initial values, line looks more stable

        ArrayList<Sample> trainingSamples = new ArrayList<>();

        for(Sample sample: samples) {
            if(sample.category() == 1 || sample.category() == 2) {
                trainingSamples.add(sample);
            }
        }

        double meanX = mean(getXs(trainingSamples));
        double meanY = mean(getYs(trainingSamples));
        zeroCenterSamples(trainingSamples);

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

    public static double round(double value, int i) {
        double factor = pow(10, i);
        return floor(value*factor)/factor;
    }
}
