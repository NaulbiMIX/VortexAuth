package ru.TheHelpix.protect;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonStreamParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.Consumer;

public class License {

    private static JsonElement post(final String s) {
        JsonElement jsonRespond = new JsonNull();

        try {
            final URLConnection connect = new URL(s).openConnection();
            connect.setConnectTimeout(60000);
            connect.setDoOutput(true);
            final OutputStreamWriter os = new OutputStreamWriter(connect.getOutputStream());
            os.flush();

            final InputStream is = connect.getInputStream();
            JsonStreamParser jsonStream = new JsonStreamParser(new InputStreamReader(is));
            if(jsonStream.hasNext()) jsonRespond = jsonStream.next();

            os.close();
            is.close();
        } catch (IOException ignored) {}

        return jsonRespond;
    }

    private static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 64).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkLicense(JavaPlugin plugin, Consumer<Boolean> asyncTask) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final String key = plugin.getConfig().getString("Key");

                String message = post("http://212.109.197.44:8080/license.json")
                        .getAsJsonObject().get("message").getAsString();

                asyncTask.accept(message.equals(key));
            } catch (IllegalStateException e) {
                asyncTask.accept(false);
            }
        });
    }

    public static boolean isURLOk(String urlString) throws IOException {
        URL u = new URL(urlString);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection huc = (HttpURLConnection)u.openConnection();
        huc.setRequestMethod("HEAD");
        huc.connect();
        return (huc.getResponseCode() == 200);
    }
}
