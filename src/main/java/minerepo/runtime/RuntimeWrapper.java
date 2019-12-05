package minerepo.runtime;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class RuntimeWrapper {

    private List<CLIExecution> executions = new ArrayList<>();
    private Path path;

    public RuntimeWrapper(String folderPath){
        this.path = FileSystems.getDefault().getPath(folderPath);
    }

    public CLIExecution executeCommand(String command) throws IOException{
        if(!Files.exists(path))
            throw new IOException("Path does not exist");
        
        if(!Files.isDirectory(path)) 
            throw new IOException("Path is not a directory");
        
        CLIExecution exec = CLIExecute.execute(command, path.toString());
        
        //TEMP
        exec.getOutput().addAll(exec.getError());
        exec.getError().clear();
        executions.add(exec);
        return exec;
    }

    public String getPath(){
        return this.path.toString();
    }

    public List<CLIExecution> getExecutions(){
        return this.executions;
    }

}