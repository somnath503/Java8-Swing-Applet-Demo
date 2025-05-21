import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WhiteboardApplet extends Applet implements MouseListener, MouseMotionListener, ActionListener {

    private Image canvas;
    private Graphics2D g2;
    private int prevX, prevY;
    private Color selectedColor = Color.BLACK;
    private int brushSize = 5, eraserSize = 10;
    private boolean eraserMode = false;
    private float[] dashPattern = null;

    private Button colorBtn, eraserBtn, clearBtn;
    private Choice brushSizeChoice, eraserSizeChoice, lineStyleChoice;
    private Panel toolPanel;

    public void init() {
        setLayout(new BorderLayout());
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setSize(getPreferredSize());

        canvas = createImage(getWidth(), getHeight());
        g2 = (Graphics2D) canvas.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clearCanvas();

        toolPanel = new Panel();
        toolPanel.setLayout(new GridLayout(6, 1));
        toolPanel.setBackground(Color.LIGHT_GRAY);
        toolPanel.setPreferredSize(new Dimension(160, getHeight()));

        colorBtn = new Button("Select Color");
        colorBtn.addActionListener(this);

        eraserBtn = new Button("Eraser");
        eraserBtn.addActionListener(this);

        clearBtn = new Button("Clear");
        clearBtn.addActionListener(this);

        Panel brushPanel = new Panel(new BorderLayout());
        Label brushLabel = new Label("Brush Size", Label.CENTER);
        brushPanel.add(brushLabel, BorderLayout.NORTH);

        brushSizeChoice = new Choice();
        for (int i = 2; i <= 20; i += 2) brushSizeChoice.add(String.valueOf(i));
        brushSizeChoice.addItemListener(e -> {
            brushSize = Integer.parseInt(brushSizeChoice.getSelectedItem());
        });
        brushPanel.add(brushSizeChoice, BorderLayout.CENTER);

        Panel eraserPanel = new Panel(new BorderLayout());
        Label eraserLabel = new Label("Eraser Size", Label.CENTER);
        eraserPanel.add(eraserLabel, BorderLayout.NORTH);

        eraserSizeChoice = new Choice();
        for (int i = 5; i <= 40; i += 5) eraserSizeChoice.add(String.valueOf(i));
        eraserSizeChoice.addItemListener(e -> eraserSize = Integer.parseInt(eraserSizeChoice.getSelectedItem()));
        eraserPanel.add(eraserSizeChoice, BorderLayout.CENTER);

        Panel linePanel = new Panel(new BorderLayout());
        Label lineLabel = new Label("Line Style", Label.CENTER);
        linePanel.add(lineLabel, BorderLayout.NORTH);

        lineStyleChoice = new Choice();
        lineStyleChoice.add("Solid");
        lineStyleChoice.add("Dashed");
        lineStyleChoice.add("Dotted");
        lineStyleChoice.addItemListener(e -> updateStroke());
        linePanel.add(lineStyleChoice, BorderLayout.CENTER);

        toolPanel.add(colorBtn);
        toolPanel.add(eraserBtn);
        toolPanel.add(clearBtn);
        toolPanel.add(brushPanel);
        toolPanel.add(eraserPanel);
        toolPanel.add(linePanel);

        add(toolPanel, BorderLayout.WEST);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void clearCanvas() {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(selectedColor);
        repaint();
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == colorBtn) {
            Color newColor = JColorChooser.showDialog(null, "Choose Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                eraserMode = false;
                updateStroke();
            }
        } else if (e.getSource() == eraserBtn) {
            eraserMode = true;
        } else if (e.getSource() == clearBtn) {
            clearCanvas();
        }
    }

    public void mousePressed(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
        updateStroke(); // Ensure stroke is updated before drawing starts
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        if (eraserMode) {
            g2.setColor(Color.WHITE);
            g2.fillOval(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize);
        } else {
            updateStroke();  // Apply stroke setting before drawing
            g2.setColor(selectedColor);
            g2.drawLine(prevX, prevY, x, y);
        }
        prevX = x;
        prevY = y;
        repaint(x - brushSize, y - brushSize, brushSize * 2, brushSize * 2);
    }
    
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    private void updateStroke() {
        String selectedStyle = lineStyleChoice.getSelectedItem();
        if (g2 != null) {
            if ("Dashed".equals(selectedStyle)) {
                float[] dashPattern = {10f, 10f};
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dashPattern, 0f));
            } else if ("Dotted".equals(selectedStyle)) {
                float[] dashPattern = {2f, 5f};
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dashPattern, 0f));
            } else { // Solid
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
        }
    }
    
}