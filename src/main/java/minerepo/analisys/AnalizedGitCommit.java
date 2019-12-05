package minerepo.analisys;
import java.util.List;

import minerepo.git.GitCommit;

public class AnalizedGitCommit extends GitCommit{

    private String analisys = "";
    
    private Type type = Type.UNSETTED;

    public AnalizedGitCommit(GitCommit analizedCommit){
        super(analizedCommit.getId(), analizedCommit.getAuthor(), analizedCommit.getDate(), analizedCommit.getMsg());
        super.changesPaths = analizedCommit.getChangesPaths();
    }

    public AnalizedGitCommit(GitCommit analizedCommit, String analisys){
        this(analizedCommit);
        this.analisys = analisys;
    }

    public AnalizedGitCommit(GitCommit analizedCommit, List<String> analisys){
        this(analizedCommit);
        setAnalisysFromList(analisys);
    }

    private void setAnalisysFromList(List<String> analisys){
        String newAnalisys = "";
        for(var line : analisys){
            newAnalisys += "\n";
            newAnalisys += line;
        }
        this.analisys = newAnalisys;
    }

    public enum Type {
        BUG, CONTROL, UNSETTED
    } 

    public String getAnalisys(){
        return this.analisys;
    }

    public void setBugged(){
        this.type = Type.BUG;
    }


    public void setControl(){
        this.type = Type.CONTROL;
    }

    public Type getType(){
        return this.type;
    }

    public boolean setAnalisys(String analisys){
        if(this.analisys.equals("")){
            this.analisys = analisys;
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        String ret = super.toString();
        ret += "\n ----------------------------------- \n";
        ret += this.analisys;
        return ret;
    }

}