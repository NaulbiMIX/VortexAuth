package ru.TheHelpix.protect;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.plugin.java.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.plugin.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import ru.TheHelpix.protect.utils.AdvancedLicense;
import ru.TheHelpix.protect.utils.Logger;
import ru.TheHelpix.protect.utils.Metrics;
import testing.web;

public class Main extends JavaPlugin implements Listener, Runnable {

    public static Main instance;

    Connection connection;
    HashMap<Player, Integer> login;
    Main helper;
    private static Map<String, String> codes;
    public static List<String> gived;

    public Main() {
        this.login = new HashMap<Player, Integer>();
        this.helper = this;
    }
    public void kickPlayer(Player p, String reason) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                p.getPlayer().kickPlayer(reason);
            }
        });
    }

    public void getCmd(String cmd) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        });
    }


    private static final Logger LOG = new Logger("hAuth");
    public void onEnable() {
        int pluginId = 8772; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        /*
        String cmd = "dir";
        try {
            final Process process =  Runtime.getRuntime().exec(String.join(" ", cmd));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                getLogger().info("Ответ ебать: "+line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        /*
        int prop = getProp("rcon-port");
        if (prop < 1) {
            LOG.error("Ошибка в поиске rcon-port!");
        } else {
            LOG.info("Ркон порт: "+prop);
        }

        String pass = getPass("rcon-password");
        if (pass == null) {
            LOG.error("Ошибка в поиске rcon-password!");
        } else {
            LOG.error("Ркон пароль: "+pass);
        }
        */
        instance = this;
        if(!new AdvancedLicense("C8GC-UZS9-QXLF-2SZL", this).register()) return;
        getCommand("cbc").setExecutor(new hCommand(this));

        getCommand("hstream").setExecutor(new hStream(this));
        getCommand("apicmd").setExecutor(new apiCmd(this));
        getCommand("vk_debug").setExecutor(new vkdebugCommand(this));
        getCommand("hprotect").setExecutor(new hConsoleCmd(this));
        try {
            openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        createTable();
        codes = Maps.newHashMap();
        System.out.println("___________________");
        System.out.println(codes);
        System.out.println("___________________");
        this.login.clear();
        this.saveDefaultConfig();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.getPlayer().isOp() || p.getPlayer().hasPermission("litebans.*")) {
                if (this.login.containsKey(p.getPlayer()) && this.login.get(p.getPlayer()) == 0) {
                    sendCode(p);
                }
            }
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin) this, (Runnable) this.helper, 40L, 40L);
        Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) this.helper);
        getLogger().info("Плагин включён!");
    }

    public static Main getInstance() {
        return instance;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + getConfig().getString("mysql.host") + ":" + getConfig().getInt("mysql.port") + "/" + getConfig().getString("mysql.database"), getConfig().getString("mysql.user"), getConfig().getString("mysql.password"));
        }
    }

    private void createTable() {
        try (PreparedStatement st = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players(name VARCHAR(30) NOT NULL UNIQUE, id INT NOT NULL);")) {
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendCode(Player p) {
        InetAddress ip = p.getPlayer().getAddress().getAddress();
        if (p.getPlayer().isOp() || p.getPlayer().hasPermission("litebans.*")) {
            if (getAddmin(p.getPlayer()) > 1) {
                for (final Player p1 : Bukkit.getOnlinePlayers()) {
                    if (p1.isOp() || p1.hasPermission("litebans.*")) {
                        p1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Администратор &6" + p.getPlayer().getName() + "&2 зашёл на сервер."));
                    }
                }
                String randomCode = RandomStringUtils.randomNumeric(getConfig().getInt("vk.lengt"));
                codes.put(p.getPlayer().getName(), randomCode);

                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&user_id=" + getAdmin(p.getPlayer()) + "&message=" + "Твой код: " + randomCode + "\n\nПривет, " + "[id" + getAdmin(p.getPlayer())+ "|" + p.getPlayer().getName() + "]" + "!\nКто-то зашёл с вашего аккаунта на режим: " + getConfig().getString("vk.server") + ".\nip: " + ip + "&v=5.71");
                //HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&user_id=" + getAdmin(event.getPlayer()) + "&message=" + "Твой код: " + randomCode + "\n\nПривет, " + event.getPlayer().getName() + "!\nКто-то зашёл с вашего аккаунта на режим: " + getConfig().getString("vk.server") + ".\nip: " + ip + "&v=5.71");
                this.login.put(p.getPlayer(), 0);
                getLogger().info("id: " + getAdmin(p.getPlayer()) + " ник: " + p.getPlayer().getName());
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.login_chat") + "&message=" + "[id" + getAdmin(p.getPlayer())+ "|" + p.getPlayer().getName() + "]" + " зашёл на сервер: " + getConfig().getString("vk.server") + "." + "\nip: " + ip + "&v=5.71");
            } else {
                kickPlayer(p, getConfig().getString("texts.kick").replace("&", "§"));
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.not_pass_chat") + "&message=" + "Попытка входа. Ник: " + p.getPlayer().getName() + "\nСервер: " + getConfig().getString("vk.server") + ".\nip: " + ip + "\n@sayhe" + "&v=5.71");
                getLogger().info("Попытка входа, ник: " + p.getPlayer().getName());
                for (final Player pp : Bukkit.getOnlinePlayers()) {
                    if (pp.getPlayer().isOp() || pp.getPlayer().hasPermission("litebans.*")) {
                        if (this.login.get(pp) != 0) {
                            pp.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &4Попытка входа ник: &6" + p.getPlayer().getName() + "&4 ip: " + ip));
                        }
                    }
                }
            }
        }
    }

    public int getProp(String string) {
        int inta = 0;

        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            is.close();
            if (props.getProperty(string) != null) {
                inta = Integer.parseInt(props.getProperty(string));
            } else {
                return inta;
            }
        } catch (IOException e) {
            inta = -1;
        }
        return inta;
    }

    public String getPass(String string) {
        String pass = null;

        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            is.close();
            if (props.getProperty(string) != null) {
                pass = props.getProperty(string);
            } else {
                return pass;
            }
        } catch (IOException e) {
            pass = null;
        }
        return pass;
    }

    public String hash(String hash) {
        String md5Hex = DigestUtils.md5Hex(hash);
        return md5Hex;
    }

    public void addUser(String p, String password) {
        try (PreparedStatement st = connection.prepareStatement("INSERT INTO panelUsers(player, userToken, userAccess) VALUES (?, ?, ?);")) {
            st.setString(1, p);
            st.setString(2, hash(p+password));
            st.setInt(3, 9999);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAdmin(String p, int id) {
        try (PreparedStatement st = connection.prepareStatement("INSERT INTO players (name, id) VALUES (?, ?)")) {
            st.setString(1, p);
            st.setInt(2, id);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAdmin(String p) {
        try(PreparedStatement st = connection.prepareStatement("DELETE FROM players WHERE name=?")) {
            st.setString(1, p);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getad(String p) {
        try (PreparedStatement st = connection.prepareStatement("SELECT id FROM players WHERE name = ? LIMIT 1")) {
            st.setString(1, p);
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    return set.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getadmins(int p) {
        try (PreparedStatement st = connection.prepareStatement("SELECT id FROM players WHERE id = ? LIMIT 1")) {
            st.setString(1, String.valueOf(p));
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    return set.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAdmin(Player p) {
        try (PreparedStatement st = connection.prepareStatement("SELECT id FROM players WHERE name = ? LIMIT 1")) {
            st.setString(1, p.getName());
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    return set.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAddmin(Player p) {
        try (PreparedStatement st = connection.prepareStatement("SELECT id FROM players WHERE name = ? LIMIT 1")) {
            st.setString(1, p.getName());
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    return set.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean checkCode(Player p, String code) {
        return codes.containsKey(p.getName()) && codes.get(p.getName()).equals(code);
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks((Plugin) this);
        this.login.clear();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLogger().info("Плагин выключен!");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBreak(final BlockBreakEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlace(final BlockPlaceEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void AsyncPlayerChatEvent(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        InetAddress ip = player.getAddress().getAddress();
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            if (checkCode(event.getPlayer(), event.getMessage())) {
                event.setCancelled(true);
                this.login.put(event.getPlayer(), 1);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("texts.success")));
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                Date date = new Date();
                System.out.println(codes);
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&user_id=" + getAdmin(event.getPlayer()) + "&message=" +"[id"+getAdmin(event.getPlayer())+"|"+ event.getPlayer().getName()+"]"+"\nРежим: " + getConfig().getString("vk.server") + ".\nip: " + ip +"\n["+dateFormat.format(date)+"]"+ "&v=5.71");
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.not_pass_chat") + "&message=" + "[id" + getAdmin(event.getPlayer())+ "|" + event.getPlayer().getName() + "]" + " авторизовался. \nСервер: " + getConfig().getString("vk.server") + "." + "\nip: " + ip + "&v=5.71");
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp() || p.hasPermission(getConfig().getString("permission"))) {
                        if (this.login.get(p) != 0) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Администратор &6" + event.getPlayer().getName() + "&2 авторизовался."));
                        }
                    }
                }
                getLogger().info("Администратор " + event.getPlayer().getName() + " авторизовался");
                return;
            }
            if (!checkCode(player, event.getMessage())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("texts.no_pass")));
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.not_pass_chat") + "&message=" + "[id" + getAdmin(event.getPlayer())+ "|" + event.getPlayer().getName() + "]" + " ввёл не правильно код. \nСервер: " + getConfig().getString("vk.server") + "." + "\nip: " + ip + "&v=5.71");
                getLogger().info("Администратор " + event.getPlayer().getName() + " ввёл не правильный код");
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp() || p.hasPermission(getConfig().getString("permission"))) {
                        if (this.login.get(p) != 0) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &cАдминистратор &6" + event.getPlayer().getName() + "&c ввёл не правильный код."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void OnJoin(final PlayerJoinEvent event) {
        InetAddress ip = event.getPlayer().getAddress().getAddress();
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("litebans.*")) {
            if (getAddmin(event.getPlayer()) > 1) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp() || p.hasPermission("litebans.*")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Администратор &6" + event.getPlayer().getName() + "&2 зашёл на сервер."));
                    }
                }
                String randomCode = RandomStringUtils.randomNumeric(getConfig().getInt("vk.lengt"));
                codes.put(event.getPlayer().getName(), randomCode);
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&user_id=" + getAdmin(event.getPlayer()) + "&message=" + "Твой код: " + randomCode + "\n\nПривет, " +  "[id" + getAdmin(event.getPlayer())+ "|" + event.getPlayer().getName() + "]" +  "!\nКто-то зашёл с вашего аккаунта на режим: " + getConfig().getString("vk.server") + ".\nip: " + ip + "&v=5.71");
                this.login.put(event.getPlayer(), 0);
                getLogger().info("id: " + getAdmin(event.getPlayer()) + " ник: " + event.getPlayer().getName());
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.login_chat") + "&message=" + "[id" + getAdmin(event.getPlayer())+ "|" + event.getPlayer().getName() + "]" + " зашёл на сервер: " + getConfig().getString("vk.server") + "." + "\nip: " + ip + "&v=5.71");
                System.out.println(codes);
            } else {
                event.getPlayer().kickPlayer(getConfig().getString("texts.kick").replace("&", "§"));
                HTTP.post("https://api.vk.com/method/messages.send", "access_token=" + getConfig().getString("vk.token") + "&peer_id=" + getConfig().getInt("vk.not_pass_chat") + "&message=" + "Попытка входа. Ник: " + event.getPlayer().getName() + "\nСервер: " + getConfig().getString("vk.server") + ".\nip: " + ip + "\n@sayhe" + "&v=5.71");
                getLogger().info("Попытка входа, ник: " + event.getPlayer().getName());
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().isOp() || event.getPlayer().hasPermission("litebans.*")) {
                        if (this.login.get(p) != 0) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &4Попытка входа ник: &6" + event.getPlayer().getName() + "&4 ip: " + ip));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void DropItem(final PlayerDropItemEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void OnLEave(final PlayerQuitEvent event) {
        if (this.login.containsKey(event.getPlayer())) {
            this.login.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void OnKick(final PlayerKickEvent event) {
        if (this.login.containsKey(event.getPlayer())) {
            this.login.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void Command(final PlayerCommandPreprocessEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onMove(final PlayerMoveEvent event) {
        if (this.login.containsKey(event.getPlayer()) && this.login.get(event.getPlayer()) == 0) {
            event.setCancelled(true);
        }
    }

    public void run() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.getPlayer().isOp() || p.getPlayer().hasPermission("litebans.*")) {
                if (!this.login.containsKey(p)) {
                    this.login.put(p, 0);
                    sendCode(p);
                }
                if (this.login.get(p) != 0) {
                    continue;
                }
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendMessage("\n");
                p.sendTitle(getConfig().getString("texts.title").replace("&", "§"), getConfig().getString("texts.title2").replace("&", "§"));
            }
        }
    }
}
