package com.ntnu.gidd.dto.Comment;

import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.model.TimeStampedModel;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto extends TimeStampedModel {

    private UUID id;

    @NotNull
    private String comment;

    private UserListDto user;
}
