public enum Type {
  CONJUNCTIVE("Коньюнктивная"),
  DISJUNCTIVE("Дизъюнктивная"),
  DUAL_GAME("Двойственная"),
  BALANCED_GAME("Взвешенная"),
  CONJUNCTIVE_BALANCED("Взвешенная конъюнктивная"),
  DISJUNCTIVE_BALANCED("Взвешенная дизъюнктивная");
  private String name;

  Type(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
