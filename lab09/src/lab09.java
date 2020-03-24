import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

public class lab09 extends Application {
    final private int WIDTH = 800;
    final private int HEIGHT = 700;

    public void start(Stage primaryStage) {
        // ArrayList of doubles given by the api
        ArrayList<Double> MSFTStock = downloadStockPrices("MSFT");
        ArrayList<Double> AAPLStock = downloadStockPrices("AAPL");

        double yScale = Math.max(Collections.max(MSFTStock), Collections.max(AAPLStock));
        double stockMaxElements = Math.max(MSFTStock.size(), AAPLStock.size());
        int start = 20, xOffset = 30, yOffset = 30;

        Canvas canvas = createLineChart(start, xOffset, yOffset);

        plotLine(canvas, MSFTStock, start, xOffset, yOffset, yScale, stockMaxElements, Color.BLUE);
        plotLine(canvas, AAPLStock, start, xOffset, yOffset, yScale, stockMaxElements, Color.RED);

        // canvas needs to be in a pane of some sort to add to Scene
        Scene scene = new Scene(new Pane(canvas), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lab 9");

        primaryStage.show();
    }

    // private function for creating the canvas and drawing the lines.
    private Canvas createLineChart(int start, int xOffset, int yOffset) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        // Line for the x axis
        graphicsContext.strokeLine(start, HEIGHT - yOffset, WIDTH - xOffset - xOffset, HEIGHT - yOffset);
        // Line for the y axis. Final argument ensures the line is always yOffset pixels below the total height
        graphicsContext.strokeLine(start, HEIGHT - yOffset, start, HEIGHT - (HEIGHT - yOffset));

        return canvas;
    }

    // Function to draw the lines on the given canvas
    private void plotLine(Canvas canvas, ArrayList<Double> stock,
                              int start, int xOffset, int yOffset,
                              double yScale, double stockMaxElements,
                              Color color) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(color);
        graphicsContext.beginPath();

        double widthOfPlot = WIDTH - xOffset - start;
        double startOfYAxis = HEIGHT - yOffset;
        double heightOfPlot = startOfYAxis - yOffset;
        double pointDistance = widthOfPlot / stockMaxElements;

        // Iterate over all values and do some math to keep everything scaled right
        for (double i : stock) {
            graphicsContext.lineTo(start, startOfYAxis - (i / yScale) * heightOfPlot);
            start += pointDistance;
        }

        graphicsContext.stroke();
    }


    // Fill the list with values
    private ArrayList<Double> downloadStockPrices(String stock){
        // Key from alphavantage
        String apiKey = "9RSNK187AT0XEV45";
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=" +
                      stock +
                      "&apikey=" + apiKey;

        JsonObject tier1;
        JsonObject tier2;
        ArrayList<Double> closingPrice = new ArrayList<>();

        StringBuilder data = new StringBuilder();
        try {
            Scanner conScan = new Scanner(new URL(url).openConnection().getInputStream());

            while (conScan.hasNextLine()) {
                data.append(conScan.nextLine());
            }
            conScan.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        tier1 = parser.parse(data.toString()).getAsJsonObject();

        if (tier1 != null){

            //parse second fold
            tier2 = tier1.get("Monthly Time Series").getAsJsonObject();

            //parse third fold
            for(Map.Entry<String, JsonElement> element : tier2.entrySet()){
                closingPrice.add(element.getValue().getAsJsonObject().get("4. close").getAsDouble());
            }
        }
        return closingPrice;
    }
}