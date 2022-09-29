package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.model.RegistrationId;
import com.ntnu.gidd.model.User;
import org.springframework.beans.factory.FactoryBean;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;

public class RegistrationFactory implements FactoryBean<Registration> {
    @Override
    public Registration getObject() throws Exception {

        User user = new UserFactory().getObject();
        Activity activity = new ActivityFactory().getObject();

        assert user != null;
        assert activity != null;

        return Registration.builder()
                .registrationId(new RegistrationId(user.getId(), activity.getId()))
                .user(user)
                .activity(activity)
                .build();
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
