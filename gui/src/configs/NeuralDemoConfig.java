package configs;

import java.io.File;

/**
 * Created by Ziad on 9-3-2017.
 */
public class NeuralDemoConfig {

    public enum NNType {
        Hopfield,
        FCK
    }

    private NNType nnType;

    private File scriptFile;

    private File trainingData;

    private File testData;

    public NNType getNnType() {
        return nnType;
    }

    public void setNnType(String nnType) {
        switch (nnType) {
            case "Hopfield":
                this.nnType = NNType.Hopfield;
                break;
            default:
                this.nnType = NNType.FCK;
        }
    }

    public File getTrainingDataFile() {
        return trainingData;
    }

    public void setTrainingDataFile(File trainingDataFile) {
        this.trainingData = trainingDataFile;
    }

    public File getTestDataFile() {
        return testData;
    }

    public void setTestDataFile(File testDataFile) {
        this.testData = testDataFile;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }
}
