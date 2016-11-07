package sk.linhard.neuro;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuroInputPainter extends JPanel implements MouseListener, MouseMotionListener, TextListener {

    private static final Logger log = LoggerFactory.getLogger(NeuroInputPainter.class);

    private float currentColor = 1.0f;
    private final int numRows;
    private final int numCols;
    private final int boxSize;
    private final int boxSpacing;

    private final int startInputX;
    private final int startInputY;
    private final int endInputY;
    private final int endInputX;

    private final int startOutputX;
    private final int endOutputX;
    private final int gridsize;
    private final int height;
    private final int width;
    private final boolean displayClicked;

    private Point clicked;
    private Dimension clickedDim;

    private TrainingPair currentPair;

    private final TrainingPairPainter pairPainter;

    public NeuroInputPainter() {
        displayClicked = false;
        startInputX = 30;
        startInputY = 30;
        numRows = 4;
        numCols = 4;
        boxSpacing = 4;
        boxSize = 24;
        gridsize = boxSize + boxSpacing;
        startOutputX = gridsize * (numCols + 3) + boxSpacing - 1 + startInputX;

        height = gridsize * numRows + boxSpacing;
        width = gridsize * numCols + boxSpacing;
        endInputY = startInputY + height;
        endInputX = startInputX + width;
        endOutputX = startOutputX + width;

        pairPainter = new TrainingPairPainter(numRows, numCols, boxSize, boxSpacing, startInputX, startInputY);
        setBackground(Color.BLACK);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setCurrentPair(TrainingPair currentPair) {
        this.currentPair = currentPair;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentPair != null) {
            pairPainter.paint(g, currentPair);
            if (displayClicked && clicked != null) {
                g.setColor(Color.red);
                g.fillRect(clicked.x, clicked.y, clickedDim.width, clickedDim.height);
            }

            g.setColor(new Color(currentColor, currentColor, currentColor));
            g.fillRect(endOutputX + 2 * gridsize, startInputY, 50, 50);
        }
    }

    private void vectorClicked(int row, int col, boolean input) {
        if (currentPair != null) {
            int idx = row * numCols + col;
            NeuroVector v = input ? currentPair.getIn() : currentPair.getOut();
            if (idx < v.data.length) {
                v.data[idx] = currentColor;
            }
        }
    }

    private void mouse(int mx, int my) {
        if (my >= startInputY && my <= endInputY) {
            if (mx >= startInputX && mx <= endInputX) {
                int basex = mx - startInputX;
                int basey = my - startInputY;

                int row = basex / gridsize;
                int col = basey / gridsize;
                int rowmod = basex % gridsize;
                int colmod = basey % gridsize;

                if (rowmod <= boxSpacing || colmod <= boxSpacing || row >= numRows || col >= numCols) {
                    clicked = null;
                    clickedDim = null;
                    log.trace("out of box click");
                } else {
                    clicked = new Point(startInputX + gridsize * row + boxSpacing + 1,
                            startInputY + gridsize * col + boxSpacing + 1);
                    clickedDim = new Dimension(boxSize, boxSize);
                    vectorClicked(row, col, true);
                }
            } else if (mx >= startOutputX && mx <= endOutputX) {
                int basex = mx - startOutputX;
                int basey = my - startInputY;

                int row = basex / gridsize;
                int col = basey / gridsize;
                int rowmod = basex % gridsize;
                int colmod = basey % gridsize;

                if (rowmod <= boxSpacing || colmod <= boxSpacing || row >= numRows || col >= numCols) {
                    clicked = null;
                    clickedDim = null;
                    log.trace("out of box click");
                } else {
                    clicked = new Point(startOutputX + gridsize * row + boxSpacing + 1,
                            startInputY + gridsize * col + boxSpacing + 1);
                    clickedDim = new Dimension(boxSize, boxSize);

                    vectorClicked(row, col, false);
                }
            } else {
                clicked = null;
                clickedDim = null;
                log.trace("X out of bounds");
            }
        } else {
            clicked = null;
            clickedDim = null;
            log.trace("Y out of bounds");
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void textValueChanged(TextEvent e) {
        TextField f = (TextField) e.getSource();
        String text = f.getText();
        try {
            currentColor = Float.parseFloat(text);
            if (currentColor > 1.0f) {
                currentColor = 1.0f;
            } else if (currentColor < 0.0f) {
                currentColor = 0.0f;
            }
            repaint();
        } catch (Exception ex) {
            log.trace("Error " + ex.getMessage());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouse(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
