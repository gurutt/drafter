package org.gurutt.drafter.service;


import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
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

    public Map<String, LineUp> select(List<String> participants) {

        List<Player> players = findPlayers(participants);
        return lineUpEngine.decide(io.vavr.collection.List.ofAll(players));
    }

    private List<Player> findPlayers(List<String> participants) {
        Query query = new Query();
        List<Criteria> criteria = participants.map(p -> Criteria.where("slug").is(p.toLowerCase()));
        query.addCriteria(new Criteria().orOperator(criteria.toJavaArray(Criteria.class)));
        return List.ofAll(mongoTemplate.find(query, Player.class));
    }
}
