package log;

import object.Game;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Log {

    private final File file;

    public Log(String path) {
        this.file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log file", e);
        }
    }

    public boolean write(Game game) {
        Map<Integer, Game> all = readAll();

        if (all.containsKey(game.getId())) {
            return false;
        }

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(file, true))) {

            writer.write(game.toString());
            writer.newLine();
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Game> readAll() {
        Map<Integer, Game> result = new LinkedHashMap<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Game g = parse(line);
                result.put(g.getId(), g);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public boolean deleteById(int gameId) {
        Map<Integer, Game> all = readAll();

        if (!all.containsKey(gameId)) {
            return false;
        }

        all.remove(gameId);
        rewrite(all.values());
        return true;
    }

    public List<Game> getExpiredGames() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> expired = new ArrayList<>();

        for (Game g : readAll().values()) {
            if (g.getFreeUntil() != null &&
                    g.getFreeUntil().isBefore(now)) {
                expired.add(g);
            }
        }
        return expired;
    }

    /* =====================================================
      GHI ĐÈ LẠI TOÀN BỘ FILE
      ===================================================== */
    private void rewrite(Collection<Game> games) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(file, false))) {

            for (Game g : games) {
                writer.write(g.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* =====================================================
       PARSE 1 DÒNG CSV → GAME
       ===================================================== */
    private static Game parse(String line) {
        List<String> parts = parseCsv(line);

        int gameId = Integer.parseInt(parts.get(0));
        String name = parts.get(1);
        String url = parts.get(2);

        LocalDateTime freeUntil =
                parts.size() > 3 && !parts.get(3).isEmpty()
                        ? LocalDateTime.parse(parts.get(3))
                        : null;

        return new Game(gameId, name, url, freeUntil);
    }

    /* =====================================================
       CSV PARSER (hỗ trợ escape)
       ===================================================== */
    private static List<String> parseCsv(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result;
    }

}
