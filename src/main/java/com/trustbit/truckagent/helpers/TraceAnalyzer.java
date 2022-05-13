package com.trustbit.truckagent.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TraceAnalyzer {
	private final String[] traceFiles;
	
	public TraceAnalyzer(String... traceFiles) {
		this.traceFiles = traceFiles;
	}
	
	public void analyze() {
		System.out.println("Analyzing traces...");
		for (String traceFile : traceFiles) {
			var waits = new ArrayList<>();
			var drives = new ArrayList<>();
			
			try (var reader = new BufferedReader(new FileReader(traceFile))) {
				for (String line; (line = reader.readLine()) != null; ) {
					if (line.contains("WAIT"))
						waits.add(line);
					else if (line.contains("DRIVE"))
						drives.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			var openTimes = new HashMap<>();
			for (var wait : waits) {
			
			}
		}
	}
	
	public static void main (String[] args) {
		new TraceAnalyzer("src/main/resources/trace_13_114944.json").analyze();
	}
}
