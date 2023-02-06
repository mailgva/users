package com.horbatenko.users.controller;

import com.horbatenko.users.model.User;
import com.horbatenko.users.repository.UserRepository;
import com.horbatenko.users.to.UserTo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.horbatenko.users.controller.Utils.userFromUserTo;
import static com.horbatenko.users.controller.Utils.userToFromUser;

@RestController
@Slf4j
@RequestMapping("/")
public class UserController {

    private final UserRepository repository;

    public UserController(@Autowired UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<UserTo>> getUsers() {
        log.info("Getting list users");
        List<UserTo> userTos = repository.findAll().stream()
                .map(Utils::userToFromUser).collect(Collectors.toList());
        return ResponseEntity.ok(userTos);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String id) {
        log.info("Delete user with id = {}", id);
        ObjectId objectId = new ObjectId(id);
        Optional<User> user = repository.findById(objectId);
        if (user.isPresent()) {
            repository.deleteById(objectId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<UserTo> addUser(@RequestBody @Valid UserTo userTo) {
        log.info("Add new user = {}", userTo);
        User user = repository.save(userFromUserTo(userTo));
        userTo = userToFromUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("location", userTo.getId());
        return new ResponseEntity<>(userTo, headers, HttpStatus.CREATED);
    }

    @PutMapping ("{id}")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable String id, @RequestBody @Valid UserTo userTo) {
        log.info("Update user with id = {}, body = {}", id, userTo);
        ObjectId objectId = new ObjectId(id);
        Optional<User> tmpUser = repository.findById(objectId);
        if (tmpUser.isPresent()) {
            userTo.setId(id);
            repository.save(userFromUserTo(userTo));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
