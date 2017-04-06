package org.aimas.consert.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aimas.consert.eventmodel.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alex on 06.04.2017.
 */
public class JSONEventReader {

    public static List<Object> parseEvents(File inputFile) {
        List<Object> eventList = new LinkedList<Object>();

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
                    eventList.add(pos);
                }
                else if (nodeType.equals("lla")) {
                    JsonNode eventInfoNode = eventDataNode.get("event_info");
                    LLA.Type llaType = LLA.Type.valueOf(eventInfoNode.get("type").textValue());
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
                        eventList.add(lla);

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
