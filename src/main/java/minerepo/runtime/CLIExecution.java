package minerepo.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class CLIExecution {

    private String executed;
    private List<String> output;
    private List<String> error;

    public CLIExecution(String executedCommand) {
        this.output = new ArrayList<>();
        this.error = new ArrayList<>();
        this.executed = executedCommand;
    }

    /**
     * @return the output
     */
    public List<String> getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(List<String> output) {
        this.output = output;
    }

    /**
     * @return the error
     */
    public List<String> getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(List<String> error) {
        this.error = error;
    }

    public void addOutput(String line){
        this.output.add(line);
    }

    public void addError(String line){
        this.error.add(line);
    }

    @Override
    public String toString() {
        String result = "";

        if(!executed.equals("")){
            result += "==========================Executed==========================\n";
            result += executed + "\n";
        }
        
        result += "==========================Output==============================\n";
        for (String string : output) {
            result += string+"\n";
        }
        
        result += "==========================Error==============================\n";
        for (String string : error) {
            result += string+"\n";
        }
        
        return result;
                
    }

    /**
     * @return the executed command
     */
    public String getExecuted() {
        return executed;
    }
    
    
}