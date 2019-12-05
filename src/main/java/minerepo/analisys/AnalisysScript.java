package minerepo.analisys;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import minerepo.git.GitCommit;
import minerepo.runtime.CLIExecution;

public class AnalisysScript implements Runnable{

    private List<String> reposUrls = new ArrayList<>();
    private String folderPath;
    private Thread[] threads;
    private IndividualScript[] runningScripts;
    private List<CLIExecution> fails = new ArrayList<>();
    private List<Exception> exceptions = new ArrayList<>();
    private List<Predicate<GitCommit>> filters = new ArrayList<>();

    public AnalisysScript(List<String> reposUrl, String executionsFolderPath, int numberOfThreads){
        this.threads = new Thread[numberOfThreads];
        this.runningScripts = new IndividualScript[numberOfThreads];
        this.folderPath = executionsFolderPath;
        this.reposUrls.addAll(reposUrl);
        for(int i = 0; i < this.threads.length; i++){
            threads[i] = new Thread();
        }
    }

    public void addUrl(String repo){
        this.reposUrls.add(repo);
    }

    public List<CLIExecution> getFails() {
        return fails;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void addFilter(Predicate<GitCommit> filter){
        this.filters.add(filter);
    }

    public IndividualScript[] getRunningScripts(){
        if(this.runningScripts == null)
            return new IndividualScript[0];

        return this.runningScripts;
    }

    public void Start(){
        for(var url : reposUrls){
            IndividualScript newScript = new IndividualScript(url, folderPath, folderPath);
            for(var filter : filters){
                newScript.addFilter(filter);                
            }
            runningScripts = new IndividualScript[1];
            runningScripts[0] = newScript;
            newScript.run();
            if(!newScript.getExceptions().isEmpty()){
                exceptions.addAll(newScript.getExceptions()); 
                fails.addAll(newScript.getExecutions());       
            }            
        }



        // while(!reposUrls.isEmpty()){
        //     for(int i = 0; i < threads.length; i++){
        //         Thread t = threads[i];
        //         if(!t.isAlive() && !reposUrls.isEmpty()){
        //             //Retrieving Exceptions from Executed Script
        //             if(runningScripts[i] != null){
        //                 var script = runningScripts[i];
        //                 if(!script.getExceptions().isEmpty()){
        //                     exceptions.addAll(script.getExceptions()); 
        //                     fails.addAll(script.getExecutions());       
        //                 }
        //             }

        //             IndividualScript newScript = new IndividualScript(reposUrls.get(0), folderPath, folderPath);
        //             reposUrls.remove(0);
        //             for(var filter : filters){
        //                 newScript.addFilter(filter);
        //             }

        //             runningScripts[i] = newScript;
        //             threads[i] = new Thread(runningScripts[i]);
        //             threads[i].start();
        //         }
        //     }
        // }
    }

    public boolean isRunning(){
        for(Thread t : threads){
            if(t != null && t.isAlive())
                return true;
        }
        return false;
    }

    public int scriptsRunning(){
        int rtn = 0;
        for(Thread t : threads){
            if(t != null && t.isAlive())
                rtn += 1;
        }
        return rtn;
    }

    public int scriptsWaiting(){
        return reposUrls.size();
    }

    @Override
    public void run() {
        Start();
    }





}