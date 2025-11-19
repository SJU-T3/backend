package com.example.demo.user.service;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserEntity saveOrGetUser(Long kakaoId, String name){
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);

        if(existingUser.isPresent())
            return existingUser.get();

        UserEntity newUser = new UserEntity(kakaoId, name);
        return userRepository.save(newUser);
    }
}
