package discord;

import config.Config;
import control.Control;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBot {

    private final JDA jda;
    private final DiscordNotifier notifier;

    public DiscordBot(Control control) throws Exception {

        // Khởi tạo slash command
        DiscordSlashCommand slashCommand = new DiscordSlashCommand(control);

        // Khởi tạo JDA (kết nối Discord)
        this.jda = JDABuilder
                .createDefault(Config.getDiscordToken())
                .addEventListeners(slashCommand)
                .build();

        // Chờ bot ready
        jda.awaitReady();
        // Đăng ký slash command
        slashCommand.registerCommands(jda);

        // Khởi tạo notifier (cần JDA)
        this.notifier = new DiscordNotifier(jda);

        System.out.println("Discord bot is ready");
    }

    public JDA getJda() {
        return jda;
    }

    public DiscordNotifier getNotifier() {
        return notifier;
    }
}
