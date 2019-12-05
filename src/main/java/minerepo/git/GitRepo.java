package minerepo.git;

import java.io.IOException;
import minerepo.runtime.RuntimeWrapper;
import minerepo.runtime.CLIExecution;

public class GitRepo extends RuntimeWrapper{

    private String name;

    public GitRepo(String repoFolderPath, String name){
        super(repoFolderPath);
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public CLIExecution checkoutTo(String destination) throws IOException{
        var exec = super.executeCommand("git checkout "+destination);
        if(!exec.getError().isEmpty()){
            exec.getOutput().addAll(exec.getError());
            exec.getError().clear();
        }
        return exec;
    }

    public CLIExecution revListCommits(String params) throws IOException{
        return super.executeCommand("git rev-list "+params);
    }

    public CLIExecution log(String params) throws IOException{
        return super.executeCommand("git log "+params);
    }

    public CLIExecution show(String params, String target) throws IOException{
        return super.executeCommand("git show "+params+" "+target);
    }
}
