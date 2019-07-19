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

    public static final String YURA = "yura";
    public static final String IGOR = "igor";
    public static final String ROMA = "roma";
    public static final String NIKITA = "nikita";
    public static final String VANYA = "vanya";
    public static final String REUS = "reus";
    public static final String VALIK = "valik";
    public static final String DIMONR = "dimonr";
    public static final String KOLYA = "kolya";
    public static final String ROST = "rost";
    public static final String SEGEDA = "segeda";
    public static final String ANDREY = "andrey";
    public static final String POBOYKO = "poboyko";
    public static final String SLAVA = "slava";
    public static final String VLADIMIR = "vladimir";
    public static final String JEKA = "jeka";
    public static final String TOHA = "toha";
    public static final String KUZNETSOV = "kuznetsov";
    public static final String IGOR_SAHNO = "igor-sahno";
    public static final String ANTON_SLAVA = "anton-slava";
    public static final String MAKHNO = "makhno";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static List<Player> players;

    public static List<Player> load() {
        Resource resource = new ClassPathResource("players.json");
        try (InputStream stream = resource.getInputStream()) {
            players = List.of(MAPPER.readValue(stream, Player[].class));
            return players;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<Player> players(String... names) {
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
