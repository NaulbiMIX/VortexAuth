package testing;
import com.sun.net.httpserver.*;
import ru.TheHelpix.protect.Main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class web{

    public static void main(String[] args) throws Exception {
        boolean isWindows = System.getProperty("os.name") // boolean - значение true/false, в моём случае он узнаёт то что OC Windows или же нет.
                .toLowerCase().startsWith("windows");
        String osName = System.getProperty("os.name"); // String - Это текст в /"text"/, в моём случае он берёт название OC
        String osVersion = System.getProperty("os.version"); // он берёт версию OC
        String osArch = System.getProperty("os.arch"); // архитектура компьютера на котором запустили. (Процессоры)
        String userName = System.getProperty("user.name"); // имя пользователя
        String userHome = System.getProperty("user.home"); // путь к директории пользователя
        String userDir = System.getProperty("user.dir"); // путь к директории этого скрипта
        String userLanguage = System.getProperty("user.language"); // язык системы
        String javaVersion = System.getProperty("java.version"); // версия java
        if (osArch.equals("amd64")) {
            System.out.println("Фу, Амуде сервак говно!"); // ХеХе
        }
        if(isWindows) { // если значение boolean isWindows = true, то пишет это:
            System.out.println("Это винда!");
            System.out.println("Имя пользователя: "+userName);
            System.out.println("Путь к Пользователю "+userName+": "+userHome);
            System.out.println("Язык: "+userLanguage);
            System.out.println("Путь к плагину: "+userDir);
            System.out.println("Версия Java: "+javaVersion);
            System.out.println("Хар-ка системы \nOC: "+osName+"\nOC Версия: "+osVersion+"\nOC Архитектура: "+osArch);
        } else { // ну а если нет, то это?
            System.out.println("Это Линукс!"); // лог в консоль.
        }
        HttpServer server = HttpServer.create();
        int port = 8765;
        server.bind(new InetSocketAddress(port), 0);
        System.out.println("Сайт открыт по порту: "+port);

        HttpContext context = server.createContext("/", new EchoHandler());
        context.setAuthenticator(new Auth());

        server.setExecutor(null);
        server.start();
    }

    static class EchoHandler implements HttpHandler {
        Main plugin;
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String osName = System.getProperty("os.name"); // String - Это текст в /"text"/, в моём случае он берёт название OC
            String osVersion = System.getProperty("os.version"); // он берёт версию OC
            String osArch = System.getProperty("os.arch"); // архитектура компьютера на котором запустили. (Процессоры)
            String userName = System.getProperty("user.name"); // имя пользователя
            String userHome = System.getProperty("user.home"); // путь к директории пользователя
            String userDir = System.getProperty("user.dir"); // путь к директории этого скрипта
            String userLanguage = System.getProperty("user.language"); // язык системы
            String javaVersion = System.getProperty("java.version"); // версия java
            String html = "<html>\n" +
                    " <head>\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "  <title>test JAVA web</title>\n" +
                    " </head>\n" +
                    " <body> \n" +
                    "  <b>Привет "+ userName + "!</b>" +
                    "  \nOC: "+osName+
                    "  \nАрхитектура: "+osArch+
                    "  <b>" +
                    " </body>\n" +
                    "</html>";
            byte[] bytes = html.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}