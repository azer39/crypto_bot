package de.azer.telegram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import de.azer.functions;

public class telegram extends TelegramLongPollingBot {
	public static String getGroupID() {
		return "-1001357288107";
	}

	@Override
	public String getBotToken() {
		String ret = "";

		try {
			ret = functions.getTelegramToken("telegram.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public String getBotUsername() {
		return "";
	}

	@Override
	public void onUpdateReceived(Update upd) {
		System.out.println(upd.getMessage().getFrom().getFirstName() + ": " + upd.getMessage().getText());

		String messge = upd.getMessage().getText();
		String chatID = upd.getMessage().getChatId().toString();

		if (messge.startsWith("/")) {
			if (messge.toLowerCase().contains("/changelog")) {
				try {
					String send = functions.getChangelog();
					sendToUser(chatID, send);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (messge.toLowerCase().contains("/stop")) {
				System.exit(0);
			}

			// /edit «File» «Item» «Value»
			if (messge.toLowerCase().contains("/edit")) {
				String[] splitted = messge.split(" ");
				if (splitted.length == 4) {
					String item = splitted[2];
					String value = splitted[3];

					try {
						if (splitted[1].equalsIgnoreCase("cfg")) {
							functions.editItem("cfg.txt", item, value);
						} else if (splitted[1].equalsIgnoreCase("blog")) {
							functions.editItem("blogs.txt", item, value);
						} else {
							sendToUser(chatID, "Usage-Error ->\n Available Files: \n  cfg\n  blog");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /edit «File» «Item» «Value»");
				}
			}

			// /remove «File» «Item»
			if (messge.toLowerCase().contains("/remove")) {
				String[] splitted = messge.split(" ");
				if (splitted.length == 3) {
					String f = splitted[1];
					String item = splitted[2];
					try {
						if (f.equalsIgnoreCase("cfg")) {
							functions.removeItem(item, "cfg.txt");
						} else if (f.equalsIgnoreCase("blog")) {
							functions.removeItem(item, "blogs.txt");
						} else if (f.equalsIgnoreCase("invests")) {
							functions.removeItem(item, "invests.txt");
						} else {
							sendToUser(chatID, "Usage-Error ->\n Available Files: \n  cfg\n  blog\n  invests");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					sendToUser(chatID, "Item removed!");
				} else {
					sendToUser(chatID, "Usage-Error ->\n /remove «File» «Item»");
				}
			}

			// /addinvest coin value
			if (messge.contains("/addinvest")) {
				String[] ii = messge.split(" ");
				if (ii.length == 3) {
					String coin = ii[1];
					String value = ii[2];

					File ex = new File("invests/" + coin + ".txt");
					if (!ex.exists()) {
						try {
							functions.addInvestment(coin, value);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						sendToUser(chatID, "Coin already invested!");
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /addinvest «Coin» «Value»");
				}
			}

			if (messge.toLowerCase().contains("/getinvest")) {
				String[] ii = messge.split(" ");
				if (ii.length == 2) {
					String coin = ii[1];
					try {
						String messageToSend = functions.getInvestPrice(coin);
						sendToUser(chatID, messageToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /getinvest «Coin»");
				}
			}

			if (messge.toLowerCase().contains("/average")) {
				if (messge.split(" ").length == 2) {
					String coin = messge.split(" ")[1];
					try {
						String oo = functions.getAverage(coin);
						sendToUser(chatID, coin + " average => " + oo + "$");

					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /average «Coin»");
				}
			}

			if (messge.toLowerCase().contains("/list")) {

				try {
					List<String> bblogs = Files.readAllLines(Paths.get("blogs.txt"));
					List<String> ccurrencies = Files.readAllLines(Paths.get("cfg.txt"));

					String kk = "";

					if (!ccurrencies.isEmpty()) {
						kk += "CRYPTOCURRENCY: \n";
						for (String rt : ccurrencies) {
							kk += " " + rt + "\n";
						}
					}
					if (!bblogs.isEmpty()) {
						kk += "\nBLOGS: \n";
						for (String tr : bblogs) {
							kk += " " + tr + "\n";
						}
					}
					sendToUser(chatID, kk);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (messge.toLowerCase().contains("/site")) {
				/*
				 * cSite g = new cSite(); sendToUser(a, "Enter the Blockname: ");
				 */

				String[] nn = messge.split(" ");

				if (nn.length == 4) {
					try {
						List<String> jj = Files.readAllLines(Paths.get("blogs.txt"));

						List<String> names = new ArrayList<String>();
						for (String e : jj) {
							String[] rr = e.split(" ");
							names.add(rr[0]);
						}

						if (!names.contains(nn[1])) {
							String write = nn[1] + " " + nn[2] + " " + nn[3];

							Files.write(Paths.get("blogs.txt"), write.getBytes(), StandardOpenOption.APPEND);
							System.out.println(Files.readAllLines(Paths.get("blogs.txt")));
						} else {
							sendToUser(chatID, "Error: This name does already exist!");
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /site «Name» «Value» «URL»");
				}
			}

			if (messge.toLowerCase().contains("/help")) {
				String help = "HELP:\n " + "Commands:\n " + "/stop \n " + "/list \n " + "/prices \n " + "/changelog \n "
						+ "/average «Coin» \n " + "/remove «File» «Item» \n " + "/getinvest «Coin» \n "
						+ "/addinvest «Coin» «Value» \n " + "/edit «File» «Item» «Value» \n "
						+ "/site «Name» «Value» «URL» \n " + "/register «Coin» «Difference Value»";
				sendToUser(chatID, help);
			}

			if (messge.contains("/prices")) {

				String respons = "";

				try {
					List<String> fdata = Files.readAllLines(Paths.get("cfg.txt"));
					Collections.sort(fdata);
					for (String f : fdata) {
						String[] s = f.split(":");
						String price = functions.getPrice(s[0]);
						respons += s[0] + " => " + price + " [" + functions.getAllTimeHigh(s[0]) + "]" + "\n";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				sendToUser(chatID, respons);
			}

			if (messge.toLowerCase().contains("/register")) {
				String[] m = messge.split(" ");
				if (m.length == 3) {
					try {
						functions.registerNewCoin(m[1], m[2]);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					sendToUser(chatID, "Usage-Error ->\n /register «Name» «Value»");
				}
			}
		}
	}

	public void sendToUser(String chatid, String text) {
		SendMessage msg = new SendMessage().setChatId(chatid);
		msg.setText(text);

		try {
			sendMessage(msg);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
