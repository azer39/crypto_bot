package de.azer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class functions {

	public static void addInvestment(String coin, String value) throws IOException {
		File investFile = new File("invests/" + coin + ".txt");
		investFile.createNewFile();

		String investData = coin + " " + value + " " + getPrice(coin) + " " + java.time.LocalDate.now();
		Files.write(Paths.get("invests/" + coin + ".txt"), investData.getBytes(), StandardOpenOption.CREATE);
	}

	public static void editItem(String file, String item, String value) throws IOException {
		List<String> data = Files.readAllLines(Paths.get(file));
		List<String> newData = new ArrayList<String>();

		String edited = "";
		for (String line : data) {
			if (!line.contains(item)) {
				newData.add(line);
			} else {
				if (file == "cfg.txt") {
					edited = line.split(":")[0] + ":" + value;
				} else if (file == "blogs.txt") {
					edited = line.split(" ")[0] + " " + value + " " + line.split(" ")[2];
				} else {
					System.out.println("Fatal edit error!");
				}
			}
			newData.add(edited);
		}
	}

	public static String getAllTimeHigh(String suffix) throws MalformedURLException {
		String data = getHTTPSData(new URL("https://coinmarketcap.com/currencies/" + suffix + "/"));
		String buffer = data.substring(data.indexOf("All Time High</div><div><div"));

		buffer = buffer.substring(buffer.indexOf("$"), buffer.indexOf("U"));
		buffer.replace(" ", "");

		return buffer;
	}

	public static String getAverage(String coin) throws IOException {

		File coinfile = new File("data/" + coin + ".txt");
		Float res = (float) 0;
		String ret = "";

		if (coinfile.exists()) {

			List<String> data = Files.readAllLines(Paths.get("data/" + coin + ".txt"));

			if (data.size() < 3000) {
				for (String l : data) {
					String value = l.split("/")[1].replace("$", "").replace(",", "");
					res += Float.valueOf(value);
				}
				res = res / data.size();
				ret = res.toString();

			} else {
				for (int i = data.size() - 3000; i < data.size(); i++) {
					String value = data.get(i).split("/")[1].replace("$", "").replace(",", "");
					res += Float.valueOf(value);
					res = res / data.size();
					ret = res.toString();
				}
			}
		} else {
			ret = "ERROR: Coin does not exist yet!";
		}
		return ret;
	}

	public static String getChangelog() throws IOException {

		String changelog = 
				  "v 0.1 \n" 
				+ "Added: \n" 
				+ "      /stop\n" 
				+ "      /list\n" 
				+ "      /prices\n"
				+ "      /changelog\n" 
				+ "      /average\n" 
				+ "      /remove\n" 
				+ "      /site\n" 
				+ "      /edit\n"
				+ "      /register\n" 
				+ "v0.2 \n"
				+ "Added: \n" 
				+ "      /list update\n"
				+ "      Telegram Token File \"telegram.txt\"" + "";

		return changelog;
	}

	public static String getHTTPSData(URL uurl) {
		String ret = null;

		try {
			HttpsURLConnection con = (HttpsURLConnection) uurl.openConnection();

			con.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			BufferedReader bf = new BufferedReader(isr);

			String inputline;
			while ((inputline = bf.readLine()) != null) {
				ret += inputline;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static String getInvestPrice(String coin) throws IOException {

		File investFile = new File("invests/" + coin + ".txt");
		String result = "";

		if (investFile.exists()) {
			List<String> fileData = Files.readAllLines(Paths.get("invests/" + coin + ".txt"));
			for (String line : fileData) {
				String oldValue = line.split(" ")[2].replace("$", "").replace(",", "");
				String newValue = getPrice(coin).replace("$", "").replace(",", "");

				Float F_res = (float) round(Float.valueOf(newValue) - Float.valueOf(oldValue), 2);
				F_res = F_res * Float.valueOf(line.split(" ")[1]);
				if (F_res > 0) {
					result = coin + " => positive (" + F_res.toString() + "$) since: " + line.split(" ")[3];
				} else {
					result = coin + " => negative (-" + F_res.toString() + "$) since: " + line.split(" ")[3];
				}
			}
		} else {
			result = "NaN";
		}
		return result;
	}

	public static Float getPercent(Float a, Float b) {
		Float diff = (float) 0;
		Float c = a - b;

		if (c > 0) {
			// Value higher than average
			diff = b / a;
			diff = (1 - diff) * 100;
		} else {
			// Value lower than average
			c = c * (-1);
			diff = a / b;
			diff = (1 - diff) * 100;
		}
		return diff;
	}

	public static String getPrice(String suffix) throws MalformedURLException {
		String data = getHTTPSData(new URL("https://coinmarketcap.com/currencies/" + suffix + "/"));

		String buffer = data.substring(data.indexOf("class=\"cmc-details-panel-price__price\""));

		buffer = buffer.substring(buffer.indexOf(">") + 1, buffer.indexOf("<"));

		return buffer;
	}

	public static String getTelegramToken(String file) throws IOException {
		List<String> data = Files.readAllLines(Paths.get(file));
		String token = "";
		for (String line : data) {
			if (line.startsWith("Token")) {
				token = line.split("-")[1];
			} else {
				System.out.println("Token not found!");
			}
		}
		return token;
	}

	public static void initialize() throws IOException {
		String[] dirs = { "data", "blogs", "invests" };
		String[] files = { "telegram.txt", "cfg.txt", "blogs.txt", "invests.txt" };

		for (String el : files) {
			File newFile = new File(el);
			newFile.createNewFile();
		}
		for (String f : dirs) {
			Files.createDirectories(Paths.get(f));
		}

		List<String> ffiles = Files.readAllLines(Paths.get("cfg.txt"));

		for (String e : ffiles) {
			String[] tmp = e.split(":");
			File temp = new File("data/" + tmp[0] + ".txt");
			temp.createNewFile();
		}
	}

	public static void registerNewCoin(String name, String value) throws IOException {
		String data = name + ":" + value + "\n";

		Files.write(Paths.get("cfg.txt"), data.getBytes(), StandardOpenOption.APPEND);
	}

	public static void removeItem(String item, String file) throws IOException {
		List<String> data = Files.readAllLines(Paths.get(file));

		List<String> newData = new ArrayList<String>();
		for (String line : data) {
			if (!line.contains(item)) {
				newData.add(line);
			}
		}
		Files.delete(Paths.get(file));
		File f = new File(file);
		f.createNewFile();
		for (String line : newData) {
			Files.write(Paths.get(file), line.getBytes(), StandardOpenOption.APPEND);
		}
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
