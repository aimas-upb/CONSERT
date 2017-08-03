package org.aimas.consert.tests.casas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by mihai on 5/20/17.
 */
public class CASASPlotlyExporter {

    public static String MINICONDA_BIN_FOLDER;


    public static boolean exportToHTML(String person, String task, KieSession session) {

        if (SystemUtils.IS_OS_LINUX)
            MINICONDA_BIN_FOLDER = "/home/alex/miniconda2/bin";
        else
            MINICONDA_BIN_FOLDER = "C:\\Users\\David\\Miniconda2";

        // Check outputHTMLFolder and set default
        File cwd = new File(Paths.get("").toAbsolutePath().toString());
        File parentFolder = cwd.getParentFile();
        
        //String outputHTMLFolder = parentFolder.getAbsolutePath() + File.separator  + "casas-event-visualizer" + File.separator + "outputs" + File.separator + task;
        String outputHTMLFolder = parentFolder.getAbsolutePath() + File.separator  + "casas-event-visualizer" + File.separator + "experiment" + File.separator + task;
        
        // Create temporary folder
        Path tmp;
        try {
            tmp = Files.createTempDirectory("consert_");
            System.out.println("[Plotly] Generating JSON outputs @: " + tmp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Write each JSON file for its respective entrypoint
        ObjectMapper mapper = new ObjectMapper();
        Collection<EntryPoint> entryPoints = (Collection<EntryPoint>) session.getEntryPoints();
        for(EntryPoint entryPoint : entryPoints) {
            //if (entryPoint.getEntryPointId().startsWith("Extended")) {
	        	try {
	                File entryPointTempFile = new File(tmp +  File.separator + "output_" + entryPoint.getEntryPointId() + ".json");
	                mapper.writeValue(entryPointTempFile, entryPoint.getObjects());
	            } catch (IOException e) {
	                e.printStackTrace();
	                return false;
	            }
            //}
        }

        // Call the python script which generates a plotly gantt chart
        try {
            // build command (this works only under Linux)
            String[] command = new String[3];
            if (SystemUtils.IS_OS_LINUX) {
                command[0]="bash";
                command[1]="-c";
                command[2]=  "source activate consert; " +
                        "python ../casas-event-visualizer/casas_plotly_generator.py --f " +
                        tmp + " --o " + outputHTMLFolder + " --p " + person;
            }
            else
                {
                    command[0]="cmd";
                    command[1]="/C";
                    command[2]=  "activate consert & " +
                            "python ..\\casas-event-visualizer\\casas_plotly_generator.py --f " +
                            tmp + " --o " + outputHTMLFolder + " --p " + person;
                }

            // build PATH environment variable value
            StringBuilder sbPath = new StringBuilder(System.getenv("PATH"));
            sbPath.append(":").append(MINICONDA_BIN_FOLDER);

            // create a process builder for the command
            ProcessBuilder pb = new ProcessBuilder(command);

            // set the PATH environment variable
            Map<String,String> env = pb.environment();
            if(env != null)
                env.put("PATH",sbPath.toString());

            // redirect outputs from python back to java
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            // run the process and wait for it
            Process p = pb.start();

            p.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
