package org.gurutt.drafter.service;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;

public interface Drafter {

    public LineUp decide(List<Player> players, DraftContext draftContext);
}
