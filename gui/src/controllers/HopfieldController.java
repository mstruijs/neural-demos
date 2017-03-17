package controllers;

import com.sun.javaws.progress.Progress;
import external.HopfieldPythonOutput;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;

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

    // animation speed
    long stepSize;
    long stepSpeed;
    boolean animating;

    // timer
    Timer timer;
    TimerTask task;

    @FXML
    Button btnHfToFirst;

    @FXML
    Button btnHfPrevious;

    @FXML
    Button btnHfNext;

    @FXML
    Button btnHfToLast;

    @FXML
    Button btnHfRun;

    @FXML
    Canvas canvasHopfield;

    @FXML
    TextField tfStepSize;

    @FXML
    TextField tfStepSpeed;

    @FXML
    Text taDescription;

    @FXML
    Text textCurrentIteration;

    @FXML
    ProgressBar hfProgressBar;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        this.hpo = (HopfieldPythonOutput) OpenController.po;

        String description = "Script:\n" + this.hpo.getCfg().getScriptFile().getName() + "\n\n"
                + "Training data:\n" + this.hpo.getCfg().getTrainingDataFile().getName() + "\n\n"
                + "Test data:\n" + this.hpo.getCfg().getTestDataFile().getName();

        this.taDescription.setText(description);

        this.currentIterationIndex = 0;

        this.width = 50;
        this.height = 50;

        this.stepSize = 10;
        this.stepSpeed = 1000;

        this.animating = false;

        tfStepSize.setText(this.stepSize + "");
        tfStepSpeed.setText(this.stepSpeed + "");

        this.timer = new Timer();

        this.task = new TimerTask() {
            @Override
            public void run() {
                doAnimationStep();
            }
        };

        this.endReached = false;

        drawIteration();

        tfStepSize.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()){
                    return;
                }
                if (!newValue.matches("\\d*")) {
                    tfStepSize.setText(newValue.replaceAll("[^\\d]", ""));
                } else {
                    stepSize = Integer.parseInt(newValue);
                }
            }
        });

        tfStepSpeed.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()){
                    return;
                }
                if (!newValue.matches("\\d*")) {
                    tfStepSpeed.setText(newValue.replaceAll("[^\\d]", ""));
                } else {
                    stepSpeed = Integer.parseInt(newValue);
                }
            }
        });

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

        btnHfRun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                animate();
            }
        });
    }

    void animate() {
        if (!animating) {
            btnHfRun.setText("Pause");
            setInteractionPossible(true);
            this.timer.scheduleAtFixedRate(task, stepSpeed, stepSpeed);
            animating = true;
        } else {
            btnHfRun.setText("Run");
            setInteractionPossible(false);
            this.timer.cancel();
            this.timer = new Timer();
            this.task = new TimerTask() {
                @Override
                public void run() {
                    doAnimationStep();
                }
            };
            animating = false;
        }
    }

    void setInteractionPossible(boolean possible) {
        btnHfNext.setDisable(possible);
        btnHfToFirst.setDisable(possible);
        btnHfToLast.setDisable(possible);
        btnHfPrevious.setDisable(possible);
        tfStepSize.setDisable(possible);
        tfStepSpeed.setDisable(possible);
    }

    boolean endReached;
    void doAnimationStep() {
        this.currentIterationIndex += stepSize;
        if (this.currentIterationIndex >= hpo.getIterations().size()) {
            this.currentIterationIndex = hpo.getIterations().size() - 1;
            this.endReached = true;
        }
        drawIteration();
    }

    void drawIteration() {

        textCurrentIteration.setText("Current iteration: " + (this.currentIterationIndex + 1) + "/" + hpo.getIterations().size());

        GraphicsContext gc = canvasHopfield.getGraphicsContext2D();

        boolean[][] matrix = hpo.getIteration(this.currentIterationIndex);

        this.canvasHopfield.setHeight(matrix.length*height);
        this.canvasHopfield.setWidth(matrix.length*width);

        this.hfProgressBar.setMinWidth(matrix.length*width);

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

        double progress = ((double)this.currentIterationIndex)/((double)hpo.getIterations().size());

        if (this.currentIterationIndex == hpo.getIterations().size() - 1) {
            hfProgressBar.setProgress(1.0);
        } else {
            hfProgressBar.setProgress(progress);
        }

        drawBorder(gc);
    }

    private void drawBorder(GraphicsContext g) {
        final double canvasWidth = g.getCanvas().getWidth();
        final double canvasHeight = g.getCanvas().getHeight();

        g.setStroke(Color.BLACK);
        g.setLineWidth(4);
        g.strokeRect(0, 0, canvasWidth, canvasHeight);
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
