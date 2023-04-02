package edu.wpi.fishfolk.pathfinding;

public enum NodeType {
  CONF,
  DEPT,
  HALL,
  LABS,
  REST,
  RETL,
  SERV,
  ELEV,
  EXIT,
  STAI;

  public boolean isItNodeType(String string){
    if (string.toUpperCase().equals("CONF") ||
            string.toUpperCase().equals("DEPT") ||
            string.toUpperCase().equals("HALL") ||
            string.toUpperCase().equals("LABS") ||
            string.toUpperCase().equals("REST") ||
            string.toUpperCase().equals("RETL") ||
            string.toUpperCase().equals("SERV") ||
            string.toUpperCase().equals("ELEV") ||
            string.toUpperCase().equals("EXIT") ||
            string.toUpperCase().equals("STAI")){
      return true;
    }
    return false;
  }

  public NodeType makeStringIntoNodeType(String string){
    if (string.toUpperCase().equals("CONF")) {
      return NodeType.CONF;
    } else if (string.toUpperCase().equals("DEPT")){
      return NodeType.DEPT;
    } else if (string.toUpperCase().equals("HALL")){
      return NodeType.HALL;
    } else if (string.toUpperCase().equals("LABS")) {
      return NodeType.LABS;
    } else if (string.toUpperCase().equals("REST")) {
      return NodeType.REST;
    } else if (string.toUpperCase().equals("RETL")) {
      return NodeType.RETL;
    } else if (string.toUpperCase().equals("SERV")) {
      return NodeType.SERV;
    } else if (string.toUpperCase().equals("ELEV")) {
      return NodeType.ELEV;
    } else if (string.toUpperCase().equals("EXIT")) {
      return NodeType.EXIT;
    }
    return NodeType.STAI;
  }
}
