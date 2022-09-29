package com.ntnu.gidd.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDto extends ContextAwareUserDto {
    private UUID id;
    private String firstName;
    private String surname;
    private String email;
    private String image;
}
