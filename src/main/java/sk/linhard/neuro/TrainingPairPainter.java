package sk.linhard.neuro;

import java.awt.Color;
import java.awt.Graphics;

public class TrainingPairPainter {

    private final int numRows;
    private final int numCols;
    private final int boxSize;
    private final int boxSpacing;
    private static final Color BORDER_COL = Color.GREEN;

    private final int startInputX;
    private final int startInputY;

    private final int startOutputX;
    private final int gridsize;
    private final int height;
    private final int width;

    public TrainingPairPainter(int numRows, int numCols, int boxSize, int boxSpacing, int startInputX, int startInputY) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.boxSize = boxSize;
        this.boxSpacing = boxSpacing;
        this.startInputX = startInputX;
        this.startInputY = startInputY;
        ;
        gridsize = boxSize + boxSpacing;
        startOutputX = gridsize * (numCols + 3) + boxSpacing - 1 + startInputX;

        height = gridsize * numRows + boxSpacing;
        width = gridsize * numCols + boxSpacing;
    }

    public void paint(Graphics g, TrainingPair currentPair) {
        drawMainLimiter(g);
        if (currentPair != null) {
            paintVector(g, currentPair.getIn(), startInputX + 1, startInputY + 1);
            paintVector(g, currentPair.getOut(), startOutputX + 1, startInputY + 1);
        }
        paintArrow(g);
    }

    private void paintArrow(Graphics g) {
        int xArrow = gridsize * (numCols + 1) + startInputX;
        int yArros = startInputY + (height / 2);
        g.setColor(BORDER_COL);
        int halfgr = gridsize / 2;
        g.drawLine(xArrow, yArros, xArrow + gridsize, yArros);
        g.drawLine(xArrow + gridsize, yArros, xArrow + halfgr, yArros - halfgr);
        g.drawLine(xArrow + gridsize, yArros, xArrow + halfgr, yArros + halfgr);
    }

    private void drawMainLimiter(Graphics g) {
        g.setColor(BORDER_COL);
        int width = this.width + 1;
        int height = this.height + 1;
        g.drawLine(startInputX, startInputY, width + startInputX, startInputY);
        g.drawLine(startInputX, height + startInputY, width + startInputX, height + startInputY);
        g.drawLine(startInputX, startInputY, startInputX, height + startInputY);
        g.drawLine(width + startInputX, startInputY, width + startInputX, height + startInputY);

        g.drawLine(startOutputX, startInputY, width + startOutputX, startInputY);
        g.drawLine(startOutputX, height + startInputY, width + startOutputX, height + startInputY);
        g.drawLine(startOutputX, startInputY, startOutputX, height + startInputY);
        g.drawLine(width + startOutputX, startInputY, width + startOutputX, height + startInputY);
    }

    private void paintVector(Graphics g, NeuroVector vector, int x, int y) {
        int gridsize = boxSize + boxSpacing;
        Color[] colors = toColors(vector);
        int xoff = boxSpacing + x;
        int yoff = boxSpacing + y;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                g.setColor(colors[row * numCols + col]);
                g.fillRect(xoff + row * gridsize, yoff + col * gridsize, boxSize, boxSize);
            }
        }
    }

    private Color[] toColors(NeuroVector vector) {
        Color[] c = new Color[25];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                float dataval = vector.data[row * numCols + col];
                c[row * numCols + col] = new Color(dataval, dataval, dataval);
            }
        }
        return c;
    }
}
