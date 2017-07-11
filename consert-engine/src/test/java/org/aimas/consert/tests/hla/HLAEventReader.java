package org.aimas.consert.tests.hla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.aimas.consert.tests.hla.assertions.LLA;
import org.aimas.consert.tests.hla.assertions.Position;
import org.aimas.consert.tests.hla.assertions.SittingLLA;
import org.aimas.consert.tests.hla.assertions.StandingLLA;
import org.aimas.consert.tests.hla.assertions.WalkingLLA;
import org.aimas.consert.tests.hla.entities.LLAType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by alex on 06.04.2017.
 */
public class HLAEventReader {

    public static Queue<Object> parseEvents(File inputFile) {
        Queue<Object> eventList = new LinkedList<Object>();

        try {
            final InputStream in = new FileInputStream(inputFile);
            ObjectMapper mapper =  new ObjectMapper();

            final JsonNode eventListNode = mapper.readTree(in);

            for (JsonNode eventNode : eventListNode) {
                JsonNode eventDataNode = eventNode.get("event");
                String nodeType = eventDataNode.get("event_type").textValue();

                if (nodeType.equals("pos")) {
                    JsonNode eventInfoNode = eventDataNode.get("event_info");
                    Position pos = mapper.treeToValue(eventInfoNode, Position.class);
                    eventList.offer(pos);
                }
                else if (nodeType.equals("lla")) {
                    JsonNode eventInfoNode = eventDataNode.get("event_info");
                    LLAType llaType = LLAType.valueOf(eventInfoNode.get("type").textValue());
                    LLA lla = null;

                    switch(llaType) {
                        case SITTING:
                            lla = mapper.treeToValue(eventInfoNode, SittingLLA.class);
                            break;
                        case STANDING:
                            lla = mapper.treeToValue(eventInfoNode, StandingLLA.class);
                            break;
                        case WALKING:
                            lla = mapper.treeToValue(eventInfoNode, WalkingLLA.class);
                            break;
                        default:
                            throw new Exception("Unknown value for LLA type: " + llaType);
                    }

                    if (lla != null)
                        eventList.offer(lla);

                }
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
