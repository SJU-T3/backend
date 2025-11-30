package com.example.demo.chat.dto;

import com.example.demo.chat.entity.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCharacterRequest {

    @Schema(description = "변경할 캐릭터", example = "DAY")
    private CharacterType character;
}
