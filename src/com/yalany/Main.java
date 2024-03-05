package com.yalany;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
  private static final String outputFileName = "out.txt";

  // Could've parse input for second time to save RAM, but decided against it for the sake of execution time
  private static final List<String> lines = new ArrayList<>();
  private static final List<Map<String, Integer>> wordPositionData = new ArrayList<>();
  private static final Map<Integer, Integer> lineGroupMap = new HashMap<>();
  private static final List<List<Integer>> groups = new ArrayList<>();


  public static void main(String[] args) {
    assert args.length != 0: "argument: file name is expected";
    var startTime = System.nanoTime();
    try (var stream = Files.lines(Paths.get(args[0]))) {
      stream.forEachOrdered(Main::processLine);
    } catch (IOException e) {
      e.printStackTrace();
    }
    groups.sort((g1, g2) -> g2.size() - g1.size());
    output();
    System.out.println("Execution time: " + ((System.nanoTime() - startTime)/1_000_000) + " ms");
    System.out.println("Number of groups with two or more elements: " + groups.size());
  }

  private static boolean isLineValid(String s) {
    return s.matches("^(\"(?:\\d+|)\"(?:;\"(?:\\d+|)\")*)$");
  }

  private static void processLine(String line) {
    if (isLineValid(line)) {
      lines.add(line);
      handleWords(lines.size() - 1, line.split(";"));
    }
  }

  private static void handleWords(int currentLineId, String[] words) {
    for (int i = 0; i < words.length; i++)
      handleWord(currentLineId, i, words[i]);
  }

  private static void handleWord(int currentLineId, int currentWordId, String currentWord) {
    if (wordPositionData.size() == currentWordId)
      wordPositionData.add(currentWordId, new HashMap<>());
    if (currentWord.equals("\"\"")) return;
    if (wordPositionData.get(currentWordId).containsKey(currentWord))
      formGroups(wordPositionData.get(currentWordId).get(currentWord), currentLineId);
    wordPositionData.get(currentWordId).put(currentWord, currentLineId);
  }

  private static void formGroups(int lastMatchingLineId, int currentLineId) {
    if (lineGroupMap.containsKey(lastMatchingLineId)) {
      var groupId = lineGroupMap.get(lastMatchingLineId);
      lineGroupMap.put(currentLineId, groupId);
      groups.get(groupId).add(currentLineId);
    } else {
      var newGroupId = groups.size();
      lineGroupMap.put(lastMatchingLineId, newGroupId);
      lineGroupMap.put(currentLineId, newGroupId);
      var newGroup = new ArrayList<Integer>();
      newGroup.add(lastMatchingLineId);
      newGroup.add(currentLineId);
      groups.add(newGroupId, newGroup);
    }
  }

  private static void output() {
    try (var writer = new BufferedWriter(new FileWriter(outputFileName))) {
      writer.write("Number of groups with two or more elements: " + groups.size() + "\n");
      for (int i = 0; i < groups.size(); i++) {
        var group = groups.get(i);
        writer.write("Group " + i + "\n");
        for (var lineId : group)
          writer.write(lines.get(lineId) + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
