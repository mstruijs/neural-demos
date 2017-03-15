package external;

import configs.NeuralDemoConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ziad on 8-3-2017.
 */
public class PythonHandler {

    public PythonOutput runPythonScript(NeuralDemoConfig cfg) throws IOException{

        String cmd = getPythonCliCommand(cfg);
        System.out.println(cmd);

        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        return buildPythonOutput(stdInput, stdError, cfg);
    }

    private String getPythonCliCommand(NeuralDemoConfig cfg) {

        StringBuilder sb = new StringBuilder();
        sb.append("python ");
        sb.append(cfg.getScriptFile().getAbsolutePath());
        sb.append(" -g ");
        sb.append(cfg.getTrainingDataFile().getAbsolutePath());
        sb.append(" -t ");
        sb.append(cfg.getTestDataFile().getAbsolutePath());
        return sb.toString();
    }

    private PythonOutput buildPythonOutput(BufferedReader stdInput, BufferedReader stdError, NeuralDemoConfig cfg) {
        switch(cfg.getNnType()) {
            case Hopfield:
                return new HopfieldPythonOutput(stdInput, stdError, cfg);
            default:
                throw new IllegalArgumentException("NeuralDemoConfig has illegal NnType");
        }
    }
}

