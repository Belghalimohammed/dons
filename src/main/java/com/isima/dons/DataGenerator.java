package com.isima.dons;

import com.isima.dons.services.DummyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator implements CommandLineRunner {

    private final DummyDataService dummyDataService;

    @Autowired
    public DataGenerator(DummyDataService dummyDataService) {
        this.dummyDataService = dummyDataService;
    }

    @Override
    public void run(String... args) throws Exception {
        dummyDataService.generateDummyData();
    }
}
