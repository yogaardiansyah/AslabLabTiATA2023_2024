import java.util.Scanner;

public class CharacterMovementInput {
    public static void moveTo(double[] position, double[] targetPosition, double speed, double deltaTime) {
        double deltaX = targetPosition[0] - position[0];
        double deltaY = targetPosition[1] - position[1];
        double distance = VectorUtils.magnitude(new double[] { deltaX, deltaY });
        double[] direction = new double[] { deltaX / distance, deltaY / distance };
        double stepDistance = speed * deltaTime;
        double[] step = VectorUtils.multiply(direction, stepDistance);

        while (distance > stepDistance) {
            position = VectorUtils.add(position, step);
            distance -= stepDistance;
            System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
            try {
                Thread.sleep((long) (deltaTime * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        position[0] = targetPosition[0];
        position[1] = targetPosition[1];
        System.out.println("Character Position: (" + position[0] + ", " + position[1] + ")");
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter start position (x y): ");
        double startX = scanner.nextDouble();
        double startY = scanner.nextDouble();
        double[] startPosition = { startX, startY };

        System.out.print("Enter target position (x y): ");
        double targetX = scanner.nextDouble();
        double targetY = scanner.nextDouble();
        double[] targetPosition = { targetX, targetY };

        double speed = 2.0;
        double deltaTime = 0.5;

        moveTo(startPosition, targetPosition, speed, deltaTime);
        scanner.close();
    }
}
