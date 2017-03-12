package auxiliary;

/**
 * Created by Ziad on 10-3-2017.
 */
public class Statics {

    public static void printBooleanMatrix(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                boolean b = matrix[i][j];
                System.out.printf("%5s ", b + "");
            }
            System.out.println();
        }
        System.out.println("----");
    }
}
