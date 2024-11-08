package backend.academy.log.analyzer.core;

import backend.academy.log.analyzer.read.LogReader;
import backend.academy.log.analyzer.read.LogReaderFactory;

public class Main {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.initialize(args);

        LogReader logReader = LogReaderFactory.create(argumentParser.path());
    }
}
