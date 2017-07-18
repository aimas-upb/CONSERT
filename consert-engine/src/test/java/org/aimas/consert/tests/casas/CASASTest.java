package org.aimas.consert.tests.casas;


import java.io.File;
import java.io.FileFilter;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.utils.TestSetup;
import org.kie.api.runtime.KieSession;


/**
 * Created by alex on 06.04.2017.
 */
public class CASASTest extends TestSetup {
    
	public static void main(String[] args) {
    	
    	try {
	    	// load up the knowledge base
    		/*
    		KieServices ks = KieServices.Factory.get();
	        KieSessionConfiguration config = ks.newKieSessionConfiguration();
	        config.setOption(ClockTypeOption.get("realtime"));
	        
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	KieSession kSession = kContainer.newKieSession("ksession-rules", config);
	    	*/
    		
    		// Read all files from the CASAS Dataset
    		String datasetFolderPath = "files" + File.separator + "casas_adlnormal";
    		//String datasetFolderPath = "files" + File.separator + "casas_adlinterwieved";
    		File casasFolder = getFileNameFromResources(datasetFolderPath);
    		
    		File[] datasetFiles = casasFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith("p");
				}
			});
	    	
	    	for (File f : datasetFiles) {
	    		String filename = f.getName();
	    		
	    		String person = filename.split("\\.")[0];
	    		String task = filename.split("\\.")[1];
	    		
	    		runEvents(datasetFolderPath, filename, person, task);
	    	}
    		
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }

	private static void runEvents(String datasetFolderPath, String filename, String person, String task) throws Exception {
		System.out.println("RUNNING EVENTS FOR file: " + datasetFolderPath + File.separator + filename);
		
		// create a new session
		KieSession kSession = getKieSessionFromResources( "casas_rules/CASAS.drl" );

    	// set up engine runner thread and event inserter
    	Thread engineRunner = new Thread(new EngineRunner(kSession));
    	
    	String filePath = datasetFolderPath + File.separator + filename;
    	File inputFile = getFileNameFromResources(filePath);
    	
    	EventTracker eventTracker = new EventTracker(kSession);
    	CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
    	
    	// start the engine thread and the inserter, wait for the inserter to finish then exit
    	engineRunner.start();
    	eventInserter.start();
    	
    	while (!eventInserter.isFinished()) {
    		Thread.sleep(2000);
    	}
    	
    	eventInserter.stop();
    	engineRunner.join(2000);
    	
		// Plot the results. Plots are stored per task (i.e. the actions by all people are grouped per task).
    	CASASPlotlyExporter.exportToHTML(person, task, kSession);
        
    	eventInserter.stop();

    	kSession.halt();
    	kSession.dispose();
    }
}
