package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.Post;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;
import java.util.UUID;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;

public class PostFactory implements FactoryBean<Post> {

    UserFactory userFactory = new UserFactory();
    ActivityFactory activityFactory = new ActivityFactory();

    @Override
    public Post getObject() throws Exception {
        return Post.builder().content(getRandomString(15))
                .activity(activityFactory.getObject())
                .creator(userFactory.getObject())
                .image(getRandomString(11))
                .likes(List.of())
                .comments(List.of())
                .id(UUID.randomUUID()).build();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
