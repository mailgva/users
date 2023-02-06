package com.horbatenko.users.controller;

import com.horbatenko.users.model.User;
import com.horbatenko.users.to.UserTo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.horbatenko.users.controller.Utils.userFromUserTo;
import static com.horbatenko.users.controller.Utils.userToFromUser;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    ObjectId id = new ObjectId();
    String name = "test";
    String email = "test@mail.com";

    User user = new User(id, name, email);
    UserTo userTo = new UserTo(id.toString(), name, email);

    @Test
    void testUserToFromUser() {
        assertTrue(Objects.deepEquals(userTo, userToFromUser(user)));
    }

    @Test
    void testUserFromUserTo() {
        assertTrue(Objects.deepEquals(user, userFromUserTo(userTo)));
    }
}