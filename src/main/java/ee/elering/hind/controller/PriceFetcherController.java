package ee.elering.hind.controller;

import ee.elering.hind.service.PriceFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceFetcherController {

    @Autowired
    private PriceFetcherService priceFetcherService;


    // Kui tagastame HTML (roheline värv jne)
    @GetMapping(value = "/price", produces = "text/html")
    public String getPrice() {
        return priceFetcherService.fetchPriceAndTimestamp();
    }
}