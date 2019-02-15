package org.gurutt.drafter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.List;
import org.gurutt.drafter.domain.Player;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class TestPlayerData {

    static final String YURA = "yura";
    static final String IGOR = "igor";
    static final String ROMA = "roma";
    static final String NIKITA = "nikita";
    static final String VANYA = "vanya";
    static final String REUS = "reus";
    static final String VALIK = "valik";
    static final String DIMONR = "dimonr";
    static final String KOLYA = "kolya";
    static final String ROST = "rost";

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

    static List<Player> players(List<String> names) {
        return bySlug(names);
    }

    private static List<Player> bySlug(List<String> slug) {
        return players.filter(player -> slug.contains(player.getSlug()));
    }

    public static Update pullDraftUpdate() {
       return pullUpdate("draftMessage.json");
    }

    public static Update pullPlayerListUpdate() {
        return pullUpdate("playerList.json");
    }

    public static Update pullUpdate(String filename) {
        Resource resource = new ClassPathResource(filename);
        Update update = new Update();
        try (InputStream stream = resource.getInputStream()) {
            update = MAPPER.readValue(stream,
                    Update.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return update;
    }
}
