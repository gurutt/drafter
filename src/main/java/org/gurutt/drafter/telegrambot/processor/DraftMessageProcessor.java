package org.gurutt.drafter.telegrambot.processor;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.GameInput;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;
import org.gurutt.drafter.service.PlayerSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiFunction;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.gurutt.drafter.domain.PlayerData.BASKETBALL;
import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;

@Component
public class DraftMessageProcessor implements MessageProcessor<Map<String, Object>, Map<String, LineUp>> {

    protected static final String PRM_PLAYERS = "players";
    protected static final String PRM_SPORT_TYPE = "sportType";
    protected static final String PRM_ATTRIBUTE = "attribute";
    private static final String PRM_TEAM_COUNT = "teamCount";
    private static String[] PARAM_NAMES = {PRM_PLAYERS, PRM_SPORT_TYPE, PRM_ATTRIBUTE, PRM_TEAM_COUNT};
    private static final String BOLD = "*";

    private static final BiFunction<Team, String, String> fSkill =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + String.format("%.2f", team.overallSkill()) + "\n";

    private static final BiFunction<Team, String, String> fStamina =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + String.format("%.2f", team.overallPhysics()) + "\n";

    private static final Map<String, BiFunction> DETAILS = HashMap.of(SKILL, fSkill, STAMINA, fStamina);
    //private static final Map<String, Integer> PLAYERS_IN_TEAM = HashMap.of(FOOTBALL, 5, BASKETBALL, 5);

    private static final String DRAFT_CMD = "/draft";

    private final PlayerSelector playerSelector;

    @Autowired
    public DraftMessageProcessor(PlayerSelector playerSelector) {
        this.playerSelector = playerSelector;
    }

    @Override
    public Map<String, Object> parseCmd(String text) {
        List<String> params = List.of(text.replaceAll(DRAFT_CMD, "").trim().split("\\|"));

        return params.zipWithIndex().toLinkedMap(t -> mkEntry(t._1, t._2));
    }

    private Tuple2<String, Object> mkEntry(String param, int idx) {
        return Tuple.of(PARAM_NAMES[idx], param);
    }

    @Override
    public Map<String, LineUp> process(Message message, Map<String, Object> params) {
        List<String> participants = List.of(params.get(PRM_PLAYERS).get().toString().trim().split("\\s*,\\s*"));

        String sportType = !params.get(PRM_SPORT_TYPE).isDefined() ? BASKETBALL
                : params.get(PRM_SPORT_TYPE).get().toString();

        List<String> attributes = !params.get(PRM_ATTRIBUTE).isDefined() ? List.empty() :
                List.of(params.get(PRM_ATTRIBUTE).get().toString().trim().split("\\s*,\\s*"));

        Integer teamCount = !params.get(PRM_TEAM_COUNT).isDefined() ? null
                : Integer.valueOf(params.get(PRM_TEAM_COUNT).get().toString().trim());

        GameInput gameInput = GameInput.of(sportType, participants, attributes, teamCount);
        return playerSelector.select(gameInput);
    }

    @Override
    public boolean match(Update update) {
        return startsWith(update.getMessage().getText(), DRAFT_CMD);
    }

    @Override
    public String response(Map<String, LineUp> teams) {
        return success(teams);
    }

    private String success(Map<String, LineUp> lines) {
        StringBuilder builder = new StringBuilder();
        lines.forEach(l -> {
            builder.append(String.format(wrap("%s version", BOLD) + "\n", l._1));
            builder.append(teams(l._2.getTeams(), l._1));
            builder.append("\n\n");
        });
        return builder.toString();
    }

    private String teams(List<Team> teams, String type) {

        StringBuilder builder = new StringBuilder();
        teams.forEach(team -> {
            builder.append(roster(team));
            List<Player> woDummy = team.getPlayers().removeFirst(p -> p.getSlug().equals("dummy"));
            team.setPlayers(woDummy);
            builder.append(DETAILS.get(type).get().apply(team, type));
            int playersCount = 5;
            if (team.getPlayers().size() > playersCount) {
                builder.append(String.format(wrap("Best %s: ", BOLD), playersCount) +
                        String.format("%.2f", team.getPlayers().take(playersCount).map(Player::getSkill).sum().doubleValue()) + "\n");
            }
            builder.append("\n");
        });

        return builder.toString();
    }

    private String roster(Team team) {
        return wrap("Team: ", BOLD) + String.join(", ", team.getPlayers()
                .sortBy(Player::getSkill).reverse().map(Player::getName)) + "\n";
    }
}
