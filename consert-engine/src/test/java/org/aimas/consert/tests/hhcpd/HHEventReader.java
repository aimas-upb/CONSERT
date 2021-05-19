package org.aimas.consert.tests.hhcpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hhcpd.assertions.Cabinet;
import org.aimas.consert.tests.hhcpd.assertions.Motion;
import org.aimas.consert.tests.utils.EventReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by alex on 06.04.2017.
 */
public class HHEventReader implements EventReader {
	@SuppressWarnings("serial")
    public static final Map<String, Class<? extends ContextAssertion>> eventClassMapping = new HashMap<String, Class<? extends ContextAssertion>>() {
		{
			put("motion", Motion.class);
			put("cabinet", Cabinet.class);
		}
	};
	
	
    public Queue<Object> parseEvents(File inputFile) {
        Queue<Object> eventList = new LinkedList<Object>();

        try {
            final InputStream in = new FileInputStream(inputFile);
            ObjectMapper mapper =  new ObjectMapper();

            final JsonNode eventListNode = mapper.readTree(in);

            for (JsonNode eventNode : eventListNode) {
                JsonNode eventDataNode = eventNode.get("event");
                String nodeType = eventDataNode.get("event_type").textValue();
                JsonNode eventInfoNode = eventDataNode.get("event_info");
                
                Class<? extends ContextAssertion> assertionClass = eventClassMapping.get(nodeType);
                ContextAssertion assertion = mapper.treeToValue(eventInfoNode, assertionClass);
                
                eventList.offer(assertion);
            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventList;
    }
}
