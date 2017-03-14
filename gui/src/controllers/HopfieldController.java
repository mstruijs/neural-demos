package controllers;

import auxiliary.Statics;
import external.HopfieldPythonOutput;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/**
 * Created by ziad on 10/03/2017.
 */
public class HopfieldController {

    // the python script output
    private HopfieldPythonOutput hpo;

    // current iteration being shown
    private int currentIterationIndex;

    // square width and height
    double width;
    double height;

    @FXML
    Button btnHfToFirst;

    @FXML
    Button btnHfPrevious;

    @FXML
    Button btnHfNext;

    @FXML
    Button btnHfToLast;

    @FXML
    Canvas canvasHopfield;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        this.hpo = (HopfieldPythonOutput) OpenController.po;

        this.currentIterationIndex = 0;

        this.width = 50;
        this.height = 50;

        drawIteration();

        for(boolean[][] matrix : hpo.getIterations()) {
            Statics.printBooleanMatrix(matrix);
        }

        btnHfToFirst.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firstIteration();
            }
        });

        btnHfPrevious.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previousIteration();
            }
        });

        btnHfNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nextIteration();
            }
        });

        btnHfToLast.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastIteration();
            }
        });
    }

    void drawIteration() {

        GraphicsContext gc = canvasHopfield.getGraphicsContext2D();
        gc.moveTo(0,0);

        boolean[][] matrix = hpo.getIteration(this.currentIterationIndex);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                double x = j*width;
                double y = i*height;
                Color c = Color.WHITE;
                if (matrix[i][j]) {
                    c = Color.BLACK;
                }
                gc.setFill(c);
                gc.fillRect(x,y,width,height);
            }
        }
    }

    void firstIteration() {
        this.currentIterationIndex = 0;
        drawIteration();
    }

    void previousIteration() {
        if (this.currentIterationIndex > 0) {
            this.currentIterationIndex--;
        }
        drawIteration();
    }

    void nextIteration() {
        if (this.currentIterationIndex < hpo.getIterations().size() - 1) {
            this.currentIterationIndex++;
        }
        drawIteration();
    }

    void lastIteration() {
        this.currentIterationIndex = hpo.getIterations().size() - 1;
        drawIteration();
    }
}
