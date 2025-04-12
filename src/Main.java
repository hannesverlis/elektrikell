import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.JSONObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class ElectricityPriceChart {
    public static void main(String[] args) {
        String apiUrl = "https://dashboard.elering.ee/api/nps/price";

        try {
            // Tee HTTP GET päring
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Loe vastus
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Töötle JSON vastust
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject priceData = jsonResponse.getJSONObject("data").getJSONObject("ee"); // Eesti hinnad

            // Andmete hoidla graafiku jaoks
            XYSeries series = new XYSeries("Elektrihind (€/MWh)");

            System.out.println("Elektrihinnad Eestis (€/MWh):");
            int hourCounter = 0;
            for (String timestamp : priceData.keySet()) {
                double price = priceData.getDouble(timestamp);
                long timeMillis = Long.parseLong(timestamp) * 1000L; // Teisenda UNIX-ajaks
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timeMillis));

                System.out.println(dateTime + " → " + price + " €/MWh");

                // Lisa andmed graafikule (X = tund, Y = hind)
                series.add(hourCounter, price);
                hourCounter++;
            }

            // Loo andmekogumik
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);

            // Loo graafik
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Elektrihind Eestis",
                    "Tund",
                    "Hind (€/MWh)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            // Kuva graafik
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Elektrihinna Graafik");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new ChartPanel(chart));
                frame.pack();
                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
