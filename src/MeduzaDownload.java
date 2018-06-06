import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MeduzaDownload {
    private static final int BUFFER_SIZE = 1024 * 32;
    private static final String DOWNLOAD_PATH = "d:\\meduza\\";

    public static void main(String[] args) throws Exception {
        String[] urls = {"meduza-v-kurse", "delo-sluchaya", "tekst-nedeli", "kak-zhit", "dva-po-tsene-odnogo"};
        String base = "https://meduza.io/";
        Pattern mp3link = Pattern.compile("href=\"([A-z0-9\\-/]+\\.mp3\\?client=native)");
        for (String url : urls) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new URL(base + "podcasts/" + url).openStream())))) {
                while (reader.ready()) {
                    String html = reader.readLine();
                    Matcher m = mp3link.matcher(html);
                    while (m.find()) {
                        String group = m.group(1);
                        String fileName = group.substring(group.lastIndexOf("/"), group.lastIndexOf("?"));
                        String path = DOWNLOAD_PATH + url;
                        File file = new File(path + fileName);
                        if (!file.exists()) {
                            new File(path).mkdirs();
                            try (FileOutputStream fileOutputStream = new FileOutputStream(path + fileName);
                                 InputStream mp3Stream = new URL(base + group).openStream()) {
                                System.out.println("Downloading '" + base + group + "' to '" + path + fileName + "'...");
                                copyStream(mp3Stream, fileOutputStream);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            int n = in.read(buf);
            if (n == -1) {
                break;
            }
            out.write(buf, 0, n);
        }
    }

}
