package org.aimas.consert.tests;

import org.aimas.consert.utils.JSONEventReader;

import java.io.File;
import java.util.List;

/**
 * Created by alex on 06.04.2017.
 */
public class HLATest {

    public static void main(String[] args) {
        HLATest test = new HLATest();

        File inputFile = test.getFileNameFromResources("files/single_hla_120s_01er_015fd.json");
        List<Object> events = JSONEventReader.parseEvents(inputFile);

        System.out.println(events);
    }

    File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
