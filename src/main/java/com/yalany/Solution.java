package com.yalany;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {
  private final List<Map<String, Integer>> wordPositionData = new ArrayList<>();
  private final Map<Integer, Integer> lineGroupMap = new HashMap<>();
  private final Map<Integer, Set<Integer>> groupLinesMap = new HashMap<>();
  private final List<String> data;
  private final String validator;

  public Solution(ArrayList<String> data, String validator) {
    this.data = data;
    this.validator = validator;
    for (int i = 0; i < data.size(); i++)
      processLine(i, data.get(i));
  }

  public void printResults(String outputFileName) {
    var groupsOrdered = new ArrayList<>(groupLinesMap.values());
    groupsOrdered.sort((g1, g2) -> g2.size() - g1.size());
    try (var writer = new BufferedWriter(new FileWriter(outputFileName))) {
      writer.write("Number of groups with two or more elements: " + groupsOrdered.size() + "\n");
      for (int i = 0; i < groupsOrdered.size(); i++) {
        var group = groupsOrdered.get(i);
        writer.write("Group " + (i + 1) + "\n");
        for (var lineId : group)
          writer.write(data.get(lineId) + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean isLineValid(String s) {
    return s.matches(validator);
  }

  private void processLine(int lineId, String line) {
    if (!validator.isEmpty() && !isLineValid(line)) return;
    var words = line.split(";");
    for (short i = 0; i < words.length; i++)
      storeWord(lineId, i, words[i].intern());
  }

  private void storeWord(int lineId, short wordId, String word) {
    if (wordPositionData.size() == wordId)
      wordPositionData.add(new HashMap<>());
    if (word.isEmpty() || word.equals("\"\"")) return;
    if (wordPositionData.get(wordId).containsKey(word))
      manageGrouping(wordPositionData.get(wordId).get(word), lineId);
    wordPositionData.get(wordId).put(word, lineId);
  }

  private void manageGrouping(int firstLineId, int secondLineId) {
    if (lineGroupMap.containsKey(firstLineId) && lineGroupMap.containsKey(secondLineId))
      mergeGroups(lineGroupMap.get(firstLineId), lineGroupMap.get(secondLineId));
    else if (lineGroupMap.containsKey(firstLineId))
      addLineToGroup(secondLineId, lineGroupMap.get(firstLineId));
    else if (lineGroupMap.containsKey(secondLineId))
      addLineToGroup(firstLineId, lineGroupMap.get(secondLineId));
    else
      createNewGroup(firstLineId, secondLineId);
  }

  private int lastGroupId = 0;
  private void createNewGroup(int firstLineId, int secondLineId) {
    var group = new HashSet<Integer>();
    group.add(firstLineId);
    group.add(secondLineId);
    lineGroupMap.put(firstLineId, lastGroupId);
    lineGroupMap.put(secondLineId, lastGroupId);
    groupLinesMap.put(lastGroupId, group);
    lastGroupId++;
  }

  private void addLineToGroup(int lineId, int groupId) {
    groupLinesMap.get(groupId).add(lineId);
    lineGroupMap.put(lineId, groupId);
  }

  private void mergeGroups(int firstGroupId, int secondGroupId) {
    if (firstGroupId == secondGroupId) return;
    var secondGroup = groupLinesMap.get(secondGroupId);
    groupLinesMap.get(firstGroupId).addAll(secondGroup);
    groupLinesMap.remove(secondGroupId);
    for (int lineId : secondGroup)
      lineGroupMap.put(lineId, firstGroupId);
  }
}
