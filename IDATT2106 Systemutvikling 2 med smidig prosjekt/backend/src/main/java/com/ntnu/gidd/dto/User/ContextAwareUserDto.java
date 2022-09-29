package com.ntnu.gidd.dto.User;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class ContextAwareUserDto {
    protected boolean currentUserIsFollowing;
}
