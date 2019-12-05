package minerepo.git;

import java.util.ArrayList;
import java.util.List;

public class GitCommitBuilder {

    /**
     * Builds the first commit in log.
     * @param gitLog git status log with one or more commits
     * @return the first commit occurance or null if none found
     */
    public GitCommit buildSingleCommit(List<String> gitLog){
        String id = "", author = "", date = "", msg = "";
        boolean completed = false;
        
        for(int i = 0; i < gitLog.size() && !completed; i++){
            String line = gitLog.get(i);
            if(id.isEmpty()){
                if(line.startsWith("commit")){
                    id = line;
                    id = id.replace("commit", "");
                    id = id.trim();
                }
            } else {
                if(line.startsWith("Merge")){
                    //merge
                } else if(line.startsWith("Author")){
                    author = line;
                    author = author.replace("Author:", "");
                    author = author.trim();
                } else if(line.startsWith("Date")){
                    date = line;
                    date = date.replace("Date:", "");
                    date = date.trim();
                } else if(line.startsWith("commit")){
                    completed = true;
                } else {
                    msg += line;
                }
            }
        }
        if(!completed){
            completed = !id.isEmpty() && !author.isEmpty() && !date.isEmpty();
        }

        if(completed)
            return new GitCommit(id, author, date, msg);
        
        return null;
    }

    /**
     * Builds multiple commits
     * @param gitLog git status log with one or more commits
     * @return a list with all commits in the log
     */
    public List<GitCommit> buildMultipleCommits(List<String> gitLog){
        String id = "", author = "", date = "", msg = "";
        List<GitCommit> commits = new ArrayList<>();
        
        for(int i = 0; i < gitLog.size(); i++){
            String line = gitLog.get(i);
            if(id.isEmpty()){
                if(line.startsWith("commit")){
                    id = line;
                    id = id.replace("commit", "");
                    id = id.trim();
                }
            } else {
                if(line.startsWith("Merge")){
                    //merge
                } else if(line.startsWith("Author")){
                    author = line;
                    author = author.replace("Author:", "");
                    author = author.trim();
                } else if(line.startsWith("Date")){
                    date = line;
                    date = date.replace("Date:", "");
                    date = date.trim();
                } else if(line.startsWith("commit")){
                    GitCommit commit = new GitCommit(id, author, date, msg);
                    msg = "";
                    commits.add(commit);
                    id = line;
                    id = id.replace("commit", "");
                    id = id.trim();
                } else {
                    msg += line;
                }
            }
        }

        if(!id.isEmpty()){
            GitCommit commit = new GitCommit(id, author, date, msg);
            commits.add(commit);
        }
        
        return commits;
    }

}