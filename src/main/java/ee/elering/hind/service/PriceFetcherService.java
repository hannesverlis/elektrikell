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
        long timestampSecondsFI = dataItemFI.getLong("timestamp");


        // EE
        // ✅ Teisendame Unix timestamp'i õigesse Eesti ajavööndisse
        Instant instantEE = Instant.ofEpochSecond(timestampSecondsEE);
        ZonedDateTime dateTimeEE = instantEE.atZone(ZoneId.of("Europe/Tallinn"));
        // ✅ Vormindame kuupäeva ja kellaaja: nt 14.04.2025 01:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");//
        String formattedTimeEE = dateTimeEE.format(formatter);

        //FI
        Instant instantFI = Instant.ofEpochSecond(timestampSecondsFI);
        ZonedDateTime dateTimeFI = instantFI.atZone(ZoneId.of("Europe/Helsinki"));
        String formattedTimeFI = dateTimeFI.format(formatter);

        double priceDiff = priceEE - priceFI;

        // ✅ Roheline tekst uue reaga HTML-na (sobib brauseris)
        return "<span style='color:green;'>This method gets latest available NordPool EE day-ahead price<br><br>Time: " + formattedTimeEE + "<br><br>Estonia EE price: " + priceEE + " EUR/MWh<br><br>Finland FI price: " + priceFI + "EUR/MWh <br><br> <span style='color:red;'> Price difference (EE-FI) " + priceDiff + " EUR</span>"+ "</span";
    }

}
