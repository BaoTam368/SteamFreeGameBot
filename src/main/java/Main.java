import config.Config;
import control.Control;
import discord.DiscordBot;
import discord.DiscordNotifier;
import log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
            Control control = new Control(steamService, log);

            // 2. Khởi tạo JDA trực tiếp
            JDA jda = JDABuilder.createDefault(Config.getDiscordToken())
                    .build()
                    .awaitReady();

            // 4. Lấy notifier từ bot
            DiscordNotifier notifier = new DiscordNotifier(jda);

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
