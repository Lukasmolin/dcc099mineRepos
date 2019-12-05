package minerepo.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import minerepo.runtime.CLIExecute;
import minerepo.runtime.CLIExecution;

public class GitRepoDownloader {

    private String repoName;
    private String originUrl;
    private String folderPath;
    private List<CLIExecution> executions = new ArrayList<>();

    private GitRepo self;

    public GitRepoDownloader(String originUrl, String path){
        this.originUrl = originUrl;
        this.folderPath = path;
        setRepoName();
    }

    private void setRepoName(){
        //Splits url to get repository name
        String[] arr = this.originUrl.split("/");
        String repoName = arr[arr.length-1];
        repoName = repoName.replace(".git", "");
        this.repoName = repoName;
    }

    /**
     * Tries to clone repository from origin to local
     * @return false if CLI returns any error
     * 
     * @throws IOException
     */
    public boolean download() throws IOException{
        var execution = CLIExecute.execute("git clone " + originUrl, folderPath);
        executions.add(execution);
        return checkSucess();
    }

    public String getRepoName(){
        return this.repoName;
    }

    public CLIExecution getLastExecution(){
        int index = executions.size() - 1;
        if(index >= 0) return executions.get(index);
        return null;
    }

    public List<CLIExecution> getExecutions(){
        return executions;
    }

    /**
     * 
     * @return true if the CLI did not output any error and the branch is up to date and
     * the working tree is clean
     * @throws IOException 
     */
    public boolean checkSucess() throws IOException{
        boolean isUpToDate = false;
        boolean workingTreeClean = false;
        var execution = CLIExecute.execute("git status", this.folderPath+"//"+this.repoName);
        var stdOut = execution.getOutput(); 
        var errOut = execution.getError();
        executions.add(execution);

        if(errOut.size() != 0){
            return false;
        }

        for(int i = 0; i < stdOut.size() && (!isUpToDate || !workingTreeClean); i++){
            if(!isUpToDate && stdOut.get(i).contains("branch is up to date")){
                isUpToDate = true;
            }
            if(!workingTreeClean && stdOut.get(i).contains("working tree clean")){
                workingTreeClean = true;
            }
        }
        
        return isUpToDate && workingTreeClean;
    }

    /**
     * Return an instance of the downloaded repository
     * @return instance of the downloaded git repo
     */
    public GitRepo getAsRepo(){
        if(this.self == null){
            this.self = new GitRepo(this.folderPath+"//"+this.repoName, this.repoName);
        }
        return self;
    }

}
