package win.skademaskinen.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    private static String path = "files/log.log";

    public static void appendLog(Loggable logEntry){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path), true))){
            writer.write(logEntry.build());
            writer.newLine();
            writer.flush();
        }
        catch(IOException e){
            Colors.exceptionHandler(e);
        }
    }
}
