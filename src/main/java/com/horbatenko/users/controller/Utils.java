package com.horbatenko.users.controller;

import com.horbatenko.users.model.User;
import com.horbatenko.users.to.UserTo;
import org.bson.types.ObjectId;

public final class Utils {
    public static UserTo userToFromUser(User user) {
        return new UserTo(user.getId().toString(), user.getName(), user.getEmail());
    }

    public static User userFromUserTo(UserTo userTo) {
        return new User(userTo.getId() != null ? new ObjectId(userTo.getId()) : null,
                userTo.getName(), userTo.getEmail());
    }
}
