package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName="of")
public class GameInput {

    private final String sportType;
    private final List<String> participants;
    private final List<String> attributes;
}
