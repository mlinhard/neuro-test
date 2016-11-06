package sk.linhard.neuro;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

public class TrainingPair implements Cloneable, Externalizable {

    private int rowsize;
    private NeuroVector in;
    private NeuroVector out;

    public TrainingPair() {
    }

    public TrainingPair(int w, int h, float inputVal, float outputVal) {
        in = new NeuroVector(w, h, inputVal);
        out = new NeuroVector(w, h, outputVal);
        rowsize = w;
    }

    private TrainingPair(int w, NeuroVector in, NeuroVector out) {
        this.in = in;
        this.out = out;
        rowsize = w;
    }

    public int getRowsize() {
        return rowsize;
    }

    public NeuroVector getIn() {
        return in;
    }

    public NeuroVector getOut() {
        return out;
    }

    @Override
    public TrainingPair clone() {
        return new TrainingPair(rowsize, in.clone(), out.clone());
    }

    @Override
    public String toString() {
        DecimalFormat fmt = new DecimalFormat("0.00");
        StringBuilder s = new StringBuilder();
        int numrows = in.data.length / rowsize;
        for (int row = 0; row < numrows; row++) {
            for (int col = 0; col < rowsize; col++) {
                float val = in.data[row * rowsize + col];
                s.append(StringUtils.leftPad(fmt.format(val), 10));
            }
            s.append("\t");
            for (int col = 0; col < rowsize; col++) {
                float val = out.data[row * rowsize + col];
                s.append(StringUtils.leftPad(fmt.format(val), 10));
            }
            s.append("\n");
        }

        return s.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(rowsize);
        out.writeObject(this.in.data);
        out.writeObject(this.out.data);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.rowsize = in.readInt();
        this.in = new NeuroVector((float[]) in.readObject());
        this.out = new NeuroVector((float[]) in.readObject());
    }

}
