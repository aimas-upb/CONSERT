package org.aimas.consert.tests.ros;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.api.ContextAssertionListener;
import org.aimas.consert.engine.api.EventWindowListener;
import org.aimas.consert.engine.core.EngineRunner;
import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.eventwindow.EventWindow;
import org.aimas.consert.model.operators.AnnAfterOperator;
import org.aimas.consert.model.operators.AnnBeforeOperator;
import org.aimas.consert.model.operators.AnnIncludesOperator;
import org.aimas.consert.model.operators.AnnIntersectsOperator;
import org.aimas.consert.model.operators.AnnOverlappedByOperator;
import org.aimas.consert.model.operators.AnnOverlapsOperator;
import org.aimas.consert.model.operators.AnnStartsAfterOperator;
import org.aimas.consert.tests.casas.CASASEventReader;
import org.aimas.consert.tests.casas.CASASSimClockEventInserter;
import org.aimas.consert.tests.casas.EventReader;
import org.aimas.consert.tests.ros.serializers.ConsertModelSerializer;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class ConsertEngineROSNode extends BaseConsertEngineROSNode 
	implements ContextAssertionListener, EventWindowListener, MessageListener<consert.ContextAssertion> {
	
	public static final String PERSON = "p15";
	public static final String TASK = "interweaved";
	public static final String TEST_FILE = "files/casas_adlinterweaved/" + PERSON + "_interweaved" + ".json";
	public static final String VALID_FILE = "files/casas_adlinterweaved/" + PERSON + "_activity_intervals" + ".json";
	public static final String [] activities = {"PhoneCall", "WatchDVD", "PreparingSoup", "WriteBirthdayCard", "FillDispenser", "Cleaning", "ChoosingOutfit"};

	
	private Publisher<consert.ContextAssertion> contextAssertionPublisher;
	private Publisher<consert.EventWindow> eventWindowPublisher;
	
	private Subscriber<consert.ContextAssertion> contextAssertionSubscriber;
	
	
	private ConsertModelSerializer consertModelSerializer;
	
	private KieSession kSession;
	private EventTracker eventTracker;
	private Thread engineRunner;
	private CASASSimClockEventInserter eventInserter;
	
	
	private void setupSession(String filepath, String person, String task) throws Exception {
		System.out.println("RUNNING EVENTS FOR file: " + filepath);
		
		// create a new knowledge builder conf
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		builderConf.setOption(EvaluatorOption.get("annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annStartsAfter", new AnnStartsAfterOperator.AnnStartsAfterEvaluatorDefinition()));

		// create a new kie session conf
		KieSessionConfiguration kSessionConfig = KieServices.Factory.get().newKieSessionConfiguration();
		kSessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );

		kSession = getKieSessionFromResources( builderConf, kSessionConfig,
				"casas_interwoven_rules/CASAS_base.drl",
				"casas_interwoven_rules/CASAS_location.drl",
				"casas_interwoven_constraints/PersonLocation_constraints.drl",

				"casas_interwoven_rules/CASAS_watch_DVD.drl",
				"casas_interwoven_rules/CASAS_phone_call.drl",
				"casas_interwoven_rules/CASAS_fill_pills.drl",
				"casas_interwoven_rules/CASAS_soup.drl",
				"casas_interwoven_rules/CASAS_outfit.drl",
				"casas_interwoven_rules/CASAS_write_birthdaycard.drl",
				"casas_interwoven_rules/CASAS_water_plants.drl",
				
				//"casas_interwoven_eventwindow_rules/CASAS_water_plants_event_windows.drl"
				"casas_interwoven_eventwindow_rules/CASAS_cleaning_event_windows.drl"
        );
		
		
		// set up engine runner thread and event inserter
    	engineRunner = new Thread(new EngineRunner(kSession));
    	engineRunner.setName("CONSERT ENGINE runner thread");
    	File inputFile = getFileNameFromResources(filepath);

    	// set up session clock
    	long testStartTs = System.currentTimeMillis();
    	SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(testStartTs, TimeUnit.MILLISECONDS);

		eventTracker = new EventTracker(kSession);
		EventReader eventReader = new CASASEventReader();

    	//CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
		eventInserter = new CASASSimClockEventInserter(inputFile, eventReader, kSession, eventTracker);
    	kSession.addEventListener(eventTracker);

    }
	
	public ConsertEngineROSNode() {
		try {
	        setupSession(TEST_FILE, PERSON, TASK);
        }
        catch (Exception e) {
	        e.printStackTrace();
	        System.exit(-1);
        }
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("consert/engine");
	}
	
	private class CASASTestLoop extends CancellableLoop {
		private ConnectedNode connectedNode;
		
		CASASTestLoop(ConnectedNode connectedNode) {
			this.connectedNode = connectedNode;
        }
		
		@Override
		protected void setup() {
			// start the CONSERT ENgine thread and the inserter, wait for the
			// inserter to finish then exit
			engineRunner.start();
			eventInserter.start();
		}
		
		@Override
		protected void loop() throws InterruptedException {
			if (!eventInserter.isFinished()) {
				Thread.sleep(2000);
			}
			else {
				// If the node has stopped processing all events, terminate the
				// inserter
				// and dispose of the kSession()
				eventInserter.stop();
				kSession.halt();
				kSession.dispose();
				kSession.destroy();
				
				eventInserter.stop();
				engineRunner.join(2000);
				
				// lastly cancel the loop
				this.cancel();
				
				connectedNode.shutdown();
			}
		}
		
	}
	
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		// create CONSERT Model serializer to read/write from/to CONSERT ROS messages to CONSERT Object Model form
		consertModelSerializer = new ConsertModelSerializer(connectedNode.getTopicMessageFactory());
		
		// Create context assertion publisher
		contextAssertionPublisher = connectedNode
		        .newPublisher("consert/engine/contextAssertions", consert.ContextAssertion._TYPE);
		
		// Create event window publisher
		eventWindowPublisher = connectedNode
		        .newPublisher("consert/engine/eventWindows", consert.EventWindow._TYPE);
		
		// Create ContextAssertion subscriber - to listen for ContextAssertions inserted from external sources
		contextAssertionSubscriber = connectedNode.newSubscriber("consert/engine/insertedAssertions", consert.ContextAssertion._TYPE);
		contextAssertionSubscriber.addMessageListener(this);
		
		// register ourselves as a ContextAssertionListener
		eventTracker.addContextAssertionListener(this);
		
		// register ourselves as a EventWindowListener
		eventTracker.addEventWindowListener(this);
    	
    	// start a main loop which waits for the processing of all the events
		// by the engine runner
		
    	connectedNode.executeCancellableLoop(new CASASTestLoop(connectedNode));
    	
    	System.out.println("========================== START PROCESSING ======================");
	}
	
	
	@Override
	public void onShutdown(Node node) {
	    super.onShutdown(node);
	    
	    System.out.println("################## PUBLISHED " + counter + " eventWindows");
	    
	    System.out.println("========================== TERMINATE MORE ======================");
//		try {
			kSession.halt();
	    	kSession.dispose();
			kSession.destroy();
	    	
//	    	eventInserter.stop();
//	    	engineRunner.join(2000);
	    	
//	    	ExecutorService executor = (ExecutorService) ExecutorProviderFactory.getExecutorProvider().getExecutor();
//	        executor.shutdown();
//        }
//        catch (InterruptedException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
	}
	
	@Override
	public void onShutdownComplete(org.ros.node.Node node) {
		super.onShutdownComplete(node);
		
		
	};
	
	@Override
    public void notifyAssertionInserted(ContextAssertion assertion) {
		
		consert.ContextAssertion msg = consertModelSerializer.writeAssertion(assertion);
        contextAssertionPublisher.publish(msg);
    }

	@Override
    public void notifyAssertionDeleted(ContextAssertion assertion) {
	    // we don't need to do anything upon delete - 
		// we assumed the consumer will only be interested in the most recent instance of a ContextAssertion
	    
    }
	
	static int counter = 0;
	
	@Override
    public void notifyEventWindowSubmitted(EventWindow eventWindow) {
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> Publishing eventWindow: \n" + eventWindow + "\n");
		
		consert.EventWindow msg = consertModelSerializer.writeEventWindow(eventWindow);
	    counter ++;
	    eventWindowPublisher.publish(msg);
    }
	
	
	@Override
    public void onNewMessage(consert.ContextAssertion assertionMsg) {
	    ContextAssertion newAssertion = consertModelSerializer.readAssertion(assertionMsg);
	    
	    if (newAssertion.isAtomic()) {
	    	eventTracker.insertEvent(newAssertion);
	    }
	    else {
	    	eventTracker.insertDerivedEvent(newAssertion);
	    }
    }
	
	
	public static void main(String[] args) throws URISyntaxException {
		// create a list of a single element with the qualified java name of the ConsertEngineROSNode
		List<String> argList = new ArrayList<String>();
		argList.add(ConsertEngineROSNode.class.getName());
		
		CommandLineLoader loader = new CommandLineLoader(argList);
		
		String nodeClassName = ConsertEngineROSNode.class.getName();
		System.out.println("Loading node class: " + loader.getNodeClassName());
		NodeConfiguration nodeConfiguration = loader.build();
		
		nodeConfiguration.setMasterUri(new java.net.URI("http://localhost:11311/"));
		
		NodeMain nodeMain = null;
		try {
			nodeMain = loader.loadClass(nodeClassName);
		}
		catch (ClassNotFoundException e) {
			throw new RosRuntimeException("Unable to locate node: "
			        + nodeClassName, e);
		}
		catch (InstantiationException e) {
			throw new RosRuntimeException("Unable to instantiate node: "
			        + nodeClassName, e);
		}
		catch (IllegalAccessException e) {
			throw new RosRuntimeException("Unable to instantiate node: "
			        + nodeClassName, e);
		}
		
		assert (nodeMain != null);
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		nodeMainExecutor.execute(nodeMain, nodeConfiguration);
		
		
//		System.out.println("MAIN THREAD REACHED HERE!!!");
//		nodeMainExecutor.shutdownNodeMain(nodeMain);
    }
}
