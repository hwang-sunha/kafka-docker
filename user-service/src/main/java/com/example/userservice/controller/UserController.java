package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/user-service")
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final Environment env;
    private final Greeting greeting;
    private final UserService userService;

    @Timed(value="users.status",longTask=true)
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in User Service"
                +"\nenv.getProperty(local.server.port)=" + env.getProperty("local.server.port")
                +"\nenv.getProperty(server.port)=" + env.getProperty("server.port")
                +"\nenv.getProperty(token.secret)=" + env.getProperty("token.secret")
                +"\nenv.getProperty(token.experation_time)=" + env.getProperty("token.experation_time")
        );
    }
    @Timed(value="users.welcome",longTask=true)
    @GetMapping("/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseUser);


    }
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userByAll = userService.getUserByAll();
        List<ResponseUser> users = new ArrayList<>();
        userByAll.forEach(v -> {
            users.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK)
                .body(users);
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(
            @PathVariable("userId") String userId) {
        UserDto userById = userService.getUserById(userId);
        ResponseUser responseUser = new ModelMapper().map(userById, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseUser);
    }
}
