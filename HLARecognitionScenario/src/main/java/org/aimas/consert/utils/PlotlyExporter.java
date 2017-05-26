package org.aimas.consert.utils;

import org.apache.commons.lang.SystemUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import java.lang.ProcessBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Created by mihai on 5/20/17.
 */
public class PlotlyExporter {

    public static String MINICONDA_BIN_FOLDER;


    public static boolean exportToHTML(String outputHTMLFilename, KieSession session) {

        if (SystemUtils.IS_OS_LINUX)
            MINICONDA_BIN_FOLDER = "/home/mihai/miniconda2/bin";
        else
            MINICONDA_BIN_FOLDER = "C:\\Users\\David\\Miniconda2";

        // Check outputHTMLFilename and set default
        if(outputHTMLFilename == null || outputHTMLFilename.isEmpty())
            outputHTMLFilename = ".." + File.separator  + "hla-event-visualizer" + File.separator + "outputs" + File.separator + "hla-kb-visualizer.html";

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
            try {
                File entryPointTempFile = new File(tmp +  File.separator + "output_" + entryPoint.getEntryPointId() + ".json");
                mapper.writeValue(entryPointTempFile, entryPoint.getObjects());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Call the python script which generates a plotly gantt chart
        try {
            // build command (this works only under Linux)
            String[] command = new String[3];
            if (SystemUtils.IS_OS_LINUX) {
                command[0]="bash";
                command[1]="-c";
                command[2]=  "source activate consert; " +
                        "python ../hla-event-visualizer/plotly_generator.py --f " +
                        tmp + " --o " + outputHTMLFilename;
            }
            else
                {
                    command[0]="cmd";
                    command[1]="/C";
                    command[2]=  "activate consert & " +
                            "python ..\\hla-event-visualizer\\plotly_generator.py --f " +
                            tmp + " --o " + outputHTMLFilename;
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
