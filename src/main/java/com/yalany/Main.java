package com.yalany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {
  private static final String outputFileName = "out.txt";

  public static void main(String[] args) {
    assert args.length != 0: "argument: file name is expected";
    var startTime = System.nanoTime();
    new Solution(new ArrayList<>(readData(args[0]))).printResults(outputFileName);
    System.out.println("Execution time: " + ((System.nanoTime() - startTime)/1_000_000) + " ms");
  }

  private static boolean validate(String s) {
//    return s.matches("^(\"(?:\\d+|)\"(?:;\"(?:\\d+|)\")*)$");
    return true;
  }

  private static HashSet<String> readData(String from) {
    var data = new HashSet<String>();
    try (var lines = Files.lines(Path.of(from))) {
      lines.forEach(line -> {
        if (validate(line)) data.add(line);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return data;
  }
}
