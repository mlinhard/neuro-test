package sk.linhard.neuro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Jama.Matrix;

public class NeuroTest extends JFrame implements ActionListener, TextListener, ListSelectionListener, OutputComputer {

    private final static File DATAFILE = new File("training.dat");

    private static final Logger log = LoggerFactory.getLogger(NeuroTest.class);
    private JTable t;
    private NeuroTableModel tableModel;
    private NeuroInputPainter painter;
    private NeuroInputPainter painterExec;
    private JButton bAdd;
    private JButton bCopy;
    private JButton bDel;
    private JButton bSave;
    private JButton b0;
    private JButton b1;
    private TextField valInput;

    public NeuroTest() {

        initUI();
    }

    private static final TrainingPair DEF_PAIR = new TrainingPair(4, 4, 0.0f, 0.0f);

    private static class TrainingPairCell extends JPanel {

        private TrainingPair pair;
        private static TrainingPairPainter painter = new TrainingPairPainter(4, 4, 5, 1, 0, 0);

        public TrainingPairCell(TrainingPair pair) {
            super();
            this.pair = pair;
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            painter.paint(g, pair);
        }

    }

    private class TrainingPairRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 1) {
                return new TrainingPairCell((TrainingPair) value);
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }

    }

    private void initUI() {

        setTitle("Neural network test");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        if (DATAFILE.exists()) {
            tableModel = new NeuroTableModel(DATAFILE);
        } else {
            tableModel = new NeuroTableModel();
            tableModel.add(DEF_PAIR.clone());
        }
        t = new JTable(tableModel) {

            @Override
            public javax.swing.table.TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 1) {
                    return new TrainingPairRenderer();
                } else {
                    return super.getCellRenderer(row, column);
                }
            };
        };
        t.setRowHeight(28);
        t.setDefaultRenderer(TrainingPair.class, new TrainingPairRenderer());
        ListSelectionModel selmodel = t.getSelectionModel();
        selmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selmodel.addListSelectionListener(this);

        JPanel mainPanel = new JPanel();
        GridLayout gl = new GridLayout(3, 1);
        mainPanel.setLayout(gl);
        JScrollPane tablePane = new JScrollPane(t);
        bAdd = new JButton("Add");
        bCopy = new JButton("Copy");
        bDel = new JButton("Del");
        bSave = new JButton("Save");
        b0 = new JButton("0");
        b1 = new JButton("1");
        painter = new NeuroInputPainter(null);
        painterExec = new NeuroInputPainter(this);
        painterExec.setCurrentPair(new TrainingPair(4, 4, 0f, 0f));

        valInput = new TextField("1.00");
        valInput.addTextListener(painter);
        valInput.addTextListener(painterExec);
        valInput.setSize(140, 50);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new FlowLayout());
        buttonpanel.add(bAdd);
        buttonpanel.add(bCopy);
        buttonpanel.add(bDel);
        buttonpanel.add(bSave);
        buttonpanel.add(b0);
        buttonpanel.add(b1);
        buttonpanel.add(valInput);

        bAdd.addActionListener(this);
        bCopy.addActionListener(this);
        bDel.addActionListener(this);
        bSave.addActionListener(this);
        b0.addActionListener(this);
        b1.addActionListener(this);
        BorderLayout bl = new BorderLayout();
        setLayout(bl);
        add(buttonpanel, BorderLayout.PAGE_START);
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(tablePane);
        mainPanel.add(painter);
        mainPanel.add(painterExec);

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            NeuroTest ex = new NeuroTest();
            ex.setVisible(true);
        });
    }

    @Override
    public NeuroVector compute(NeuroVector input) {
        int n = tableModel.getRowCount();
        double[][] x = new double[n - 1][];
        double[][] y = new double[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            TrainingPair pair = tableModel.get(i);
            x[i] = pair.getIn().toDoubleVector();
            y[i] = pair.getOut().toDoubleVector();
        }

        Matrix X = Matrix.constructWithCopy(x).transpose();
        Matrix Y = Matrix.constructWithCopy(y).transpose();

        try {
            Matrix Xtr = X.transpose();
            Matrix Xplus = null;
            if (X.getRowDimension() < Y.getColumnDimension()) {
                Xplus = Xtr.times(X.times(Xtr).inverse());
            } else if (X.getRowDimension() > Y.getColumnDimension()) {
                Xplus = Xtr.times(X).inverse().times(Xtr);
            } else {
                Xplus = X.inverse();
            }

            Matrix W = Y.times(Xplus);
            Matrix in = Matrix.constructWithCopy(new double[][] { input.toDoubleVector() }).transpose();
            Matrix out = W.times(in);

            float[] v = new float[out.getRowDimension()];
            for (int i = 0; i < v.length; i++) {
                v[i] = (float) out.get(i, 0);
                if (v[i] < 0) {
                    v[i] = 0;
                }
                if (v[i] > 1) {
                    v[i] = 1;
                }
            }

            X.times(Xplus.times(X)).print(4, 2);

            return new NeuroVector(v);
        } catch (RuntimeException e) {
            log.debug("Couldn't compute: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bAdd) {
            log.debug("Adding new training pair");
            tableModel.add(DEF_PAIR.clone());
        } else if (e.getSource() == bCopy) {
            int selrow = t.getSelectedRow();
            if (selrow != -1) {
                tableModel.add(tableModel.get(selrow).clone());
            }
        } else if (e.getSource() == bDel) {
            int selrow = t.getSelectedRow();
            tableModel.delete(selrow);
        } else if (e.getSource() == b0) {
            valInput.setText("0");
        } else if (e.getSource() == b1) {
            valInput.setText("1");
        } else if (e.getSource() == bSave) {
            try {
                tableModel.save(DATAFILE);
            } catch (Exception ex) {
                log.error("Error while saving training data", ex);
            }
        } else {
            log.debug("Unknown action source");
        }

    }

    @Override
    public void textValueChanged(TextEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = t.getSelectedRow();
        if (!e.getValueIsAdjusting() && selectedRow != -1) {
            TrainingPair pair = (TrainingPair) tableModel.getValueAt(selectedRow, 1);
            log.debug("training pair {} selected: {}\n{}", Integer.toHexString(Objects.hashCode(pair)), selectedRow, pair);
            painter.setCurrentPair(pair);
            painter.repaint();
        }
    }

}
