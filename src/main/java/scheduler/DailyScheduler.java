package scheduler;

import control.Control;
import discord.DiscordNotifier;
import object.Game;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyScheduler {

    private final Control control;
    private final DiscordNotifier notifier;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public DailyScheduler(Control control, DiscordNotifier notifier) {
        this.control = control;
        this.notifier = notifier;
    }

    public void start() {

        Runnable task = () -> {
            try {
                System.out.println("===== DAILY STEAM CHECK =====");
                System.out.println("Time: " + LocalDateTime.now());

                // 1. Xóa game hết hạn
                control.deleteOldGames();

                // 2. Lấy game free mới
                List<Game> newGames = control.getNewFreeGames();

                if (!newGames.isEmpty()) {
                    for (Game g : newGames) {
                        System.out.println(g.getName());
                        System.out.println(g.getUrl());
                        System.out.println("Free until: " + g.getFreeUntil());
                        System.out.println();
                        notifier.sendNewGame(g);
                    }
                    control.saveNewGames(newGames);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // ✅ 1. CHẠY NGAY KHI START APP
        scheduler.execute(task);

        // ✅ 2. TÍNH DELAY ĐẾN 7H SÁNG TIẾP THEO
        long initialDelay = TimeUtil.secondsUntilNext7AM();

        // ✅ 3. CHẠY DAILY
        scheduler.scheduleAtFixedRate(
                task,
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );

        System.out.println(
                "Scheduler started. First run executed immediately. " +
                        "Next run in " + initialDelay + " seconds"
        );
    }
}
