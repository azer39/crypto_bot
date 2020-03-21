package de.azer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import de.azer.telegram.telegram;

public class c_scanner {

	@SuppressWarnings("resource")
	public static void scan(telegram bot) throws IOException {
		List<String> fdata = Files.readAllLines(Paths.get("cfg.txt"));

		for (String e : fdata) {
			String[] splitted = e.split(":");
			String coin = splitted[0];

			File coinfile = new File("data/" + coin + ".txt");

			if (coinfile.exists()) {
				List<String> value = Files.readAllLines(Paths.get("data/" + coin + ".txt"));
				BufferedReader br = new BufferedReader(new FileReader("data/" + coin + ".txt"));

				if (br.readLine() != null) {
					String lastvalue = value.get(value.size() - 1).split("/")[1].replace("$", "");

					String newValue = functions.getPrice(coin).replace("$", "");

					if (!newValue.equalsIgnoreCase(lastvalue)) {
						Float F_difference = Float.valueOf(newValue.replace(",", ""))
								- Float.valueOf(lastvalue.replace(",", ""));
						Float F_coin = Float.valueOf(newValue.replace(",", ""));

						if (F_difference > 0) {
							if (F_difference > F_coin) {
								bot.sendToUser(telegram.getGroupID(),
										"-> " + coin + " has changed a lot! " + F_difference + "$");
							}

							Float F_average = Float.valueOf(functions.getAverage(coin));

							Float diff = functions.getPercent(F_average, F_coin);
							if (100 - diff > 5) {
								// bot.sendToUser(telegram.getGroupID(), "-> " + a[0] + " AVERAGE GAIN " +
								// (100-diff) + "%");
							}
						} else {
							if (F_difference < (F_coin * (-1))) {
								bot.sendToUser(telegram.getGroupID(),
										"-> " + coin + " has changed a lot! " + F_difference + "$");
							}
							Float aa = Float.valueOf(functions.getAverage(coin));

							Float diff = functions.getPercent(aa, F_coin);
							if (100 - diff > 5) {
								// bot.sendToUser(telegram.getGroupID(), "-> " + a[0] + " AVERAGE LOSE " +
								// (100-diff) + "%");
							}
						}
						newValue = java.time.LocalDate.now() + " - " + java.time.LocalTime.now() + "/$" + newValue
								+ "\n";
						Files.write(Paths.get("data/" + coin + ".txt"), newValue.getBytes(), StandardOpenOption.APPEND);
						System.out.println(coin + " => " + newValue);
					}
				} else {
					String v = functions.getPrice(coin);
					v = java.time.LocalDate.now() + " - " + java.time.LocalTime.now() + "/" + v + "\n";
					Files.write(Paths.get("data/" + coin + ".txt"), v.getBytes(), StandardOpenOption.APPEND);
				}
			} else {
				coinfile.createNewFile();
			}
		}
	}
}
