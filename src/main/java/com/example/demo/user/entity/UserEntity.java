package com.example.demo.user.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;
    private String name;

    public UserEntity() {}

    public UserEntity(Long kakaoId, String name){
        this.kakaoId = kakaoId;
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public Long getKakaoId(){
        return kakaoId;
    }

    public String getName(){
        return name;
    }

}
