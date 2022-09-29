package com.ntnu.gidd.service.invite;


import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.model.User;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InviteService {
    Page<UserListDto> inviteUser(Predicate predicate, Pageable pageable, UUID activityId, UserEmailDto user);
    public Page<UserListDto> unInviteUser(Predicate predicate, Pageable pageable, UUID activityId, UUID userId);
    boolean inviteBatch(UUID activityId, List<User> users);
    Page<UserListDto> getAllInvites(Predicate predicate, Pageable pageable, UUID activityId);



}
