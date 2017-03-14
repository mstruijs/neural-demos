package controllers;

import auxiliary.Statics;
import configs.NeuralDemoConfig;
import external.HopfieldPythonOutput;
import external.PythonOutput;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.stage.Modality.*;

/**
 * Created by Ziad on 9-3-2017.
 */
public class MainController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // MenuItem: File -> Load
    private MenuItem miFileLoad;

    @FXML // MenuItem: File -> Exit
    private MenuItem miExit;

    @FXML // MenuItem: Help -> About
    private MenuItem miAbout;

    @FXML
    private Pane bodyPane;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        miFileLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showLoadPopup();
            }
        });

        miExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });
    }

    public static boolean loadSuccess = false;
    void showLoadPopup() {
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../resources/open.fxml"),
                    resources);
            stage.setScene(new Scene(root));
            stage.setTitle("Load neural network");
            stage.initModality(APPLICATION_MODAL);
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    loadNeuralNetwork();
                }
            });
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void close() {
        Platform.exit();
        System.exit(0);
    }

    void loadNeuralNetwork() {

        if (!loadSuccess) {
            return;
        }

        System.out.println("loading neural network demo UI");

        PythonOutput po = OpenController.po;
        NeuralDemoConfig ndc = OpenController.nnCfg;

        switch(ndc.getNnType()) {
            case Hopfield:
                loadHopfieldUI((HopfieldPythonOutput) po);
                break;
            default:
                 System.out.println("uh oh");
        }
    }

    // Set the hopfield ui
    void loadHopfieldUI(HopfieldPythonOutput hpo) {
        try {
            bodyPane.getChildren().add(FXMLLoader.load(getClass()
                    .getResource("../resources/hopfield.fxml"), resources));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
