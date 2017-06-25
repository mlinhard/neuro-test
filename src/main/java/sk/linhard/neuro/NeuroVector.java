package sk.linhard.neuro;

import Jama.Matrix;

public class NeuroVector implements Cloneable {

    float[] data;

    public NeuroVector(float[] data) {
        super();
        this.data = data;
    }

    public NeuroVector(int w, int h, float fillValue) {
        data = new float[w * h];
        for (int i = 0; i < data.length; i++) {
            data[i] = fillValue;
        }
    }

    @Override
    public NeuroVector clone() {
        float[] copydata = new float[data.length];
        System.arraycopy(data, 0, copydata, 0, data.length);
        return new NeuroVector(copydata);
    }

    public double[] toDoubleVector() {
        double[] v = new double[data.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = data[i];
        }
        return v;
    }

    public static Matrix toMatrix(NeuroVector... neuroVectors) {
        double[][] m = new double[neuroVectors.length][];
        for (int i = 0; i < m.length; i++) {
            m[i] = neuroVectors[i].toDoubleVector();
        }
        return Matrix.constructWithCopy(m);
    }
}