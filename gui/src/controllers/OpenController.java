package controllers;

import configs.NeuralDemoConfig;
import external.PythonHandler;
import external.PythonOutput;
import external.RetentionFileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ziad on 9-3-2017.
 */
public class OpenController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private ComboBox nnComboBox;

    @FXML
    private Button btnScriptLocationBrowse;

    @FXML
    private Button btnTrainingBrowse;

    @FXML
    private Button btnTestBrowse;

    @FXML
    private Button btnDoneLoad;

    @FXML
    private Button btnCancelLoad;

    @FXML
    private TextArea taScriptLocation;

    @FXML
    private TextArea taTrainingData;

    @FXML
    private TextArea taTestData;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        this.localCfg = new NeuralDemoConfig();

        this.fileChooser = new FileChooser();

        ObservableList<String> nnOptions = FXCollections.observableArrayList("Hopfield","Fish Chips Ketchup");

        nnComboBox.setItems(nnOptions);
        nnComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                retrieveComboBoxSelection();
            }
        });

        btnScriptLocationBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scriptLocationFileChooser();
            }
        });

        btnTrainingBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                trainingFileChooser();
            }
        });

        btnTestBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                testFileChooser();
            }
        });

        btnDoneLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nnLoadDone();
            }
        });

        btnCancelLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancelLoadDialog();
            }
        });
    }

    public static PythonOutput po;
    private void nnLoadDone() {
        nnCfg = localCfg;
        if (localCfg.getNnType().equals(NeuralDemoConfig.NNType.Hopfield)) {
            PythonHandler ph = new PythonHandler();
            try {
                po = ph.runPythonScript(nnCfg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) btnCancelLoad.getScene().getWindow();
        MainController.loadSuccess = true;
        stage.close();
    }

    private void cancelLoadDialog() {
        Stage stage = (Stage) btnCancelLoad.getScene().getWindow();
        MainController.loadSuccess = false;
        stage.close();
    }

    private void scriptLocationFileChooser() {
        File selectedFile = chooseFile();
        if (selectedFile != null) {
            taScriptLocation.setText(selectedFile.getAbsolutePath().toString());
        }
        localCfg.setScriptFile(selectedFile);
    }

    private void trainingFileChooser() {
        File selectedFile = chooseFile();
        if (selectedFile != null) {
            taTrainingData.setText(selectedFile.getAbsolutePath().toString());
        }
        localCfg.setTrainingDataFile(selectedFile);
    }

    private void testFileChooser() {
        File selectedFile = chooseFile();
        if (selectedFile != null) {
            taTestData.setText(selectedFile.getAbsolutePath().toString());
        }
        localCfg.setTestDataFile(selectedFile);
    }

    private void retrieveComboBoxSelection() {
        String selectedNN = nnComboBox.getSelectionModel().getSelectedItem().toString();
        localCfg.setNnType(selectedNN);
        setInteractionImpossible(localCfg.getNnType().equals(NeuralDemoConfig.NNType.FCK));
    }

    private File chooseFile() {
        File selectedFile = RetentionFileChooser.showOpenDialog();
        return selectedFile;
    }

    // Disable buttons and Text Views
    private void setInteractionImpossible(boolean value) {
        btnTrainingBrowse.setDisable(value);
        btnTestBrowse.setDisable(value);
        btnCancelLoad.setDisable(value);
        taScriptLocation.setDisable(value);
        taTrainingData.setDisable(value);
        taTestData.setDisable(value);
    }

    // File browsing
    private FileChooser fileChooser;

    // The parent window
    private Stage parent;

    // The loaded neural network configuration that is used externally
    public static NeuralDemoConfig nnCfg;

    // The member used to temporarily save the values
    private NeuralDemoConfig localCfg;
}