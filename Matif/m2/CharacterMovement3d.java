import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class CharacterMovement3d extends JPanel implements ActionListener, KeyListener, MouseListener {
    private double[] position;
    private double speed;
    private double deltaTime;
    private Timer timer;
    private int[][] map; // Matriks untuk memetakan kollision
    private List<double[]> cities;
    private double[] targetPosition;
    private boolean movingToTarget;
    private double[] clickedPosition; // Menyimpan posisi yang diklik

    // Ukuran sel matriks (64x64 piksel per sel)
    private static final int CELL_SIZE = 64;
    private static final int COLLISION_OFFSET = -1; // Offset untuk collision

    public CharacterMovement3d(double[] startPosition, double speed, double deltaTime, int width, int height) {
        this.position = startPosition;
        this.speed = speed;
        this.deltaTime = deltaTime;
        this.timer = new Timer((int) (deltaTime * 1000), this);
        this.cities = new ArrayList<>();
        this.targetPosition = null;
        this.movingToTarget = false;
        this.clickedPosition = null; // Inisialisasi posisi yang diklik
        this.map = new int[width / CELL_SIZE][height / CELL_SIZE]; // Inisialisasi matriks dengan ukuran yang sesuai
        this.timer.start();
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                initializeGrid();
                generateRandomCities(10);
            }
        });

        initializeGrid();
        generateRandomCities(10);
    }

    private void initializeGrid() {
        // Inisialisasi matriks dengan nilai 0 (tidak ada collision)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 0;
            }
        }
    }

    private void generateRandomCities(int numberOfCities) {
        cities.clear();

        Random rand = new Random();
        for (int i = 0; i < numberOfCities; i++) {
            int x = rand.nextInt(map.length);
            int y = rand.nextInt(map[0].length);
            cities.add(new double[] { x * CELL_SIZE, y * CELL_SIZE });
            map[x][y] = 1; // Set nilai 1 di matriks untuk collision
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Gambar garis-garis grid
        g.setColor(Color.GRAY);
        for (int i = 0; i <= getWidth(); i += CELL_SIZE) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int j = 0; j <= getHeight(); j += CELL_SIZE) {
            g.drawLine(0, j, getWidth(), j);
        }

        // Visualisasi collision hit
        g.setColor(Color.RED);
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y] == 1) {
                    g.fillRect(x * CELL_SIZE + COLLISION_OFFSET, y * CELL_SIZE + COLLISION_OFFSET,
                            CELL_SIZE - COLLISION_OFFSET * 2, CELL_SIZE - COLLISION_OFFSET * 2);
                }
            }
        }

        // Gambar karakter
        g.setColor(Color.RED);
        g.fillOval((int) position[0], (int) position[1], 20, 20);

        // Gambar kota-kota
        g.setColor(Color.BLUE);
        for (double[] city : cities) {
            g.fillRect((int) city[0], (int) city[1], CELL_SIZE, CELL_SIZE); // Kotak kecil untuk mewakili kota
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (movingToTarget && targetPosition != null) {
            double deltaX = targetPosition[0] - position[0];
            double deltaY = targetPosition[1] - position[1];
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            double[] direction = { deltaX / distance, deltaY / distance };
            double stepDistance = speed * deltaTime;
            double[] step = { direction[0] * stepDistance, direction[1] * stepDistance };

            if (distance > stepDistance) {
                double[] nextPosition = { position[0] + step[0], position[1] + step[1] };
                if (!isColliding(nextPosition[0], nextPosition[1])) {
                    position = nextPosition;
                    System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
                    repaint();
                }
            } else {
                position[0] = targetPosition[0];
                position[1] = targetPosition[1];
                System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
                repaint();
                movingToTarget = false;
                targetPosition = null; // Reset target position
            }
        }
    }

    private boolean isColliding(double x, double y) {
        int cellX = (int) ((x + COLLISION_OFFSET) / CELL_SIZE);
        int cellY = (int) ((y + COLLISION_OFFSET) / CELL_SIZE);

        if (cellX < 0 || cellX >= map.length || cellY < 0 || cellY >= map[0].length) {
            return true; // Di luar batas matriks
        }

        return map[cellX][cellY] == 1;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        double stepDistance = speed * deltaTime;
        if (key == KeyEvent.VK_UP) {
            if (!isColliding(position[0], position[1] - stepDistance)) {
                position[1] -= stepDistance;
            }
        } else if (key == KeyEvent.VK_DOWN) {
            if (!isColliding(position[0], position[1] + stepDistance)) {
                position[1] += stepDistance;
            }
        } else if (key == KeyEvent.VK_LEFT) {
            if (!isColliding(position[0] - stepDistance, position[1])) {
                position[0] -= stepDistance;
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            if (!isColliding(position[0] + stepDistance, position[1])) {
                position[0] += stepDistance;
            }
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
        int clickX = e.getX();
        int clickY = e.getY();

        int cellX = clickX / CELL_SIZE;
        int cellY = clickY / CELL_SIZE;

        if (cellX >= 0 && cellX < map.length && cellY >= 0 && cellY < map[0].length && map[cellX][cellY] != 1) {
            List<Node> path = findPath(position,
                    new double[] { cellX * CELL_SIZE + CELL_SIZE / 2, cellY * CELL_SIZE + CELL_SIZE / 2 });

            if (path != null) {
                System.out.println("Found path to clicked position.");
                for (Node node : path) {
                    System.out.println("Node: (" + node.x + ", " + node.y + ")");
                }
                moveAlongPath(path);
            } else {
                System.out.println("Failed to find path to clicked position.");
            }
        } else {
            System.out.println("Clicked position is invalid or contains collision.");
        }

        System.out.println("Clicked Position: (" + clickX + ", " + clickY + ")");
    }

    private List<Node> findPath(double[] startPos, double[] targetPos) {
        int startX = (int) ((startPos[0] + COLLISION_OFFSET) / CELL_SIZE);
        int startY = (int) ((startPos[1] + COLLISION_OFFSET) / CELL_SIZE);
        int targetX = (int) (targetPos[0] / CELL_SIZE);
        int targetY = (int) (targetPos[1] / CELL_SIZE);

        PriorityQueue<Node> open = new PriorityQueue<>();
        open.offer(new Node(startX, startY, 0, null));

        double[][] cost = new double[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                cost[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        cost[startX][startY] = 0;

        Node[][] cameFrom = new Node[map.length][map[0].length];

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(cameFrom, current);
            }

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0)
                        continue;

                    int neighborX = current.x + i;
                    int neighborY = current.y + j;

                    if (isValidTile(neighborX, neighborY) && map[neighborX][neighborY] != 1) {
                        double tentativeCost = cost[current.x][current.y] + Math.sqrt(i * i + j * j);
                        if (tentativeCost < cost[neighborX][neighborY]) {
                            cost[neighborX][neighborY] = tentativeCost;
                            double priority = tentativeCost + heuristic(neighborX, neighborY, targetX, targetY);
                            open.offer(new Node(neighborX, neighborY, priority, current));
                            cameFrom[neighborX][neighborY] = current;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isValidTile(int x, int y) {
        return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
    }

    private List<Node> reconstructPath(Node[][] cameFrom, Node current) {
        List<Node> totalPath = new ArrayList<>();
        while (current != null) {
            totalPath.add(current);
            current = cameFrom[current.x][current.y];
        }
        List<Node> path = new ArrayList<>();
        for (int i = totalPath.size() - 1; i >= 0; i--) {
            path.add(totalPath.get(i));
        }
        return path;
    }

    private void moveAlongPath(List<Node> path) {
        if (path.size() > 1) {
            Node nextNode = path.get(1);
            targetPosition = new double[] { nextNode.x * CELL_SIZE + CELL_SIZE / 2,
                    nextNode.y * CELL_SIZE + CELL_SIZE / 2 };
            movingToTarget = true;
        }
    }

    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
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

    public static class Node implements Comparable<Node> {
        public int x, y;
        public double cost;
        public Node parent;

        public Node(int x, int y, double cost, Node parent) {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(cost, o.cost);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Character Movement");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            double[] startPosition = { 100, 100 };
            double speed = 100.0;
            double deltaTime = 0.05;

            CharacterMovement3d movementPanel = new CharacterMovement3d(startPosition, speed, deltaTime,
                    frame.getWidth(), frame.getHeight());
            frame.add(movementPanel);
            frame.setVisible(true);
        });
    }
}
