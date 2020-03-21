package de.azer;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import de.azer.telegram.telegram;

public class site_checker {

	public static void blogListener(telegram bot) {
		try {
			List<String> blogs = Files.readAllLines(Paths.get("blogs.txt"));

			for (String line : blogs) {
				// COINNAME CHANGEVALUE URL
				String[] splitted = line.split(" ");

				File f = new File("blogs/" + splitted[0] + ".txt");
				f.createNewFile();

				String data = functions.getHTTPSData(new URL(splitted[2]));
				String fdata = new String(Files.readAllBytes(Paths.get("blogs/" + splitted[0] + ".txt")));

				float F_d1 = 0, F_d2 = 0;
				float F_erg = 0;

				for (int i = 0; i < data.length(); i++) {
					F_d1++;
				}
				for (int i = 0; i < fdata.length(); i++) {
					F_d2++;
				}

				if (F_d1 > F_d2) {
					F_erg = F_d2 / F_d1;
				} else {
					F_erg = F_d1 / F_d2;
				}

				if (F_erg < Float.valueOf(splitted[1])) {
					Files.write(Paths.get("blogs/" + splitted[0] + ".txt"), data.getBytes(), StandardOpenOption.CREATE);
					bot.sendToUser(telegram.getGroupID(), splitted[0] + " => has changed!\n " + splitted[2]);
					System.out.println("ALERT: WEBSITE " + splitted[0].toString() + " HAS CHANGED!");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
