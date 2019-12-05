package minerepo.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import minerepo.App;

/**
 *
 * @author gleiph
 */
public class CLIExecute {
    
    public static CLIExecution execute(String command, String directory) throws IOException {
        App.getLog().log("EXECTUTING:" + command);
        CLIExecution execution = new CLIExecution(command);
        
        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec(command, null,
                new File(directory));

        String s;

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

        // read the output from the command
        while ((s = stdInput.readLine()) != null) {
            execution.addOutput(s);
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            execution.addError(s);
        }

        App.getLog().writeLine("\n"+execution.toString());
        
        return execution;
    }
    
}