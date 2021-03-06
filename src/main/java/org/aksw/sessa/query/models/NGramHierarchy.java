package org.aksw.sessa.query.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the n-gram hierarchy. For a given n-gram it represents a tree. The root of
 * the tree is the n-gram itself. The children of each node are the different 'n-1-grams'. E.g. the
 * children of the trigram "bill gates wife" are bigrams "bill gates" and "gates wife".
 *
 * @author Simon Bordewisch
 */
public class NGramHierarchy {

  private List<String> nGram;

  /**
   * Initializes with already splitted n-gram. The split has to be between the words. E.g. the
   * orginal n-gram "birthplace bill gates" has to be given as '["birthplace", "bill", "gates"]'.
   *
   * @param nGram already split n-gram
   */
  public NGramHierarchy(String[] nGram) {
    this.nGram = new ArrayList<>();
    this.nGram.addAll(Arrays.asList(nGram));
  }

  /**
   * Initializes with n-gram sentences in normal String-representation. Unigram has to be split by
   * one single space.
   *
   * @param nGram n-gram in String-representation
   */
  public NGramHierarchy(String nGram) {
    this(nGram.split(" "));
  }

  /**
   * This method is for copying an already existing n-gram hierarchy.
   *
   * @param nGram existing n-gram hierarchy which should be copied
   */
  public NGramHierarchy(NGramHierarchy hierarchy) {
    this(hierarchy.toStringArray());
  }

  /**
   * Returns the n-gram for which the positional data was given.
   *
   * @param pos the n-gram entry position
   * @return n-gram at the given position
   */
  public String getNGram(NGramEntryPosition pos) {
    return getNGram(pos.getLength(), pos.getPosition());
  }

  /**
   * Returns the n-gram for which the positional data was given.
   *
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @param index represents the position within the n-gram of given length
   * @return n-gram at the given position
   */
  public String getNGram(int length, int index) {
    if (length == 1) {
      return nGram.get(index);
    } else {
      StringBuilder sb = new StringBuilder();
      for (int i = index; i < length + index - 1; i++) {
        sb.append(nGram.get(i) + " ");
      }
      sb.append(nGram.get(length + index - 1));
      return sb.toString();
    }
  }


  /**
   * Returns the parents of given n-gram.
   *
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @param index represents the position within the n-gram of given length
   * @return parents of given n-gram
   */
  public String[] getParents(int length, int index) {
    String parents[];
    if (index == 0) {
      if (index + length == nGram.size()) {
        return null;
      } else {
        String parent = getNGram(length + 1, index);
        parents = new String[1];
        parents[0] = parent;
      }
    } else if (index + length == nGram.size()) {
      String parent = getNGram(length + 1, index - 1);
      parents = new String[1];
      parents[0] = parent;
    } else {
      String parent1 = getNGram(length + 1, index - 1);
      String parent2 = getNGram(length + 1, index);
      parents = new String[2];
      parents[0] = parent1;
      parents[1] = parent2;
    }
    return parents;
  }


  /**
   * Given the position and length for a n-gram, returns direct children, i.e. the children directly
   * connected to the n-gram within the n-gram hierarchy.
   *
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @param index represents the position within the n-gram of given length
   * @return directly connected children
   */
  public String[] getDirectChildren(int length, int index) {
    if (length == 1) {
      return null;
    } else {
      String[] children = new String[2];
      children[0] = getNGram(length - 1, index);
      children[1] = getNGram(length - 1, index + 1);
      return children;
    }
  }

  /**
   * Returns the whole n-gram hierarchy sorted by length, then by position. E.g. "birthplace bill
   * gates" would return ["birthplace bill gates", "birthplace bill", "bill gates", "birthplace",
   * "bill", "gates"]
   *
   * @return n-gram hierarchy represented as array
   */
  public String[] toStringArray() {
    String[] hierarchy = new String[(nGram.size() * (nGram.size() + 1)) / 2];
    int hierarchyIndex = 0;
    for (int l = nGram.size(); l > 0; l--) {
      for (int i = 0; i + l <= nGram.size(); i++) {
        hierarchy[hierarchyIndex] = getNGram(l, i);
        hierarchyIndex++;
      }
    }
    return hierarchy;
  }

  /**
   * Generates all possible NGramEntryPositions for this n-gram hierarchy. I.e. if the n-gram has a
   * length of 3, it would generate the NGramEntryPositions for the trigram, for the 2 possible
   * bigrams and for the 3 unigrams.
   *
   * @return all possible NgramEntryPositions as set
   * @see NGramEntryPosition
   */
  public Set<NGramEntryPosition> getAllPositions() {
    Set<NGramEntryPosition> positions = new HashSet<>();
    for (int l = nGram.size(); l > 0; l--) {
      for (int i = 0; i + l <= nGram.size(); i++) {
        positions.add(new NGramEntryPosition(l, i));
      }
    }
    return positions;
  }

  /**
   * Returns length of initial n-gram, i.e. how many words it has.
   *
   * @return number of words within the initial n-gram
   */
  public int getNGramLength() {
    return nGram.size();
  }

  /**
   * Extends the hierarchy by adding additional keywords (as array) at the end.
   *
   * @param extension array of strings which should be added
   */
  public void extendHierarchy(String[] extension) {
    this.nGram.addAll(Arrays.asList(extension));
  }

  /**
   * Extends the hierarchy by adding additional keywords at the end.
   *
   * @param extension array of strings which should be added
   */
  public void extendHierarchy(String extension) {
    extendHierarchy(extension.split(" "));
  }

  /**
   * Returns the position of the given n-gram in the n-gram hierarchy.
   *
   * @param nGram n-gram for which the position should be given back
   * @return position of the given n-gram
   */
  public NGramEntryPosition getPosition(String nGram) {
    return getPosition(nGram.split(" "));
  }

  /**
   * Returns the position of the given n-gram (in array-format) in the n-gram hierarchy.
   *
   * @param forNGram n-gram for which the position should be given back
   * @return position of the given n-gram
   */
  public NGramEntryPosition getPosition(String[] forNGram) {
    int pos = -1;
    for (int i = 0; i < nGram.size(); i++) {
      if (nGram.get(i).equals(forNGram[0])) {
        boolean foundWholeNGram = true;
        for (int j = 1; j < forNGram.length; j++) {
          if (i + j >= nGram.size() || !nGram.get(i + j).equals(forNGram[j])) {
            foundWholeNGram = false;
          }
        }
        if (foundWholeNGram) {
          return new NGramEntryPosition(forNGram.length, i);
        } else {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Returns all keywords in their representative order as string (with space as delimiter).
   *
   * @return all keywords in their representative order as string (with space as delimiter).
   */
  @Override
  public String toString() {
    return String.join(" ", nGram);
  }

}
