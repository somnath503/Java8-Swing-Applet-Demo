
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class PaintApplication extends Applet implements ActionListener, MouseListener , MouseMotionListener {
    
    //instace variable 
    
    private Image canvas;
    private Graphics2D g2;
    private int prevX, prevY;
    private Color selectedColor = Color.black;
    private int brushSize = 5, erasersize = 10;
    private boolean eraserMode = false;
    private float [] dashPattern = null;

    // UI components 

    private Button colorBtn,eraserBtn, clearBtn;
    private Choice brushsizeChoice; 
    private Choice erasersizeChoice;
    private Choice  linestyleChoice;
    private Panel toolPanel;

    
    public void init(){
        setLayout( new BorderLayout()); // which allows to placing components 
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize()); // its set in this way that full screen used by applet
        setSize(getPreferredSize()); // applied to preffered screensize 

        canvas = createImage(getWidth(),getHeight()); // acts as a offscreen image for drawing surface
        g2 = (Graphics2D) canvas.getGraphics(); // get graphic object for drawing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // this helps to smooth drawing 
        clearCanvas(); // clearing the surface for further drawing 


        //create toolbar panel

        toolPanel = new Panel(); // create a panel that holds tool bar and control 
        toolPanel.setLayout(new GridLayout(6,1)); // there 1 column and 6 rows in panle section
        toolPanel.setBackground(Color.orange);
        toolPanel.setPreferredSize(new Dimension(160,getWidth())); // for full screen

        // create button and add event listeners

        colorBtn = new Button("select color");
        colorBtn.addActionListener(this); // adds a click even to open color picker

        //Eraser button

        eraserBtn = new Button("Eraser");
        eraserBtn.addActionListener(this); // event listner dectes when eraser mode is enable

        //clear canvas button

        clearBtn = new Button("clear");
        clearBtn.addActionListener(this);

        //brush size selection from panel 
        //panel like a container that can holds a lots of ui tools like button


         Panel brushPanel = new Panel(new BorderLayout());
         Label brushLabel = new Label("Brush Size", Label.CENTER);
        brushPanel.add(brushLabel, BorderLayout.NORTH);

        //adds the brush size on pannel north side

        brushsizeChoice = new Choice();
        for (int i = 2; i <= 20; i += 2) brushsizeChoice.add(String.valueOf(i));
        brushsizeChoice.addItemListener(e -> {
        brushSize = Integer.parseInt(brushsizeChoice.getSelectedItem());
        updateStroke();
        });
        brushPanel.add(brushsizeChoice, BorderLayout.CENTER);


        //Eraser Size Selection

        Panel eraserPanel = new Panel(new BorderLayout());
        Label eraserLabel = new Label("Eraser Size", Label.CENTER);
        eraserPanel.add(eraserLabel, BorderLayout.NORTH);

        erasersizeChoice = new Choice();
         for (int i = 5; i <= 40; i += 5) erasersizeChoice.add(String.valueOf(i));
        erasersizeChoice.addItemListener(e -> erasersize = Integer.parseInt(erasersizeChoice.getSelectedItem()));
        eraserPanel.add(erasersizeChoice, BorderLayout.CENTER);


        //line style selection


        Panel linePanel = new Panel(new BorderLayout());
        Label lineLabel = new Label("Line Style", Label.CENTER);
        linePanel.add(lineLabel, BorderLayout.NORTH);

        linestyleChoice = new Choice();
        linestyleChoice.add("Solid");
        linestyleChoice.add("Dashed");
        linestyleChoice.add("Dotted");
        linestyleChoice.addItemListener(e -> updateStroke());
        linePanel.add(linestyleChoice, BorderLayout.CENTER);
        
        //add component to toolpanel

        toolPanel.add(colorBtn);
        toolPanel.add(eraserBtn);
        toolPanel.add(clearBtn);
        toolPanel.add(brushPanel);
        toolPanel.add(eraserPanel);
        toolPanel.add(linePanel);

        //add toolpanel to applet west part 

        add(toolPanel, BorderLayout.WEST);

        // add mouse action
        addMouseListener(this); // when mouse click on canvas 
        addMouseMotionListener(this); // when mouse is dragged , allow to user to draw


    }


    //work later

    private void clearCanvas() {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(selectedColor);
        repaint();
    }

    // Prevent Flickering
    public void update(Graphics g) {
        paint(g);
    }

    private void updateStroke() {
        String selectedStyle = linestyleChoice.getSelectedItem();
    
        float[] dashPattern = null; // Default to solid
        if (selectedStyle.equals("Dashed")) {
            dashPattern = new float[]{10f, 10f}; // Long dashes
        } else if (selectedStyle.equals("Dotted")) {
            dashPattern = new float[]{2f, 5f}; // Dots
        }
    
        if (g2 != null) {
            if (dashPattern == null) {
                // Solid Line
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            } else {
                // Dashed or Dotted Line
                g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        10f, dashPattern, 0f));
            }
        }
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
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        if (eraserMode) {
            g2.setColor(Color.WHITE);
            g2.fillOval(x - erasersize / 2, y - erasersize / 2, erasersize, erasersize);
        } else {
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

}