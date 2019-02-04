package org.gurutt.drafter.service;


import org.gurutt.drafter.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerSelector {

    private final MongoTemplate mongoTemplate;
    private final LineUpEngine lineUpEngine;

    @Autowired
    public PlayerSelector(MongoTemplate mongoTemplate, LineUpEngine lineUpEngine) {
        this.mongoTemplate = mongoTemplate;
        this.lineUpEngine = lineUpEngine;
    }

    public Object select(List<String> participants) {

        List<Player> players = findPlayers(participants);
        return lineUpEngine.decide(io.vavr.collection.List.ofAll(players));
    }

    //TODO replace list with vavr
    private List<Player> findPlayers(List<String> participants) {
        Query query = new Query();
        java.util.List<Criteria> criteria = new ArrayList<>();
        for (String participant : participants) {
            criteria.add(Criteria.where("slug").is(participant.toLowerCase()));
        }
        query.addCriteria(new Criteria().orOperator(criteria.toArray(new Criteria[criteria.size()])));
        return mongoTemplate.find(query, Player.class);
    }
}
