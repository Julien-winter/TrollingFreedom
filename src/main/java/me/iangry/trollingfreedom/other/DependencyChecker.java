package me.iangry.trollingfreedom.other;

import me.iangry.trollingfreedom.main.Core;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads ProtocolLib into the plugins folder when it is missing.
 * The download goes to a temporary file first so a failed download never
 * leaves a broken, empty ProtocolLib.jar behind.
 *
 * <p>The old Jenkins URL on ci.dmulloy2.net now answers 403, so the file is
 * resolved through the public GitHub releases API with a fixed fallback URL.
 */
public class DependencyChecker {

    private static final String RELEASES_API =
            "https://api.github.com/repos/dmulloy2/ProtocolLib/releases/latest";
    private static final String FALLBACK_URL =
            "https://github.com/dmulloy2/ProtocolLib/releases/download/dev-build/ProtocolLib.jar";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) TrollingFreedom-DependencyChecker";
    private static final long MIN_JAR_BYTES = 100_000L;

    public static void DependencyChecker() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Bukkit.getLogger().info("§3TF§8: §7ProtocolLib dependency detected");
            return;
        }

        File pluginsDir = new File(Core.instance.getServer().getWorldContainer(), "plugins");
        if (!pluginsDir.exists() && !pluginsDir.mkdirs()) {
            Bukkit.getLogger().severe("§3TF§8: §cCould not create plugins folder, skipping ProtocolLib download");
            return;
        }

        File target = new File(pluginsDir, "ProtocolLib.jar");
        if (target.exists() && target.length() > 0) {
            Bukkit.getLogger().info("§3TF§8: §7ProtocolLib.jar found, please restart the server to enable §b§lTrolling§3§lFreedom");
            return;
        }

        File tmp = new File(pluginsDir, "ProtocolLib.jar.download");
        String url = resolveDownloadUrl();
        try {
            download(url, tmp);
            if (tmp.length() < MIN_JAR_BYTES) {
                throw new IOException("downloaded file is only " + tmp.length() + " bytes");
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("§3TF§8: §cFailed to download ProtocolLib: " + e.getMessage());
            Bukkit.getLogger().severe("§3TF§8: §cPlease install ProtocolLib manually: https://www.spigotmc.org/resources/protocollib.1997/");
            //noinspection ResultOfMethodCallIgnored
            tmp.delete();
            return;
        }

        try {
            Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Bukkit.getLogger().severe("§3TF§8: §cFailed to move ProtocolLib.jar into place: " + e.getMessage());
            //noinspection ResultOfMethodCallIgnored
            tmp.delete();
            return;
        }

        Bukkit.getLogger().info("§3TF§8: §7ProtocolLib dependency has just been installed");
        Bukkit.getLogger().info("§3TF§8: §7§lPLEASE RESTART YOUR SERVER TO ENABLE §b§lTROLLING§3§lFREEDOM");
    }

    private static String resolveDownloadUrl() {
        HttpURLConnection conn = null;
        try {
            conn = open(RELEASES_API);
            if (conn.getResponseCode() != 200) {
                return FALLBACK_URL;
            }
            String json = readAll(conn.getInputStream());
            Matcher matcher = Pattern.compile(
                    "\"name\"\\s*:\\s*\"([^\"]+\\.jar)\"[\\s\\S]*?\"browser_download_url\"\\s*:\\s*\"([^\"]+)\""
            ).matcher(json);
            String fallback = null;
            while (matcher.find()) {
                String assetName = matcher.group(1);
                String assetUrl = matcher.group(2);
                if ("ProtocolLib.jar".equals(assetName)) {
                    return assetUrl;
                }
                if (fallback == null) {
                    fallback = assetUrl;
                }
            }
            return fallback != null ? fallback : FALLBACK_URL;
        } catch (Exception e) {
            Bukkit.getLogger().warning("§3TF§8: §7Could not query ProtocolLib releases (" + e.getMessage() + "), using fallback URL");
            return FALLBACK_URL;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void download(String url, File destination) throws IOException {
        HttpURLConnection conn = open(url);
        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IOException("server returned HTTP response code: " + code);
        }
        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] data = new byte[8192];
            int read;
            while ((read = in.read(data)) != -1) {
                out.write(data, 0, read);
            }
        } finally {
            conn.disconnect();
        }
    }

    private static HttpURLConnection open(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/octet-stream, application/json, */*");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int read;
        while ((read = in.read(data)) != -1) {
            buffer.write(data, 0, read);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }
}
