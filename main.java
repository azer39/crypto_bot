package de.azer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.ActionType;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import de.azer.telegram.telegram;

public class main {

	public static void main(String[] args) throws TelegramApiRequestException, IOException {
		functions.initialize();

		String token = functions.getTelegramToken("telegram.txt");

		if (!token.isEmpty()) {
			ApiContextInitializer.init();
			TelegramBotsApi tgapi = new TelegramBotsApi();

			telegram bot = new telegram();
			tgapi.registerBot(bot);
			SendChatAction action = new SendChatAction();
			action.setAction(ActionType.TYPING);
			action.setChatId("-1001357288107");

			Thread scanner = new Thread("Scanner") {
				@Override
				public void run() {
					try {
						while (true) {
							bot.sendChatAction(action);
							c_scanner.scan(bot);
							Thread.sleep(5000);
						}
					} catch (TelegramApiException | InterruptedException | IOException e) {
						e.printStackTrace();
					}
				}
			};

			Thread scapper = new Thread("Scapper") {
				@Override
				public void run() {
					while (true) {
						site_checker.blogListener(bot);
						try {
							Thread.currentThread();
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};

			scanner.start();
			scapper.start();
		} else {
			String write = "Token-000000000000000000000000000000";
			Files.write(Paths.get("telegram.txt"), write.getBytes(), StandardOpenOption.CREATE_NEW);
			System.out.println("Insert a Telegram Bot Token and restart the service!");
			System.exit(0);
		}
	}
}
