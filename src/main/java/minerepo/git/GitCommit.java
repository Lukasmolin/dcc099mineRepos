package minerepo.git;

import java.util.ArrayList;
import java.util.List;

public class GitCommit {

    private String id;
    private String author = "";
    private String date = "";
    private String msg = "";

    protected List<String> changesPaths = new ArrayList<>();

    public GitCommit(String id){
        this.id = id;
    }

    public GitCommit(String id, String author, String date, String message){
        this(id);
        this.author = author;
        this.date = date;
        this.msg = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getChangesPaths(){
        return this.changesPaths;
    }

    public void addPath(String path){
        this.changesPaths.add(path);
    }

    public void addPaths(List<String> path){
        this.changesPaths.addAll(path);
    }

    @Override
    public String toString(){
        String commit = "";
        commit += "id: " + id + "\n";
        commit += "author: " + author + "\n";
        commit += "date: " + date + "\n";
        commit += msg + "\n";
        commit += "=========================================== \n";
        return commit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;        
        if (getClass() != obj.getClass())
            return false;
        
        GitCommit other = (GitCommit) obj;
        if(id.equals(other.id)){
            return true;
        }

        return false;
    }

    

}