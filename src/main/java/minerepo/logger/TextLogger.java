package minerepo.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TextLogger {

    private Path folderPath;
    private String fileName;
    private File file;
    private FileWriter fw;
    private BufferedWriter writer;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 

    public TextLogger(String folderPath, String fileName){
        this.fileName = fileName;
        this.folderPath = FileSystems.getDefault().getPath(folderPath);
        this.file = FileSystems.getDefault().getPath(folderPath.toString(), fileName).toFile();
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getPath(){
        return folderPath.toString();
    }

    public boolean isFileCreated(){
        return this.file.exists();
    }

    public void writeLine(String txt) throws IOException{
        try {
            fw = new FileWriter(file, true);
            writer = new BufferedWriter(fw);
            writer.append(txt);
            writer.newLine();
            writer.flush();            
        } catch (IOException ex){
            throw ex;
        } finally {
            if(writer != null)
                writer.close();

            if(fw != null)
                fw.close();
        }
        writer = null;
        fw = null;
    }

    public void log(String log) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = dateFormatter.format(now);
        this.writeLine(dateTime + ":: " +log);
    }

    public void writeLines(List<String> txt) throws IOException{
        try {
            fw = new FileWriter(file, true);
            writer = new BufferedWriter(fw);
            for(String line : txt){
                writer.append(line);
                writer.newLine();
            }            
            writer.flush();           
        } catch (IOException ex){
            throw ex;
        } finally {
            if(writer != null)
                writer.close();

            if(fw != null)
                fw.close();
        }
        writer = null;
        fw = null;
    }
}