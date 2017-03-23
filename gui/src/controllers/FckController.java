package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.System.*;
import java.util.Random;

public class FckController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private Button btnReseed;

    @FXML
    private Button btnPlay;

    @FXML
    private Pane lineChartPane;

    @FXML
    private ProgressBar fckProgressBar;

    @FXML
    private TextField tfStepSpeed;

    @FXML
    private TextField tfStepSize;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        this.init = true;

        this.currentIndex = 0;

        this.result = new LinearNeuron().GetResult();

        this.stepSpeed = 20;

        this.stepSize = 1;

        this.tfStepSize.setText(Integer.toString(this.stepSize));
        this.tfStepSpeed.setText(Integer.toString(this.stepSpeed));

        this.animating = false;


        this.lineChartPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        initLineChart();

        this.atl = new Timeline(new KeyFrame(Duration.millis(stepSpeed), event -> {
            doAnimationStep();
        }));
        this.atl.setCycleCount(Animation.INDEFINITE);

        this.btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                animate();
            }
        });

        this.btnReseed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                result = new LinearNeuron().GetResult();
                currentIndex = 0;
                initLineChart();
                doAnimationStep();
            }
        });

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
                    atl = new Timeline(new KeyFrame(Duration.millis(stepSpeed), event -> {
                        doAnimationStep();
                    }));
                    atl.setCycleCount(Animation.INDEFINITE);
                }
            }
        });

        doAnimationStep();
    }

    // Initiate the linechart
    private void initLineChart() {

        this.lineChartPane.getChildren().remove(this.lineChart);

        xAxis = new NumberAxis(0,this.result.iterations.size()- 1,10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(true);
        xAxis.setTickMarkVisible(true);
        xAxis.setMinorTickVisible(false);

        this.yAxis = new NumberAxis(0,200,10);
        this.yAxis.setForceZeroInRange(false);
        this.yAxis.setAutoRanging(false);
        this.yAxis.setTickLabelsVisible(true);
        this.yAxis.setTickMarkVisible(true);
        this.yAxis.setMinorTickVisible(false);

        // Create a LineChart
        this.lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        this.lineChart.setLegendVisible(false);

        this.fckProgressBar.setMinWidth(this.lineChart.getWidth());

        this.lineChart.setAnimated(false);

        this.lineChartPane.getChildren().add(this.lineChart);

        this.lineChart.setMinSize(600,600);
        this.lineChartPane.setMinSize(500,620);
    }

    // Start animating
    private void animate() {

        if (!animating) {
            btnPlay.setText("Pause");
            this.atl.play();
        } else {
            btnPlay.setText("Run");
            this.atl.stop();
        }

        this.animating = !animating;
        setAllDisabled(this.animating);
    }

    // Single animation step
    private void doAnimationStep() {

        if (currentIndex >= result.iterations.size()) {
            currentIndex = result.iterations.size() - 1;
        }

        XYChart.Series seriesFish = new XYChart.Series();
        // fish = blauw
        seriesFish.setName("Fish price");
        //seriesFish.getChart().

        XYChart.Series seriesChips = new XYChart.Series();
        // chips = groen
        seriesChips.setName("Chips price");

        XYChart.Series seriesKetchup = new XYChart.Series();
        // ketchup = rood
        seriesKetchup.setName("Ketchup price");

        for (int i = 0; i < currentIndex; i++) {
            seriesFish.getData().add(new XYChart.Data(i, this.result.iterations.get(i).fishPrice));
            seriesChips.getData().add(new XYChart.Data(i, this.result.iterations.get(i).chipPrice));
            seriesKetchup.getData().add(new XYChart.Data(i, this.result.iterations.get(i).ketchupPrice));
        }

        lineChart.getData().clear();
        lineChart.getData().add(seriesFish);
        lineChart.getData().add(seriesChips);
        lineChart.getData().add(seriesKetchup);

        if (init) {
            this.init = false;
        } else {
            ObservableList<XYChart.Series> data = lineChart.getData();
            changeLineChartSeriesColour(data.get(data.size() - 3).getNode().lookup(".chart-series-line"),Color.BLUE);
            changeLineChartSeriesColour(data.get(data.size() - 2).getNode().lookup(".chart-series-line"),Color.GREEN);
            changeLineChartSeriesColour(data.get(data.size() - 1).getNode().lookup(".chart-series-line"),Color.RED);
        }


        lineChart.setCreateSymbols(false);

        double progress = ((double)this.currentIndex)/((double)this.result.iterations.size());

        if (this.currentIndex == this.result.iterations.size() - 1) {
            fckProgressBar.setProgress(1.0);
        } else {
            fckProgressBar.setProgress(progress);
        }

        if (currentIndex == result.iterations.size() - 1) {
            animate();
            currentIndex = 0;
        } else {
            currentIndex += this.stepSize;
        }
    }


    private void changeLineChartSeriesColour(Node line, Color color) {
        String rgb = String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        System.out.println(rgb);

        line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
    }

    private void setAllDisabled(boolean disabled) {
        this.tfStepSpeed.setDisable(disabled);
        this.tfStepSize.setDisable(disabled);
        this.btnReseed.setDisable(disabled);
    }

    // whether we are animating or not
    boolean animating;

    // Result of the run
    private FishChipKetchupResult result;

    // Animating timeline
    Timeline atl;

    // Step speed
    int stepSpeed;

    // Step size
    int stepSize;

    // How far we've animated so far
    int currentIndex;

    private LineChart lineChart;

    private NumberAxis xAxis;

    private NumberAxis yAxis;

    boolean init;
}

/**
 *
 * @author evink
 */
class LinearNeuron {

    // actual price of fish, chips, and ketchup
    public static final int ACT_PRICE_FISH = 150;
    public static final int ACT_PRICE_CHIPS = 50;
    public static final int ACT_PRICE_KETCHUP = 100;


    public FishChipKetchupResult GetResult() {

        double eps;
        double her_price, my_price, price_fish, price_chips, price_ketchup;
        int cnt_fish, cnt_chips, cnt_ketchup;
        // initial values
        price_fish = 50;
        price_chips = 50;
        price_ketchup = 50;


        // the maximum value encountered
        double maxValue = Double.MIN_VALUE;

        // the min value encoutered
        double minValue = Double.MAX_VALUE;

        // all ierations
        ArrayList<FishChipKetchupPrice> iterations = new ArrayList<>();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        Random rn = new Random();

        int i=0; int j=10;
        while ( (i < 1000) && (j > 0) ) {

            // generate_random_order
            cnt_fish = rn.nextInt(5) + 1;
            cnt_chips = rn.nextInt(5) + 1;
            cnt_ketchup = rn.nextInt(5) + 1;

            her_price = cnt_fish*ACT_PRICE_FISH + cnt_chips*ACT_PRICE_CHIPS + cnt_ketchup*ACT_PRICE_KETCHUP;
            my_price = cnt_fish*price_fish + cnt_chips*price_chips + cnt_ketchup*price_ketchup;
            i = i+1;
            if ( java.lang.Math.abs(my_price - her_price) < 0.125 ) j--;

            eps = 0.025;
            price_fish = price_fish + eps*cnt_fish*(her_price-my_price);
            price_chips = price_chips + eps*cnt_chips*(her_price-my_price);
            price_ketchup = price_ketchup + eps*cnt_ketchup*(her_price-my_price);

            maxValue = Math.max(Math.max(Math.max(price_chips, price_chips),price_ketchup),maxValue);
            minValue = Math.min(Math.min(Math.min(price_chips, price_chips),price_ketchup),minValue);

            iterations.add(new FishChipKetchupPrice(price_fish,price_chips,price_ketchup));

            System.out.format("%d x ",cnt_fish);
            System.out.format("%3.0f, ",price_fish);
            System.out.format("%d x %3.0f, ",cnt_chips,price_chips);
            System.out.format("%d x %3.0f, ",cnt_ketchup,price_ketchup);
            System.out.format("guessed %3.0f, ",my_price);
            System.out.format("real %3.0f",her_price);
            System.out.println();
        }

        return new FishChipKetchupResult(minValue, maxValue, iterations);
    }
}

class FishChipKetchupResult {

    public double minValue;
    public double maxValue;
    public ArrayList<FishChipKetchupPrice> iterations;

    public FishChipKetchupResult(double minValue, double maxValue, ArrayList<FishChipKetchupPrice> iterations) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.iterations = iterations;
    }
}

class FishChipKetchupPrice {

    public double fishPrice;
    public double chipPrice;
    public double ketchupPrice;

    public FishChipKetchupPrice(double fishPrice, double chipPrice, double ketchupPrice) {
        this.fishPrice = fishPrice;
        this.chipPrice = chipPrice;
        this.ketchupPrice = ketchupPrice;
    }
}