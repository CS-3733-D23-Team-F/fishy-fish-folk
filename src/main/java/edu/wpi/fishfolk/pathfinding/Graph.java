package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.TableEntry.Edge;
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import java.util.*;
import javafx.geometry.Point2D;
import lombok.Getter;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  @Getter private int size;
  @Getter private double[][] adjMat;
  @Getter private Node[] nodes;

  // string id to index in nodes array and adjacency matrix
  @Getter private HashMap<Integer, Integer> id2idx;

  @Getter private HashMap<String, ArrayList<Integer>> elevLet2ids;

  private Fdb dbConnection;

  public Graph(Fdb dbConnection) {

    this.dbConnection = dbConnection;

    this.size = dbConnection.getNumNodes();

    adjMat = new double[size][size];
    nodes = new Node[size];
    id2idx = new HashMap<>(size * 4 / 3 + 1); // default load factor is 75% = 3/4
    elevLet2ids = new HashMap<>(32); // estimate of number of elevator nodes

    populate();
  }

  public void populate() {

    // get Nodes from table into array and record elevators

    int[] nodeCount = {
      0
    }; // array is technically final but allows modification of elements inside lambda
    nodes =
        dbConnection.getAllEntries(TableEntryType.NODE).stream()
            .map(
                elt -> {
                  Node node = (Node) elt;

                  id2idx.put(node.getNodeID(), nodeCount[0]);
                  nodeCount[0]++;

                  if (node.containsType(NodeType.ELEV)) {
                    for (String letter : node.getElevLetters()) {

                      // add nid to the list of ids associated with the elevator's letter
                      ArrayList<Integer> lst = new ArrayList<>(List.of(node.getNodeID()));
                      elevLet2ids.merge(
                          letter,
                          lst,
                          (lst1, lst2) -> {
                            lst1.addAll(lst2);
                            return lst1;
                          });
                    }
                  }
                  return node;
                })
            .toArray(Node[]::new);

    int[] edgeCount = {0};
    dbConnection
        .getAllEntries(TableEntryType.EDGE)
        .forEach(
            elt -> {
              Edge edge = (Edge) elt;
              addEdge(edge.getStartNode(), edge.getEndNode());
              edgeCount[0]++;
            });

    // TODO test different values for this cost
    edgeCount[0] += connectElevators(250);

    System.out.println(
        "[Graph.populate]: Populated graph with "
            + nodeCount[0]
            + " nodes and "
            + edgeCount[0]
            + " edges.");
  }

  public void resize(int newSize) {

    double[][] newAdjMat = new double[newSize][newSize];
    Node[] newNodes = new Node[newSize];
    HashMap<Integer, Integer> newid2idx = new HashMap<>(newSize * 4 / 3 + 1);

    // copy adjmat
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) { // take advantage of symmetry across main diagonal
        newAdjMat[i][j] = adjMat[i][j];
        newAdjMat[j][i] = adjMat[i][j];
      }
    }

    // copy array of nodes
    for (int i = 0; i < size; i++) {
      newNodes[i] = nodes[i];
    }

    // copy id2idx map
    for (Map.Entry<Integer, Integer> entry : id2idx.entrySet()) {
      newid2idx.put(entry.getKey(), entry.getValue());
    }

    size = newSize;
  }

  /**
   * Add the edges between the two given nodes.
   *
   * @param nid1 first node id
   * @param nid2 second node id
   * @return
   */
  public boolean addEdge(int nid1, int nid2) {

    Integer idx1 = id2idx.get(nid1);
    Integer idx2 = id2idx.get(nid2);
    int adjust = 0;

    if (idx1 != null && idx2 != null) {

      // Add weights to stairs
      // TODO: make this dependent on a boolean parameter passed by the pathfinding controller
      if (nodes[idx1].containsType(NodeType.STAI) && nodes[idx2].containsType(NodeType.STAI)) {
        adjust = 250;
      }

      // System.out.println(edge.id);

      Point2D point1 = nodes[idx1].getPoint();
      Point2D point2 = nodes[idx2].getPoint();

      adjMat[idx1][idx2] = point1.distance(point2) + adjust;
      adjMat[idx2][idx1] = point1.distance(point2) + adjust;
      return true;

    } else {
      return false;
    }
  }

  /**
   * Connect every pair of elevators in the same shaft with a fixed cost edge.
   *
   * @param cost the cost of taking the elevator
   * @return the number of elevator connections added
   */
  private int connectElevators(double cost) {

    int count = 0;

    for (String letter : elevLet2ids.keySet()) {

      ArrayList<Integer> nids = elevLet2ids.get(letter);
      int n = nids.size();

      // connect up associated ids:
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {

          int idxi = id2idx.get(i), idxj = id2idx.get(j);
          if (adjMat[idxi][idxj] <= 0) count++;
          adjMat[idxi][idxj] = cost;
          adjMat[idxj][idxi] = cost;
        }
      }
    }

    return count;
  }

  public double distance(int n1, int n2) {
    return nodes[id2idx.get(n1)].getPoint().distance(nodes[id2idx.get(n2)].getPoint());
  }

  public void speedTest(int n, boolean verbose) {
    /**
     * // record length of path and time taken to find the path between pairs of random points
     *
     * <p>Random rng = new Random(); double[][] dist_time = new double[n][2];
     *
     * <p>for (int i = 0; i < n; i++) {
     *
     * <p>try { int n1 = nodes[rng.nextInt(size)].nid; int n2 = nodes[rng.nextInt(size)].nid;
     *
     * <p>//TODO Make pathlength take is list of paths long start = System.nanoTime();
     * dist_time[i][0] = AStar(n1, n2).pathLength(); dist_time[i][1] = (System.nanoTime() - start) /
     * 1000.0; } catch (Exception e) { System.out.println(i + " " + e.getMessage()); } }
     *
     * <p>Arrays.sort( dist_time, new Comparator<double[]>() { @Override public int compare(double[]
     * dt1, double[] dt2) { return Double.compare(dt1[0], dt2[0]); } });
     *
     * <p>if (verbose) { for (int i = 0; i < n; i++) { System.out.println( String.format("d: %.2f" +
     * " t (us): %.1f", dist_time[i][0], dist_time[i][1])); } }
     *
     * <p>double totalDist = 0, totalTime = 0; for (int i = 0; i < n; i++) { totalDist +=
     * dist_time[i][0]; totalTime += dist_time[i][1]; }
     *
     * <p>System.out.println( String.format("avg d: %.2f" + " avg t (us): %.1f", totalDist / n,
     * totalTime / n)); }
     *
     * <p>public void print() {
     *
     * <p>for (int id : id2idx.keySet()) {
     *
     * <p>System.out.println(id); } }
     *
     * <p>public void countNull() {
     *
     * <p>for (int i = 0; i < size; i++) { try { String x = nodes[i].id; } catch (Exception e) {
     * System.out.println(i + "-" + e.getMessage()); } }
     */
  }

  public ArrayList<Integer> adjacentNodes(int nodeId) {

    ArrayList<Integer> neighbors = new ArrayList<>();
    int idx = id2idx.get(nodeId);
    for (int i = 0; i < size; i++) {
      if (adjMat[idx][i] != 0) {
        neighbors.add(nodes[i].getNodeID());
      }
    }
    return neighbors;
  }

  public int id2idx(int id) {
    return id2idx.get(id);
  }

  public Node getNodeFromID(int id) {
    return nodes[id2idx.get(id)];
  }

  public Node getNodeFromIdx(int idx) {
    return nodes[idx];
  }

  public boolean containsNode(int id) {
    return id2idx.containsKey(id);
  }

  public boolean adjacent(int n1, int n2) {
    return adjMat[n1][n2] > 0;
  }

  public double getEdgeWeight(int n1, int n2) {
    return adjMat[n1][n2];
  }
}
