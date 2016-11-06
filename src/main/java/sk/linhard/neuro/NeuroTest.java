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

public class NeuroTest extends JFrame implements ActionListener, TextListener, ListSelectionListener {

    private final static File DATAFILE = new File("training.dat");

    private static final Logger log = LoggerFactory.getLogger(NeuroTest.class);
    private JTable t;
    private NeuroTableModel tableModel;
    private NeuroInputPainter painter;
    private JButton bAdd;
    private JButton bSave;

    public NeuroTest() {

        initUI();
    }

    private static final TrainingPair DEF_PAIR = new TrainingPair(4, 4, 0.3f, 0.7f);

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
        GridLayout gl = new GridLayout(2, 1);
        mainPanel.setLayout(gl);
        JScrollPane tablePane = new JScrollPane(t);
        bAdd = new JButton("Add input");
        bSave = new JButton("Save");
        painter = new NeuroInputPainter();

        TextField valInput = new TextField("1.00");
        valInput.addTextListener(painter);
        valInput.setSize(140, 50);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new FlowLayout());
        buttonpanel.add(bAdd);
        buttonpanel.add(bSave);
        buttonpanel.add(valInput);

        bAdd.addActionListener(this);
        bSave.addActionListener(this);
        BorderLayout bl = new BorderLayout();
        setLayout(bl);
        add(buttonpanel, BorderLayout.PAGE_START);
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(tablePane);
        mainPanel.add(painter);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            NeuroTest ex = new NeuroTest();
            ex.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bAdd) {
            log.debug("Adding new training pair");
            tableModel.add(DEF_PAIR.clone());
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
        TrainingPair pair = (TrainingPair) tableModel.getValueAt(e.getFirstIndex(), 1);
        log.debug("training pair selected:\n" + pair);
        painter.setCurrentPair(pair);
        painter.repaint();
    }

}
