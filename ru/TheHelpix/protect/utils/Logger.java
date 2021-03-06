package ru.TheHelpix.protect.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private final String module;

    public Logger(String module){
        this.module = module;
    }

    public String getTime() {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return formatForDateNow.format(new Date());
    }

    public Logger core(String text){System.out.println("\033[34m["+getTime()+" CORE]: \033[34m["+this.module+"]\033[0m \033[34m" + text + "\033[0m"); return this;}
    public Logger info(String text){System.out.println("\033[34m[" + this.module + "]\033[0m " + text); return this;}
    public Logger warn(String text){System.out.println("\033[33m\033[5m["+getTime()+" WARN]\033[0m: \033[34m[" + this.module + "]\033[0m " + text); return this;}
    public Logger error(String text){System.out.println("\033[31m[" + this.module + "]\033[31m " + text + "\033[0m"); return this;}

}