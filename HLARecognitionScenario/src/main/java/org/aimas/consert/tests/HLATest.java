package org.aimas.consert.tests;

import org.aimas.consert.utils.JSONEventReader;
import org.aimas.consert.eventmodel.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

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

        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("ksession-rules");

        for (Object x: events)
        {
            kSession.insert(x);
        }

        kSession.fireAllRules();

    }

    File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
