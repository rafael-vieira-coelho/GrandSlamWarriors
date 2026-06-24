package com.grandslamwarriors.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LeaderboardManager {

    private static final String FILE_NAME = "leaderboard.json";
    private static final int MAX_ENTRIES = 5;

    private Array<LeaderboardEntry> entries;

    public LeaderboardManager() {
        entries = new Array<>();
        load();
        // Remove entradas de teste (Nadal) que foram criadas automaticamente anteriormente
        removeTestEntries();
    }

    private void removeTestEntries() {
        boolean changed = false;
        for (int i = entries.size - 1; i >= 0; i--) {
            LeaderboardEntry entry = entries.get(i);
            if ("Nadal".equalsIgnoreCase(entry.characterName) || "nadal".equalsIgnoreCase(entry.characterId)) {
                entries.removeIndex(i);
                changed = true;
            }
        }
        if (changed) {
            save();
            Gdx.app.log("Leaderboard", "Test entries (Nadal) removed.");
        }
    }

    public void addEntry(String slamName, String characterName, String characterId, int score) {
        LeaderboardEntry entry = new LeaderboardEntry(slamName, characterName, characterId, score, System.currentTimeMillis());
        entries.add(entry);
        entries.sort((a, b) -> Integer.compare(b.score, a.score)); // decrescente
        if (entries.size > MAX_ENTRIES) {
            entries.truncate(MAX_ENTRIES);
        }
        save();
    }

    public Array<LeaderboardEntry> getEntries() {
        return entries;
    }

    private void load() {
        try {
            FileHandle file = Gdx.files.local(FILE_NAME);
            if (file.exists()) {
                String jsonStr = file.readString();
                Json jsonReader = new Json();
                entries = jsonReader.fromJson(Array.class, LeaderboardEntry.class, jsonStr);
                Gdx.app.log("Leaderboard", "Loaded " + (entries != null ? entries.size : 0) + " entries");
            } else {
                Gdx.app.log("Leaderboard", "No leaderboard file found at " + file.file().getAbsolutePath());
            }
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Load failed", e);
            entries = new Array<>();
        }
        if (entries == null) entries = new Array<>();
    }

    private void save() {
        try {
            Json json = new Json();
            String data = json.toJson(entries, Array.class, LeaderboardEntry.class);
            FileHandle file = Gdx.files.local(FILE_NAME);
            file.writeString(data, false);
            Gdx.app.log("Leaderboard", "Saved to: " + file.file().getAbsolutePath());
            Gdx.app.log("Leaderboard", "Data: " + data);
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Save failed", e);
        }
    }

    public static class LeaderboardEntry {
        public String slamName;
        public String characterName;
        public String characterId;
        public int score;
        public long timestamp;

        public LeaderboardEntry() {} // necessário para JSON

        public LeaderboardEntry(String slamName, String characterName, String characterId, int score, long timestamp) {
            this.slamName = slamName;
            this.characterName = characterName;
            this.characterId = characterId;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getFormattedDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}
