package org.gurutt.drafter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.jackson.datatype.VavrModule;
import org.gurutt.drafter.service.Drafter;
import org.gurutt.drafter.service.PartitionDrafter;
import org.gurutt.drafter.service.SlotSwapDrafter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DrafterApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(DrafterApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return mapper;
    }

    @Bean
    public Map<String, Drafter> drafters() {
        Map<String, Drafter> drafters = new HashMap<>();
        //drafters.put("slot-swap", new SlotSwapDrafter());
        drafters.put("partition", new PartitionDrafter());
        return drafters;
    }

}

