package config;

import log.Log;
import object.Game;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private final Log log;

    public Config(Log log) {
        this.log = log;
    }

    /**
     * Lấy token Discord từ biến môi trường DISCORD_TOKEN
     */
    public static String getDiscordToken() {
        return getRequiredEnv("DISCORD_TOKEN");
    }

    /**
     * Lấy channel ID từ biến môi trường CHANNEL_ID
     */
    public static String getChannelId() {
        return getRequiredEnv("CHANNEL_ID");
    }

    /**
     * Lấy URL Steam từ biến môi trường STEAM_URL
     */
    public static String getSteamUrl() {
        return getRequiredEnv("STEAM_URL");
    }

    /**
     * Lấy log path từ biến môi trường LOG_PATH
     */
    public static String getLogPath() {
        return getRequiredEnv("LOG_PATH");
    }

    /**
     * Kiểm tra và lấy biến môi trường, ném lỗi nếu không có
     */
    private static String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Thiếu biến môi trường: " + key);
        }
        return value;
    }

    /**
     * Lấy toàn bộ game đã log
     */
    public List<Game> getAllLoggedGames() {
        return new ArrayList<>(log.readAll().values());
    }
}
