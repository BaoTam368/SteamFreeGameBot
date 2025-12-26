package discord;

import config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import object.Game;

import java.time.format.DateTimeFormatter;

public class DiscordNotifier {

    private final JDA jda;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DiscordNotifier(JDA jda) {
        this.jda = jda;
    }

    public void sendNewGame(Game game) {

        if (game == null || game.getUrl() == null) return;

        TextChannel channel =
                jda.getTextChannelById(Config.getChannelId());

        if (channel == null) {
            System.err.println("Không tìm thấy Discord channel");
            return;
        }

        StringBuilder message = new StringBuilder();

        message
                .append("Tên game: ")
                .append(game.getName() != null ? game.getName() : "Không rõ")
                .append("\n")

                .append("Link: ")
                .append(game.getUrl())
                .append("\n")

                .append("Thời gian hết miễn phí: ")
                .append(
                        game.getFreeUntil() != null
                                ? game.getFreeUntil().format(FORMATTER)
                                : "Không rõ"
                );

        channel.sendMessage(message.toString())
                .queue(
                        success -> System.out.println(
                                "Đã gửi notify game: " + game.getName()
                        ),
                        error -> {
                            System.err.println("Gửi notify thất bại");
                            error.printStackTrace();
                        }
                );
    }
}
