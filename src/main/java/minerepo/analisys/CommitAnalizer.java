package minerepo.analisys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import minerepo.analizo.Analizo;
import minerepo.git.GitCommit;
import minerepo.git.GitRepo;
import minerepo.runtime.CLIExecution;;

public class CommitAnalizer {

    private GitRepo repo;
    private List<CLIExecution> executions;
    private List<Exception> exceptions;

    public CommitAnalizer(GitRepo repo, List<CLIExecution> executions, List<Exception> exceptions){
        this.repo = repo;
        this.executions = executions;
        this.exceptions = exceptions;
    }

    public AnalizedGitCommit analize(GitCommit commit, AnalizedGitCommit.Type type) throws IOException{       
        AnalizedGitCommit analized = new AnalizedGitCommit(commit);
        switch(type){
            case BUG:
                analized.setBugged();
                break;
            case CONTROL:
                analized.setControl();
                break;
            case UNSETTED:
                break;
        }
        ExecuteAnalizo(analized);
        return analized;
    }

    public List<String> getChangesPath(GitCommit commit) throws IOException{
        List<String> paths = new ArrayList<>();
        var executed = repo.show("--name-only", commit.getId());
        executions.add(executed);
        if(!executed.getError().isEmpty()) throw new IOException("CLI Execution Outputted Error"+"\nRepo: "+repo.getName()+" id: "+commit.getId());

        boolean foundAllPaths = false;
        List<String> output = executed.getOutput();
        for(int i = output.size()-1; i >= 0 && !foundAllPaths; i--){
            if(!output.get(i).isBlank() && i < output.size() - 1){
                foundAllPaths = true;
            } else {
                String path = output.get(i).trim();
                paths.add(path);
            }
        }
        return paths;
    }
    
    private void ExecuteAnalizo(AnalizedGitCommit commit) throws IOException{
        executions.add(repo.checkoutTo(commit.getId()));
        Analizo analizo = new Analizo(repo.getPath());
        String fullAnalisys = "";

        for(var path : commit.getChangesPaths()){
            var executed = analizo.metrics(path);
            executions.add(executed);
            if(!executed.getError().isEmpty()) throw new IOException("CLI Execution Outputted Error\n"+commit.toString()); 

            String localAnalisys = "\n###########################\n";
            localAnalisys += "Path: "+path;

            for(var line : executed.getOutput()){
                localAnalisys += "\n"+line;
            }
            fullAnalisys += localAnalisys + "\n";
        }

        if(!commit.setAnalisys(fullAnalisys)){
            exceptions.add(new Exception("Duplicate set Analisys\n"+commit.toString()));
        }
    }

}