package org.aimas.consert.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.aimas.consert.model.annotations.*;
import org.aimas.consert.model.content.ContextAssertion;
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
public class JSONEventReader {

    private static ContextAssertion setAnnotations(String node, ContextAssertion assertion) throws ParseException {

        DefaultAnnotationData annotations = new DefaultAnnotationData();

        if (node.indexOf("annotations")>=0)
        {
            if (node.indexOf("confidence")>=0)
            {
                int index = node.indexOf("confidence")+12;
                int fin = index;
                while ( (node.charAt(fin)>='0' && node.charAt(fin)<='9' ) || node.charAt(fin)=='.'|| node.charAt(fin)=='E')
                    fin++;
                double val = Double.parseDouble(node.substring(index, fin));
                    annotations.add(new NumericCertaintyAnnotation(val,"","",""));
            }
            if (node.indexOf("lastUpdated")>=0)
            {
                int index = node.indexOf("lastUpdated")+13;
                int fin = index;
                while ( (node.charAt(fin)>='0' && node.charAt(fin)<='9' ) || node.charAt(fin)=='.'|| node.charAt(fin)=='E' )
                    fin++;
                double val = Double.parseDouble(node.substring(index, fin));
                annotations.add(new NumericTimestampAnnotation(val,"","",""));
            }
            if (node.indexOf("endTime")>=0)
            {
                int index = node.indexOf("endTime")+10;
                int fin = index;

                while ( node.charAt(fin)!='"')
                    fin++;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH);
                Date endTime = format.parse(node.substring(index,fin));

                index = node.indexOf("startTime") + 12;
                fin = index;
                while ( node.charAt(fin)!='"')
                    fin++;

                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH);
                Date startTime = new Date();
                startTime = format2.parse(node.substring(index,fin));

                DatetimeInterval time = new DatetimeInterval(startTime, endTime);
                annotations.add(new TemporalValidityAnnotation(time,"","",""));
            }
        }

        assertion.setAnnotations(annotations);
        return assertion;
    }
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

                    String node = eventInfoNode.toString();
                    pos = (Position) setAnnotations(node,pos);

                    eventList.offer(pos);
                }
                else if (nodeType.equals("lla")) {
                    JsonNode eventInfoNode = eventDataNode.get("event_info");
                    String llaType = eventInfoNode.get("type").textValue();
                    LLA lla = null;

                    switch(llaType) {
                        case "SITTING":
                            lla = mapper.treeToValue(eventInfoNode, SittingLLA.class);
                            break;
                        case "STANDING":
                            lla = mapper.treeToValue(eventInfoNode, StandingLLA.class);
                            break;
                        case "WALKING":
                            lla = mapper.treeToValue(eventInfoNode, WalkingLLA.class);
                            break;
                        default:
                            throw new Exception("Unknown value for LLA type: " + llaType);
                    }

                    if (lla != null) {
                        String node = eventInfoNode.toString();
                        lla = (LLA) setAnnotations(node, lla);
                        eventList.offer(lla);
                    }

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
