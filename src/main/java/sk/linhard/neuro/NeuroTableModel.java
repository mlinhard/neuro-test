package sk.linhard.neuro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.common.io.Files;

public class NeuroTableModel extends AbstractTableModel {

    private List<TrainingPair> items;

    public NeuroTableModel() {
        items = new ArrayList<>();
    }

    public NeuroTableModel(File loadFrom) {
        items = deserialize(loadFrom);
    }

    private List<TrainingPair> deserialize(File loadFrom) {
        try (FileInputStream fis = new FileInputStream(loadFrom); ObjectInputStream oin = new ObjectInputStream(fis)) {
            int numItems = oin.readInt();
            if (numItems == 0) {
                return new ArrayList<>();
            } else {
                List<TrainingPair> r = new ArrayList<>(numItems);
                TrainingPair pair = (TrainingPair) oin.readObject();
                r.add(pair);
                return r;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? rowIndex : items.get(rowIndex);
    }

    public void add(TrainingPair item) {
        int rowindex = items.size();
        items.add(item);
        fireTableRowsInserted(rowindex, rowindex);
    }

    public void save(File file) {
        try {
            Files.write(serialize(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream dos = new ObjectOutputStream(bos);) {
            dos.writeInt(items.size());
            for (TrainingPair p : items) {
                dos.writeObject(p);
            }
            dos.flush();
            return bos.toByteArray();
        }
    }
}
