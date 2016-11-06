package sk.linhard.neuro;

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
}