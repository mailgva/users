package com.horbatenko.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horbatenko.users.model.User;
import com.horbatenko.users.repository.UserRepository;
import com.horbatenko.users.to.UserTo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.horbatenko.users.controller.Utils.userFromUserTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testGetUsers() throws Exception {
        List<User> users = List.of(
                new User(new ObjectId(), "test1", "test1@gmail.com"),
                new User(new ObjectId(), "test2", "test2@gmail.com")
        );

        Mockito.when(userRepository.findAll()).thenReturn(users);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        UserTo[] userTos = mapper.readValue(result.getResponse().getContentAsString(), UserTo[].class);
        List<User> actual = List.of(userTos).stream().map(Utils::userFromUserTo).collect(Collectors.toList());
        assertEquals(users, actual);
    }

    @Test
    public void testDeleteUser() throws Exception {
        ObjectId id = new ObjectId();
        User user = new User(id, "test1", "test1@gmail.com");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/" + id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        ObjectId id = new ObjectId();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .delete("/" +  id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUser() throws Exception {
        ObjectId id = new ObjectId();
        User newUser = new User("test1", "test1@gmail.com");
        User user = new User(id, "test1", "test1@gmail.com");
        Mockito.when(userRepository.save(newUser)).thenReturn(user);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test1\", \"email\": \"test1@gmail.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        UserTo userTo = mapper.readValue(result.getResponse().getContentAsString(), UserTo.class);
        assertEquals(user, userFromUserTo(userTo));
    }

    @Test
    public void testUpdateUser() throws Exception {
        ObjectId id = new ObjectId();
        User newUser = new User("test2", "test2@gmail.com");
        User user1 = new User(id, "test1", "test1@gmail.com");
        User user2 = new User(id, "test1", "test1@gmail.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(newUser)).thenReturn(user2);

        mvc.perform(MockMvcRequestBuilders
                        .put("/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test2\", \"email\": \"test2@gmail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateUserBadRequest() throws Exception {
        ObjectId id = new ObjectId();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .put("/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test2\", \"email\": \"test2@gmail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}