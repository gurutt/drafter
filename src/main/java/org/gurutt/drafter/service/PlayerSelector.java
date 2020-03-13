package org.gurutt.drafter.service;


import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.GameInput;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.PlayerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class PlayerSelector {

    private final MongoTemplate mongoTemplate;
    private final LineUpEngine lineUpEngine;

    @Autowired
    public PlayerSelector(MongoTemplate mongoTemplate, LineUpEngine lineUpEngine) {
        this.mongoTemplate = mongoTemplate;
        this.lineUpEngine = lineUpEngine;
    }

    public Map<String, LineUp> select(GameInput gameInput) {

        List<Player> players = findPlayers(gameInput.getParticipants(), gameInput.getSportType());
        return lineUpEngine.decide(players, gameInput.getSportType(), gameInput.getTeamCount());
    }

    public List<Player> listPlayers(String sportType) {
        Query query = new Query();
        if (PlayerData.FOOTBALL.equalsIgnoreCase(sportType)) {
            query.addCriteria(Criteria.where(PlayerData.FOOTBALL).exists(true));
        } else if (PlayerData.BASKETBALL.equalsIgnoreCase(sportType)) {
            query.addCriteria(Criteria.where(PlayerData.BASKETBALL).exists(true));
        }
        else if (PlayerData.DOTA.equalsIgnoreCase(sportType)) {
            query.addCriteria(Criteria.where(PlayerData.DOTA).exists(true));
        }
        return List.ofAll(mongoTemplate.find(query, PlayerData.class)).map(p -> p.toPlayer(sportType));
    }

    private List<Player> findPlayers(List<String> participants, String sportType) {
        Query query = new Query();
        List<Criteria> criteria = participants.map(p -> Criteria.where("slug").is(p.toLowerCase()));
        query.addCriteria(new Criteria().orOperator(criteria.toJavaArray(Criteria.class)));
        return List.ofAll(mongoTemplate.find(query, PlayerData.class)).map(p -> p.toPlayer(sportType));
    }
}
