package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/" + id, id, null);
    }

    public ResponseEntity<Object> getUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> createUser(UserCreateDto user) {
        return post("", null, user);
    }

    public ResponseEntity<Object> changeUser(UserChangeDto user, Long userId) {
        return patch("/" + userId, userId, user);
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        return delete("/" + id, id);
    }
}
