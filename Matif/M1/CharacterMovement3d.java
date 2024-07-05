import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class CharacterMovement3d extends JPanel implements ActionListener, KeyListener, MouseListener {
    private double[] position;
    private double speed;
    private double deltaTime;
    private Timer timer;
    private ArrayList<double[]> cities;
    private double[] targetPosition;
    private boolean movingToTarget;

    public CharacterMovement3d(double[] startPosition, double speed, double deltaTime) {
        this.position = startPosition;
        this.speed = speed;
        this.deltaTime = deltaTime;
        this.timer = new Timer((int) (deltaTime * 1000), this);
        this.cities = new ArrayList<>();
        this.targetPosition = null;
        this.movingToTarget = false;
        this.timer.start();
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                generateRandomCities(5);
            }
        });
    }

    private void generateRandomCities(int numberOfCities) {
        Random rand = new Random();
        for (int i = 0; i < numberOfCities; i++) {
            double x = rand.nextInt(getWidth() - 40) + 20;
            double y = rand.nextInt(getHeight() - 40) + 20;
            cities.add(new double[] { x, y });
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Character Movement");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        double[] startPosition = { 100, 100 };
        double speed = 100.0;
        double deltaTime = 0.05;

        CharacterMovement3d movementPanel = new CharacterMovement3d(startPosition, speed, deltaTime);
        frame.add(movementPanel);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval((int) position[0], (int) position[1], 20, 20);

        g.setColor(Color.BLUE);
        for (double[] city : cities) {
            g.fillOval((int) city[0], (int) city[1], 10, 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (movingToTarget && targetPosition != null) {
            double deltaX = targetPosition[0] - position[0];
            double deltaY = targetPosition[1] - position[1];
            double distance = VectorUtils.magnitude(new double[] { deltaX, deltaY });
            double[] direction = new double[] { deltaX / distance, deltaY / distance };
            double stepDistance = speed * deltaTime;
            double[] step = VectorUtils.multiply(direction, stepDistance);

            if (distance > stepDistance) {
                position = VectorUtils.add(position, step);
                System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
                repaint();
            } else {
                position[0] = targetPosition[0];
                position[1] = targetPosition[1];
                System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
                repaint();
                movingToTarget = false;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        double stepDistance = speed * deltaTime;
        if (key == KeyEvent.VK_UP) {
            position[1] -= stepDistance;
        } else if (key == KeyEvent.VK_DOWN) {
            position[1] += stepDistance;
        } else if (key == KeyEvent.VK_LEFT) {
            position[0] -= stepDistance;
        } else if (key == KeyEvent.VK_RIGHT) {
            position[0] += stepDistance;
        }
        System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (double[] city : cities) {
            double cityX = city[0];
            double cityY = city[1];
            if (e.getX() >= cityX && e.getX() <= cityX + 10 && e.getY() >= cityY && e.getY() <= cityY + 10) {
                targetPosition = new double[] { cityX, cityY };
                movingToTarget = true;
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static class VectorUtils {
        public static double[] add(double[] v1, double[] v2) {
            return new double[] { v1[0] + v2[0], v1[1] + v2[1] };
        }

        public static double[] multiply(double[] v, double scalar) {
            return new double[] { v[0] * scalar, v[1] * scalar };
        }

        public static double magnitude(double[] v) {
            return Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        }
    }
}
