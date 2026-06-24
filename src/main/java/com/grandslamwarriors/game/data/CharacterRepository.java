package com.grandslamwarriors.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class CharacterRepository {
    private CharacterConfig[] characters;

    public CharacterRepository() {
        Json json = new Json();
        characters = json.fromJson(
            CharacterConfig[].class,
            Gdx.files.internal("characters/characters.json")
        );
    }

    public CharacterConfig[] getAll() { return characters; }

    public CharacterConfig getById(String id) {
        for (CharacterConfig c : characters) {
            if (c.id.equals(id)) return c;
        }
        return null;
    }
}
