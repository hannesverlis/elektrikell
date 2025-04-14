package ee.elering.hind.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PriceFetcherService {

    private static final String API_URL_EE = "https://dashboard.elering.ee/api/nps/price/EE/latest";
    private static final String API_URL_FI = "https://dashboard.elering.ee/api/nps/price/FI/latest";

    public String fetchPriceAndTimestamp() {
        RestTemplate restTemplate = new RestTemplate();
        // EE price
        String responseEE = restTemplate.getForObject(API_URL_EE, String.class);
        JSONObject jsonResponseEE = new JSONObject(responseEE);
        JSONObject dataItemEE = jsonResponseEE.getJSONArray("data").getJSONObject(0);

        double priceEE = dataItemEE.getDouble("price");
        long timestampSecondsEE = dataItemEE.getLong("timestamp");

        // FI price

        String responseFI = restTemplate.getForObject(API_URL_FI, String.class);
        JSONObject jsonResponseFI = new JSONObject(responseFI);
        JSONObject dataItemFI = jsonResponseFI.getJSONArray("data").getJSONObject(0);

        double priceFI = dataItemFI.getDouble("price");

        // EE
        // Convert Unix timestamp to the correct Estonian time zone.  Teisendame Unix timestamp'i õigesse Eesti ajavööndisse
        Instant instant = Instant.ofEpochSecond(timestampSecondsEE);
        ZonedDateTime dateTime = instant.atZone(ZoneId.of("Europe/Tallinn"));
        // We format the date and time:  14.04.2025 01:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");//
        String formattedTime = dateTime.format(formatter);

        double priceDiff = priceEE - priceFI;

        //  HTML format
        return "<span style='color:green;'>This method gets latest available NordPool EE day-ahead price<br><br>Time: " + formattedTime + "<br><br>Estonia EE price: " + priceEE + " EUR/MWh<br><br>Finland FI price: " + priceFI + "EUR/MWh <br><br> <span style='color:red;'> Price difference (EE-FI) " + priceDiff + " EUR</span>" + "</span";
    }

}
