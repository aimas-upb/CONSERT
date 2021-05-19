package org.aimas.consert.tests.casas;

import java.io.File;
import java.util.Queue;

public interface EventReader {
	Queue<Object> parseEvents(File inputFile);
}