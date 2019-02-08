package org.gurutt.drafter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.List;
import org.gurutt.drafter.domain.Player;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

class TestPlayerData {

    static final String YURA = "yura";
    static final String IGOR = "igor";
    static final String ROMA = "roma";
    static final String NIKITA = "nikita";
    static final String VANYA = "vanya";
    static final String REUS = "reus";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static List<Player> players;

    static List<Player> load() {
        Resource resource = new ClassPathResource("players.json");
        try (InputStream stream = resource.getInputStream()) {
            players = List.of(MAPPER.readValue(stream, Player[].class));
            return players;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static List<Player> players(String... names) {
        return bySlug(List.of(names));
    }

    private static List<Player> bySlug(List<String> slug) {
        return players.filter(player -> slug.contains(player.getSlug()));
    }
}
