package external;

import configs.NeuralDemoConfig;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class PythonOutput {

    protected NeuralDemoConfig cfg;

    protected boolean error;

    public PythonOutput(BufferedReader stdInput, BufferedReader stdError,
                        NeuralDemoConfig cfg) {
        this.cfg = cfg;
        handleStdInput(stdInput);
        handleStdError(stdError);
    }

    abstract protected void handleStdInput(BufferedReader stdInput);

    abstract protected void handleStdError(BufferedReader stdError);

    public NeuralDemoConfig getCfg() {
        return cfg;
    }

    public boolean isError() {
        return error;
    }

    public NeuralDemoConfig.NNType getType() {
        return cfg.getNnType();
    }

    protected String buildStringFromReader(BufferedReader reader) throws IOException {

        String sCurrentLine = "";
        String s = "";

        while ((sCurrentLine = reader.readLine()) != null) {
            s += sCurrentLine + "\n";
        }

        return s;
    }
}
