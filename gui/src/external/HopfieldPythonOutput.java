package external;

import auxiliary.Statics;
import configs.NeuralDemoConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HopfieldPythonOutput extends PythonOutput {

    private ArrayList<boolean[][]> iterations;

    public HopfieldPythonOutput(BufferedReader stdInput, BufferedReader stdError, NeuralDemoConfig cfg) {
        super(stdInput, stdError, cfg);
    }

    @Override
    protected void handleStdInput(BufferedReader stdInput) {

        this.iterations = new ArrayList<>();
        iterations.add(new boolean[10][10]);
        try {
            String sCurrentLine = "";
            int row = 0;
            while ((sCurrentLine = stdInput.readLine()) != null) {
                if (sCurrentLine.equals("--Start")) {
                    continue;
                } else if (sCurrentLine.equals("--End")) {
                    break;
                } else if (sCurrentLine.length() > 20) {
                    continue;
                } else {
                    if (sCurrentLine.toLowerCase().contains("iteration")) {
                        row = 0;
                        iterations.add(new boolean[10][10]);
                    } else {
                        String[] split = sCurrentLine.split("\\s+");
                        int col = 0;
                        for (String s : split) {
                            iterations.get(iterations.size()-1)[row][col] = s.equals("X");
                            col++;
                        }
                        row++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleStdError(BufferedReader stdError) {

        String output = null;
        try {
            output = buildStringFromReader(stdError);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.error = (output != null);

        if (output != null) {
            //System.out.println("errors or warnings: \n " + output);
        }
    }
}
