package com.yalany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {
  private static final String outputFileName = "out-big.txt";

  public static void main(String[] args) {
    assert args.length != 0: "argument: file name is expected";
    var startTime = System.nanoTime();
    var data = new ArrayList<>(readUniqueData(args[0]));
    new Solution(data, "").printResults(outputFileName);
    System.out.println("Execution time: " + ((System.nanoTime() - startTime)/1_000_000) + " ms");
  }

  private static HashSet<String> readUniqueData(String from) {
    var data = new HashSet<String>();
    try (var lines = Files.lines(Path.of(from))) {
      lines.forEach(e -> data.add(e.intern()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return data;
  }
}
