package minerepo.analizo;

import java.io.IOException;
import java.nio.file.FileSystems;

import minerepo.runtime.CLIExecution;
import minerepo.runtime.RuntimeWrapper;

public class Analizo extends RuntimeWrapper {

    public Analizo(String folderPath){
        super(folderPath);
    }

    public CLIExecution metrics() throws IOException{
        return super.executeCommand("analizo metrics "+super.getPath());
    }

    public CLIExecution metrics(String subFolderTarget) throws IOException{
        String desiredPath = FileSystems.getDefault().getPath(super.getPath(), subFolderTarget).toString();
        return super.executeCommand("analizo metrics "+desiredPath);
    }
    
}