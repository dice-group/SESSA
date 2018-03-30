package org.aksw.sessa.candidate;

/**
 * This class is used to store a candidate for a n-gram and its energy. The energy approximates the
 * probability that a node should be part of or lead to the solution generated by SESSA.
 */
public class Candidate {

  private String key;
  private String uri;
  private float energy;

  /**
   * Constructs a Candidate with its content. More formally it contains the URI that was found for a
   * n-gram. The key will be empty (""). The energy score will be default (1). This constructor is
   * mainly used for testing purposes.
   *
   * @param uri URI found for a n-gram that is used as candidate
   */
  public Candidate(String uri) {
    this(uri, "", 1);
  }

  /**
   * Constructs a Candidate with its content. More formally it contains the URI that was found for a
   * n-gram and the key of the dictionary-entry, where the URI was found. The energy score will be
   * default (1).
   *
   * @param uri URI found for a n-gram that is used as candidate
   * @param key keyword that was used to find the URI
   */
  public Candidate(String uri, String key) {
    this(uri, key, 1);
  }

  /**
   * Constructs a Candidate with its content. More formally it contains the URI that was found for a
   * n-gram and its energy.
   *
   * @param uri URI found for a n-gram that is used as candidate
   * @param key keyword that was used to find the URI
   * @param energy energy value to the found URI
   */
  public Candidate(String uri, String key, float energy) {
    this.uri = uri;
    this.key = key;
    this.energy = energy;
  }

  /**
   * Returns the key of the entry in the dictionary in which the URI was found.
   *
   * @return the key of the entry in the dictionary in which the URI was found
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the URI for this candidate.
   *
   * @return the URI for this candidate
   */
  public String getUri() {
    return uri;
  }

  /**
   * Returns the energy of this candidate.
   *
   * @return the energy of this candidate
   */
  public float getEnergy() {
    return energy;
  }

  /**
   * Sets the energy score for this candidate
   *
   * @param energyScore energy value to the found URI
   */
  public void setEnergy(float energyScore) {
    this.energy = energyScore;
  }

  /**
   * Compares the specified object with this candidate for equality. More formally, the uri and key
   * will be compared. If they are the same, the objects are treated as the same
   *
   * @param other the reference object with which to compare
   * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Candidate) {
      if (!((Candidate) other).getKey().equals(this.getKey())) {
        return false;
      }
      if (!((Candidate) other).getUri().equals(this.getUri())) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns a hash code value for the object. More formally, the the uri and the key are united and
   * the result is the hash code.
   *
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode() {
    String hashCodeBase = getKey() + getUri();
    return hashCodeBase.hashCode();
  }

  @Override
  public String toString() {
    return "Candidate{" +
        "key='" + key + '\'' +
        ", uri='" + uri + '\'' +
        ", energy=" + energy +
        '}';
  }
}