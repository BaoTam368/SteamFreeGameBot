
import config.Config;
import control.Control;
import discord.DiscordBot;
import discord.DiscordNotifier;
import log.Log;
import object.Game;
import steam.Service;

import java.util.List;

public class Main {

    public static void main(String[] args) {


        try {
            System.out.println("=== START APPLICATION ===");

            // 1. Khởi tạo các dependency core
            Service steamService = new Service();
            Log log = new Log(Config.getLogPath());

            // 2. Control (trung tâm nghiệp vụ)
            Control control = new Control(steamService, log);

            // 3. Khởi tạo DiscordBot
            DiscordBot discordBot = new DiscordBot(control);

            // 4. Lấy notifier từ bot
            DiscordNotifier notifier = discordBot.getNotifier();

            // 5. Xóa game hết hạn
            control.deleteOldGames();

            // 6. Khởi tạo DailyScheduler
            List<Game> newGames = control.getNewFreeGames();

            if (!newGames.isEmpty()) {
                for (Game g : newGames) {
                    notifier.sendNewGame(g);
                }
                control.saveNewGames(newGames);
            }

            System.out.println("=== APPLICATION RUNNING ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
