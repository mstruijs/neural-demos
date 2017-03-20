package controllers;

import auxiliary.Statics;
import configs.NeuralDemoConfig;
import external.HopfieldPythonOutput;
import external.PythonOutput;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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

    @FXML
    private AnchorPane mainPane;

    @FXML // MenuItem: File -> Load
    private MenuItem miFileLoad;

    @FXML // MenuItem: File -> Exit
    private MenuItem miExit;

    @FXML // MenuItem: Help -> About
    private MenuItem miAbout;

    @FXML
    private TabPane tabPaneMain;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        tabPaneMain.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

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
            Tab tab = FXMLLoader.load(getClass().getResource("../resources/tab.fxml"), resources);
            tab.setContent(FXMLLoader.load(getClass().getResource("../resources/hopfield.fxml"), resources));
            tab.setText("");
            Tab editableTab = makeTabTitleEditable(tab, "Hopfield " + (tabPaneMain.getTabs().size() + 1));
            tabPaneMain.getTabs().add(editableTab);
            tabPaneMain.getSelectionModel().select(tabPaneMain.getTabs().size() -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tab makeTabTitleEditable(Tab tab, String text) {
        final Label label = new Label(text);
        tab.setGraphic(label);
        final TextField textField = new TextField();
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount()==2) {
                    textField.setText(label.getText());
                    tab.setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }
            }
        });


        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                label.setText(textField.getText());
                tab.setGraphic(label);
            }
        });


        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if (! newValue) {
                    label.setText(textField.getText());
                    tab.setGraphic(label);
                }
            }
        });
        return tab ;
    }
}
