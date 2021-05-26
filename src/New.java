import java.io.*;
import java.util.*;

public class New {
    public static int shortestDistance = Integer.MAX_VALUE;
    public static ArrayList<Integer> visitedGlobal = new ArrayList<Integer>();

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new File("C:\\Users\\conor\\Google Drive\\College\\CS211\\sample2.csv"));
        List<List<String>> rows = new ArrayList<>();
        sc.useDelimiter(","); // sets the delimiter pattern
        while (sc.hasNextLine()) // returns a boolean value
        {
            rows.add(getRecordFromLine(sc.nextLine()));

        }
        sc.close(); // closes the scanner

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).size(); j++) {
                System.out.print(rows.get(i).get(j) + ", ");
            }
            System.out.println();
        }

        for (int i = 0; i < 10; i++) {
            Collections.shuffle(rows);
            calculate(rows);
        }

        for (int i = 0; i < visitedGlobal.size(); i++) {
            System.out.println(visitedGlobal.get(i));
        }
        System.out.println("distance: " + shortestDistance);

        // for (int i = 0; i < matrix.length; i++) {
        // for (int j = 0; j < matrix[i].length; j++) {
        // System.out.print(matrix[i][j] + ",");
        // }
        // System.out.println();
        // }

    }

    static void calculate(List<List<String>> rows) {
        int count = 0;
        int[][] matrix = new int[rows.size()][rows.size()];
        for (int i = 0; i < matrix.length; i++) {
            double rowLat = Double.parseDouble(rows.get(i).get(2));
            double rowLong = Double.parseDouble(rows.get(i).get(3));
            for (int j = 0; j < matrix[i].length; j++) {
                double compareLat = Double.parseDouble(rows.get(j).get(2));
                double compareLong = Double.parseDouble(rows.get(j).get(3));
                int distance = getDistance(rowLat, rowLong, compareLat, compareLong);
                if (distance == 0) {
                    matrix[i][j] = -1;
                } else {
                    Random rn = new Random();
                    matrix[i][j] = (distance + rn.nextInt(20));
                    // matrix[i][j] = distance;
                }
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + ",");
            }
            System.out.println();
        }

        ArrayList<Integer> visited = new ArrayList<Integer>();
        for (int i = 0; i < matrix.length; i++) {

            int shortest = 10000;
            int shortestIndex = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] < shortest && !visited.contains(j) && matrix[i][j] != -1) {
                    shortest = matrix[i][j];
                    shortestIndex = j;
                }
            }
            visited.add(shortestIndex);
            count += shortest;
        }

        if (count <= shortestDistance) {
            shortestDistance = count;
            visitedGlobal = visited;
        }

    }

    static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    static int getDistance(double latOne, double longOne, double latTwo, double longTwo) {

        double latDistance = Math.toRadians(latTwo - latOne);
        double longDistance = Math.toRadians(longTwo - longOne);

        // apply haversine formula
        double formula = Math.pow(Math.sin(latDistance / 2), 2) + Math.pow(Math.sin(longDistance / 2), 2)
                * Math.cos(Math.toRadians(latOne)) * Math.cos(Math.toRadians(latTwo));

        double answer = 2 * Math.asin(Math.sqrt(formula));

        return (((int) (answer * 6371) + 99) / 100) * 100;
    }
}