public class GameSimulation {
    private int[][] map;
    private int characterX;
    private int characterY;

    public GameSimulation(int[][] map, int startX, int startY) {
        this.map = map;
        this.characterX = startX;
        this.characterY = startY;
    }

    public boolean canMoveTo(int x, int y) {
        if (x < 0 || x >= map[0].length || y < 0 || y >= map.length) {
            return false;
        }
        return map[y][x] == 1;
    }

    public void moveCharacter(int x, int y) {
        if (canMoveTo(x, y)) {
            characterX = x;
            characterY = y;
            System.out.println("Karakter bergerak ke: (" + x + ", " + y + ")");
        } else {
            System.out.println("Tidak dapat bergerak ke: (" + x + ", " + y + ")");
        }
    }

    public void printMap() {
        System.out.println("Map saat ini:");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (characterX == j && characterY == i) {
                    System.out.print("P "); // Player
                } else if (map[i][j] == 1) {
                    System.out.print("_ "); // Map tiles blank
                } else {
                    System.out.print("X "); // collison hit
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[][] map = {
                { 1, 0, 1 },
                { 0, 1, 0 },
                { 1, 1, 1 }
        };

        GameSimulation game = new GameSimulation(map, 0, 0);
        game.printMap();

        game.moveCharacter(1, 0);
        game.printMap();

        game.moveCharacter(2, 1);
        game.printMap();

        game.moveCharacter(2, 2);
        game.printMap();

        game.moveCharacter(0, 2);
        game.printMap();
    }
}
