package com.ntnu.gidd.util;

import com.ntnu.gidd.dto.User.ContextAwareUserDto;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.security.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextAwareModelMapper extends ModelMapper {

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        D map = super.map(source, destinationType);

        if (map instanceof ContextAwareUserDto) {
            ContextAwareUserDto userDto = ((ContextAwareUserDto) map);
            User user = ((User) source);
            includeUserContext(user, userDto);
        }

        return map;
    }

    private void includeUserContext(User user, ContextAwareUserDto userDto) {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (isAuthenticated(auth))
            tryToIncludeFollowerContext(user, userDto, auth);
    }

    private boolean isAuthenticated(Authentication auth) {
        if (auth == null)
            return false;

        return auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    private void tryToIncludeFollowerContext(User user, ContextAwareUserDto userDto, Authentication auth) {
        try {
            // Some of the tests are skipping proper authentication, causing this to fail
            includeFollowerContext(user, userDto, auth);
        } catch (ClassCastException ignored) {
        }
    }

    private void includeFollowerContext(User user, ContextAwareUserDto userDto, Authentication auth) {
        UserDetailsImpl currentUser = ((UserDetailsImpl) auth.getPrincipal());
        userDto.setCurrentUserIsFollowing(user.getFollowers()
                                                  .stream()
                                                  .map(User::getId)
                                                  .anyMatch(id -> id.equals(currentUser.getId())));
    }
}