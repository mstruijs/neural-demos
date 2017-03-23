package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
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
    private LineChart lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button btnReseed;

    @FXML
    private Button btnPlay;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        this.currentIndex = 0;

        this.result = new LinearNeuron().GetResult();

        this.stepSpeed = 200;

        this.animating = false;

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

    }

    // Start animating
    private void animate() {

        if (!animating) {
            this.atl.play();
        } else {
            this.atl.stop();
        }

        this.animating = !animating;

        setAllDisabled(this.animating);
    }

    // Single animation step
    private void doAnimationStep() {

        XYChart.Series seriesFish = new XYChart.Series();
        seriesFish.setName("Fish price");

        XYChart.Series seriesChips = new XYChart.Series();
        seriesChips.setName("Chips price");

        XYChart.Series seriesKetchup = new XYChart.Series();
        seriesChips.setName("Ketchup price");

        for (int i =0; i < currentIndex; i++) {
            seriesFish.getData().add(new XYChart.Data(i, this.result.iterations.get(i).fishPrice));
            seriesChips.getData().add(new XYChart.Data(i, this.result.iterations.get(i).chipPrice));
            seriesKetchup.getData().add(new XYChart.Data(i, this.result.iterations.get(i).ketchupPrice));
        }

        lineChart.getData().clear();
        lineChart.getData().add(seriesFish);
        lineChart.getData().add(seriesChips);
        lineChart.getData().add(seriesKetchup);

        currentIndex++;

        if (currentIndex == result.iterations.size()) {
            atl.stop();
            currentIndex = 0;
        }
    }

    private void setAllDisabled(boolean disabled) {
        btnReseed.setDisable(disabled);
    }

    // whether we are animating or not
    boolean animating;

    // Result of the run
    private FishChipKetchupResult result;

    // Animating timeline
    Timeline atl;

    // Step speed
    int stepSpeed;

    // How far we've animated so far
    int currentIndex;
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

            iterations.add(new FishChipKetchupPrice(price_fish,price_chips,price_ketchup));

            System.out.format("%d x ",cnt_fish);
            System.out.format("%3.0f, ",price_fish);
            System.out.format("%d x %3.0f, ",cnt_chips,price_chips);
            System.out.format("%d x %3.0f, ",cnt_ketchup,price_ketchup);
            System.out.format("guessed %3.0f, ",my_price);
            System.out.format("real %3.0f",her_price);
            System.out.println();
        }

        return new FishChipKetchupResult(maxValue, iterations);
    }
}

class FishChipKetchupResult {

    public double maxValue;
    public ArrayList<FishChipKetchupPrice> iterations;

    public FishChipKetchupResult(double maxValue, ArrayList<FishChipKetchupPrice> iterations) {
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