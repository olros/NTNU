package com.ntnu.gidd.dto.followers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FollowRequest {

    private UUID actorId;
    private UUID subjectId;

    public boolean isIdentical() {
        return actorId.equals(subjectId);
    }
}
