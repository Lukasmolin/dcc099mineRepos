package minerepo.analisys;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import minerepo.App;
import minerepo.analisys.AnalizedGitCommit.Type;
import minerepo.git.GitCommit;
import minerepo.git.GitCommitBuilder;
import minerepo.git.GitRepo;
import minerepo.git.GitRepoDownloader;
import minerepo.logger.TextLogger;
import minerepo.runtime.CLIExecution;

public class IndividualScript implements Runnable{

    private String originUrl;
    private String folderPath;
    private String outputPath;
    private Path output;
    private GitRepo repo;
    private Filter<GitCommit> filter = new Filter<>();
    private int numOfCommitsToAnalize = 75;
    private List<CLIExecution> executions = new ArrayList<>();
    private List<Exception> exceptions = new ArrayList<>();
    private Status status = Status.WAITING_START;

    //temp
    private List<AnalizedGitCommit> analized;

    public IndividualScript(String originUrl, String folderPath, String outputPath){
        this.originUrl = originUrl;
        this.folderPath = folderPath;
        this.outputPath = outputPath;
    }

    public enum Status {
        WAITING_START, STARTING, DOWNLOADING, ANALAZING, PERSISTING_DATA, REMOVING_FILES, FINISHED;

        @Override
        public String toString(){
            switch(this){
                case WAITING_START: return "Waiting Start!";
                case STARTING: return "Starting!";
                case DOWNLOADING: return "Downloading";
                case ANALAZING: return "Analizing";
                case PERSISTING_DATA: return "Persisting";
                case REMOVING_FILES: return "Deleting";
                case FINISHED: return "Finished";
            }
            return "";
        }
    };

    //Selecionar Commits
    //Analizar Commits
    //Salvar analise
    //excluir

    public void addFilter(Predicate<GitCommit> filter){
        this.filter.getFilters().add(filter);
    }

    private boolean downloadRepo() throws IOException{
        status = Status.DOWNLOADING;
        var downloader = new GitRepoDownloader(originUrl, folderPath);
        if(downloader.download()){
            this.repo = downloader.getAsRepo();            
            return true;
        }
        executions.addAll(downloader.getExecutions());
        return false;
    }

    private boolean fillCommitLists(List<GitCommit> buggedCommits, List<GitCommit> controlCommits) throws IOException{
        //Executes git log command
        status = Status.ANALAZING;
        var exec = repo.log("--max-count="+numOfCommitsToAnalize);
        executions.add(exec);
        if(exec.getError().size() > 0) return false;
        
        //Converts git log output to a GitCommit List
        List<String> commitLog = exec.getOutput();
        List<GitCommit> commits = new GitCommitBuilder().buildMultipleCommits(commitLog);

        //Filters the commits based on filter functions
        List<GitCommit> filteredCommits = filter.filterAll(commits);

        //Stores index values for commits to be analized
        List<Integer> buggedCommitsIndex = new ArrayList<>();
        List<Integer> controlCommitsIndex = new ArrayList<>();
        
        //Gets the index of each filtered commit predecessor
        for(var commit : filteredCommits){
            for(int i = 0; i < commits.size()-1; i++){
                if(commits.get(i).equals(commit)){
                    buggedCommitsIndex.add(i+1);
                }
            }
        }

        //Marks the intermediate commit as control
        for(int i = 1; i < buggedCommitsIndex.size(); i++){
            int firstIndex = buggedCommitsIndex.get(i-1);
            int secondIndex = buggedCommitsIndex.get(i);
            int intermediateCommits =  secondIndex - firstIndex - 1;
            if(intermediateCommits >= 3){
                int controlIndex = (secondIndex - firstIndex) / 2;
                controlCommitsIndex.add(controlIndex);
            }
        }

        CommitAnalizer analizer = new CommitAnalizer(repo, executions, exceptions); 
        //Fills bugged commits list
        for(var index : buggedCommitsIndex){
            GitCommit bugged = commits.get(index);
            GitCommit predecessor = commits.get(index-1);
            List<String> filtered = analizer.getChangesPath(predecessor);
            bugged.addPaths(filtered);
            buggedCommits.add(bugged);
        }
        
        //Fills control commit list
        for(var index : controlCommitsIndex){
            GitCommit control = commits.get(index);
            List<String> filtered = analizer.getChangesPath(control);
            control.addPaths(filtered);
            controlCommits.add(control);
        }

        return true;
    }

    private boolean analizeCommits(List<GitCommit> buggedCommits, List<GitCommit> controlCommits, List<AnalizedGitCommit> analized) throws IOException{
        if(repo == null) return false;
        CommitAnalizer cAnalizer = new CommitAnalizer(repo, executions, exceptions);
        
        for(var commit : buggedCommits){
            AnalizedGitCommit agc = cAnalizer.analize(commit, Type.BUG);
            if(!agc.getAnalisys().isBlank())
                analized.add(agc);
            else
                exceptions.add(new Exception("Blank Commit Analisys.\n"+agc.toString()));
        }


        for(var commit : controlCommits){
            AnalizedGitCommit agc = cAnalizer.analize(commit, Type.CONTROL);
            if(!agc.getAnalisys().isBlank())
                analized.add(agc);
            else
                exceptions.add(new Exception("Blank Commit Analisys.\n"+agc.toString()));
        }

        return true;
    }

    @Override
    public void run() {
        try {
            runScript();
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    
    private void createFolder(String folderName) throws IOException{
        if(output == null){
            output = FileSystems.getDefault().getPath(outputPath + "//" + "out " + repo.getName());
            if(!Files.exists(output)){
                Files.createDirectory(output);
            }
        }

        Path p = FileSystems.getDefault().getPath(output.toString(), folderName);
        if(!Files.exists(p)){
            Files.createDirectory(p);
        }
    }

    private boolean deleteDirectory(String directoryToBeDeleted){
        Path p = FileSystems.getDefault().getPath(directoryToBeDeleted);
        if(Files.exists(p)){
            return deleteDirectory(p.toFile());
        }
        return false;
    }

    private boolean deleteDirectory(File directoryToBeDeleted){

        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private boolean persist(List<AnalizedGitCommit> commits) throws IOException{
        status = Status.PERSISTING_DATA;
        String bugFolderName = "bug", controlFolderName = "control";
        createFolder(bugFolderName);
        createFolder(controlFolderName);

        for(var c : commits){
            try {
                persistAnalizedCommit(c, bugFolderName, controlFolderName);
            } catch (Exception ex) {
                exceptions.add(ex);
            }
        }

        return true;
    }

    private boolean persistAnalizedCommit(AnalizedGitCommit commit, String bugFolderName, String controlFolderName) throws IOException{
        String select = "";
        switch(commit.getType()){
            case BUG:
                select = bugFolderName;
                break;
            case CONTROL:
                select = controlFolderName;
                break;
            default:
                return false;
        }

        String out = output.toString() + "//" + select;
        var txtOut = new TextLogger(out, commit.getId());
        
        String txt = "#COMMIT INFO#\n";
        txt += commit.toString() + "\n";        
        txt += "Paths: \n";
        for(var path : commit.getChangesPaths()){
            txt+= path + "\n";
        }
        txt += "#ANALIZED DATA#";
        txt += commit.getAnalisys();
        txt += "#END#";

        txtOut.writeLine(txt);

        return true;
    }

    public boolean runScript() throws IOException{
        status = Status.STARTING;
        List<GitCommit> buggedCommits = new ArrayList<>();
        List<GitCommit> controlCommits = new ArrayList<>();
        List<AnalizedGitCommit> analizedCommits = new ArrayList<>();

        
        if(!downloadRepo())
            return false;
        
        if(!fillCommitLists(buggedCommits, controlCommits))
            return false;

        if(!analizeCommits(buggedCommits, controlCommits, analizedCommits))
            return false;

        this.analized = analizedCommits;

        if(!persist(analizedCommits))
            return false;
        
        status = Status.REMOVING_FILES;
        if(!deleteDirectory(repo.getPath()))
            return false;
        
        return true;
    }

    public String getOriginUrl() {
        return this.originUrl;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public GitRepo getRepo() {
        return this.repo;
    }

    public List<Exception> getExceptions(){
        return this.exceptions;
    }

    public List<CLIExecution> getExecutions() {
        return this.executions;
    }

    public Status getStatus(){
        return this.status;
    }

    public int getNumberOfCommitsToAnalize(){
        return this.numOfCommitsToAnalize;
    }

    public void setNumberOfCommitsToAnalize(int number){
        if(number > 0){
            this.numOfCommitsToAnalize = number;
        }
    }

    public List<AnalizedGitCommit> getAnalizedCommits(){
        if(this.analized == null) return new ArrayList<AnalizedGitCommit>();
        return this.analized;
    }

}