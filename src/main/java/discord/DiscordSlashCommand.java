package discord;

import control.Control;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import object.Game;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiscordSlashCommand extends ListenerAdapter {

    private final Control control;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DiscordSlashCommand(Control control) {
        this.control = control;
    }

    public void registerCommands(JDA jda) {

        jda.getGuilds().forEach(guild -> {
            guild.updateCommands()
                    .addCommands(
                            Commands.slash(
                                    "getfreegames",
                                    "Lấy danh sách game miễn phí hiện tại"
                            )
                    )
                    .queue(
                            success -> System.out.println(
                                    "Slash command registered in guild: "
                                            + guild.getName()
                            ),
                            error -> error.printStackTrace()
                    );
        });
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("getfreegames")) return;

        event.deferReply().queue();

        try {
            List<Game> games = control.getCurrentFreeGames();

            if (games == null || games.isEmpty()) {
                event.getHook()
                        .sendMessage("Hiện tại không có game miễn phí nào.")
                        .queue();
                return;
            }

            StringBuilder response = new StringBuilder();

            for (Game g : games) {
                response.append("Tên game: ")
                        .append(g.getName() != null ? g.getName() : "Không rõ")
                        .append("\n");

                response.append("Link: ")
                        .append(g.getUrl() != null ? g.getUrl() : "Không có")
                        .append("\n");

                response.append("Thời gian hết miễn phí: ")
                        .append(
                                g.getFreeUntil() != null
                                        ? g.getFreeUntil().format(FORMATTER)
                                        : "Không rõ"
                        )
                        .append("\n\n");
            }

            String message = response.toString().trim();

            event.getHook().sendMessage(message).queue();

        } catch (Exception e) {
            e.printStackTrace();
            event.getHook()
                    .sendMessage("Đã xảy ra lỗi khi lấy danh sách game.")
                    .queue();
        }
    }
}
