package net.kodehawa.mantarobot.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.monoid.web.Resty;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	private static final Logger LOGGER = LoggerFactory.getLogger("Utils");

	/**
	 * Capitalizes the first letter of a string.
	 *
	 * @param s the string to capitalize
	 * @return A string with the first letter capitalized.
	 */
	public static String capitalize(String s) {
		if (s.length() == 0) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Capitalizes each first letter after a space.
	 *
	 * @param original the string to capitalize.
	 * @return a string That Looks Like This. Useful for titles.
	 */
	public static String capitalizeEachFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}

		String[] words = original.split("\\s");
		StringBuilder builder = new StringBuilder();
		for (String s : words) {
			builder.append(capitalize(s)).append(" ");
		}
		return builder.toString();
	}

	public static String getDurationMinutes(long length) {
		return String.format("%d:%02d minutes",
			TimeUnit.MILLISECONDS.toMinutes(length),
			TimeUnit.MILLISECONDS.toSeconds(length) -
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
		);
	}

	/**
	 * Fetches an Object from any given URL. Uses vanilla Java methods.
	 * Can retrieve text, JSON Objects, XML and probably more.
	 *
	 * @param url   The URL to get the object from.
	 * @param event
	 * @return The object as a parsed UTF-8 string.
	 */
	public static String getObjectFromUrl(String url, GuildMessageReceivedEvent event) {
		String webobject = null;
		try {
			URL ur1 = new URL(url);
			HttpURLConnection ccnn = (HttpURLConnection) ur1.openConnection();
			ccnn.setRequestProperty("User-Agent", "Mantaro");
			InputStream ism = ccnn.getInputStream();
			webobject = CharStreams.toString(new InputStreamReader(ism, Charsets.UTF_8));
		} catch (Exception e) {
			LOGGER.warn("Seems like I cannot fetch data from " + url, e);
			event.getChannel().sendMessage("\u274C Error retrieving data from URL.").queue();
		}

		return webobject;
	}

	public static Iterable<String> iterate(Pattern pattern, String string) {
		return () -> {
			Matcher matcher = pattern.matcher(string);
			return new Iterator<String>() {
				@Override
				public boolean hasNext() {
					return matcher.find();
				}

				@Override
				public String next() {
					return matcher.group();
				}
			};
		};
	}

	public static String paste(String toSend) {
		try {
			String pasteToken = Unirest.post("https://hastebin.com/documents")
					.header("User-Agent", "Mantaro")
					.header("Content-Type", "text/plain")
					.body(toSend)
					.asJson()
					.getBody()
					.getObject()
					.getString("key");
			return "https://hastebin.com/" + pasteToken;

		} catch (UnirestException e) {
			LOGGER.warn("Hastebin is being funny, huh? Cannot send or retrieve paste.", e);
			return "Bot threw ``" + e.getClass().getSimpleName() + "``" + " while trying to upload paste, check logs";
		}
	}

	/**
	 * Same than above, but using resty. Way easier tbh.
	 *
	 * @param url   The URL to get the object from.
	 * @param event JDA message event.
	 * @return The object as a parsed string.
	 */
	public static String restyGetObjectFromUrl(String url, GuildMessageReceivedEvent event) {
		String url2 = null;
		try {
			Resty resty = new Resty();
			resty.identifyAsMozilla();
			url2 = resty.text(url).toString();
		} catch (IOException e) {
			LOGGER.warn("[Resty] Seems like I cannot fetch data from " + url, e);
			event.getChannel().sendMessage("\u274C Error retrieving data from URL [Resty]").queue();
		}

		return url2;
	}

	public static String toPrettyJson(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(jsonString);
		return gson.toJson(jsonElement);
	}
}