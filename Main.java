import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    private static String basePath;
    private static String project;
    private static String cookie;
    private static final String outputPath = "output";


    public static void main(String[] args) throws IOException, InterruptedException {
        basePath = args[0];
        project = args[1];

        // Read cookie from file
        cookie = Files.readString(new File("cookie").toPath());

        List<String> wikiPages = listWikiPages();
        for (String wikiPage : wikiPages) {
            System.out.println("Exporting wiki page: " + wikiPage);
            String content = fetchPage(wikiPage);
            File outputFile = new File(outputPath, wikiPage.substring(1) + ".md");
            outputFile.getParentFile().mkdirs();
            Files.writeString(outputFile.toPath(), content);
        }
    }

    private static List<String> listWikiPages() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(basePath + "/projects/" + project + "/wiki/wiki"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("Cookie", cookie)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractPagesFromMainPage(response.body());
            } else {
                System.out.println("Failed to fetch wiki pages: " + response.statusCode());
            }
        }
        System.exit(1);
        return Collections.emptyList();
    }

    private static List<String> extractPagesFromMainPage(String mainPage) {
        return Arrays.stream(mainPage.split("\n"))
                .filter(line -> line.startsWith("<li class=\"-hierarchy-expanded op-uc-list--item\"><span class=\"tree-menu--item\" slug=\""))
                .map(line -> line.split("href=\"")[1].split("\"")[0])
                .toList();
    }

    private static String fetchPage(String wikiPage) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(basePath + wikiPage + ".markdown"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("Cookie", cookie)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Failed to fetch wiki page: " + response.statusCode());
                throw new RuntimeException("Failed to fetch wiki page: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to export wiki page", e);
        }
    }

}