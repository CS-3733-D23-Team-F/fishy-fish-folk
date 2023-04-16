package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.Move;
import java.time.LocalDate;
import java.util.*;
import javafx.geometry.Point2D;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int size;
  double[][] adjMat;
  MicroNode[] unodes;
  HashMap<Integer, Integer> id2idx; // string id to index in nodes array and adjacency matrix
  HashMap<String, ArrayList<Integer>> elevLet2ids;
  private int lastIdx;

  private Fdb dbConnection;

  public Graph(Fdb dbConnection) {

    this.dbConnection = dbConnection;

    this.size = dbConnection.micronodeTable.size();

    adjMat = new double[size][size];
    unodes = new MicroNode[size];
    id2idx = new HashMap<>(size * 4 / 3 + 1); // default load factor is 75% = 3/4
    elevLet2ids = new HashMap<>(32); // estimate of number of elevator nodes

    lastIdx = 0;

    populate();
  }

  private static void accept(String[] move) {
    move[2] = Move.sanitizeDate(move[2]);
  }

  public void populate() {

    ArrayList<String[]> unodesLst = dbConnection.micronodeTable.getAll();
    ArrayList<String[]> moves = dbConnection.moveTable.getAll();
    ArrayList<String[]> locations = dbConnection.locationTable.getAll();
    ArrayList<String[]> edges = dbConnection.edgeTable.getAll();

    // put unodes into array and record in id2idx
    Iterator<String[]> itr = unodesLst.iterator();
    int idx = 0;
    while (itr.hasNext()) {

      MicroNode unode = new MicroNode();
      unode.construct(new ArrayList<>(List.of(itr.next())));

      unodes[idx] = unode;

      id2idx.put(Integer.parseInt(unode.id), idx);
      idx++;

      // record node ids of elevator nodes
      if (unode.containsType(NodeType.ELEV)) {

        for (String letter : unode.getElevLetters()) {

          // add nid to the list of ids associated with the elevator's letter
          ArrayList<Integer> lst = new ArrayList<>();
          lst.add(unode.nid);
          elevLet2ids.merge(
              letter,
              lst,
              (lst1, lst2) -> {
                lst1.addAll(lst2);
                return lst1;
              });
        }
      }
    }

    // compare moves by the natural order of their dates

    // ensure move dates follow the above format
    moves.forEach(
        move -> {
          move[2] = Move.sanitizeDate(move[2]);
        });

    moves.sort(Comparator.comparing(move -> LocalDate.parse(move[2], Move.format)));

    HashMap<String, Integer> lname2id = new HashMap<>(size * 4 / 3 + 1);

    itr = moves.iterator();
    while (itr.hasNext()) {
      String[] move = itr.next();
      // one unode per location. also sorted by date so later moves overwrite previous mappings
      lname2id.put(move[1], Integer.parseInt(move[0]));
    }

    // associate locations to unodes
    locations.forEach(
        elt -> {
          Location loc = new Location();
          loc.construct(new ArrayList<>(List.of(elt)));

          // from lname -> get id -> get idx
          int i = id2idx.get(lname2id.get(loc.longname));

          // 2 way connection from location <-> unode
          loc.assign(unodes[i]);
          unodes[i].addLocation(loc);
        });

    edges.forEach(
        edge -> {
          int n1 = Integer.parseInt(edge[0]);
          int n2 = Integer.parseInt(edge[1]);
          addEdge(n1, n2);
        });

    // TODO test different values for this cost
    int edgeCount = edges.size() + connectElevators(250);

    System.out.println(
        "[Graph.populate]: Populated graph with "
            + unodes.length
            + " nodes and "
            + edgeCount
            + " edges.");
  }

  public void resize(int newSize) {

    double[][] newAdjMat = new double[newSize][newSize];
    MicroNode[] newNodes = new MicroNode[newSize];
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
      newNodes[i] = unodes[i];
    }

    // copy id2idx map
    for (Map.Entry<Integer, Integer> entry : id2idx.entrySet()) {
      newid2idx.put(entry.getKey(), entry.getValue());
    }

    size = newSize;
  }

  public boolean removeNode(String id) {

    Integer idx = id2idx.get(id);

    if (id2idx.remove(idx) == null) { // requested node is not in graph
      return false;
    }

    unodes[idx] = null; // remove from nodes array
    return false;
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
      if (unodes[idx1].containsType(NodeType.STAI) && unodes[idx2].containsType(NodeType.STAI)) {
        adjust = 250;
      }

      // System.out.println(edge.id);

      Point2D point1 = unodes[idx1].point;
      Point2D point2 = unodes[idx2].point;

      adjMat[idx1][idx2] = point1.distance(point2) + adjust;
      adjMat[idx2][idx1] = point1.distance(point2) + adjust;
      unodes[idx1].degree++;
      unodes[idx2].degree++;

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
          unodes[idxi].degree++;
          unodes[idxj].degree++;
        }
      }
    }

    return count;
  }

  public Path bfs(int start, int end) {

    // check for correctness by inputting edge list into https://graphonline.ru/en/

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    boolean[] visited = new boolean[size];

    LinkedList<Integer> queue = new LinkedList<>(); // queue of next nodes to look at in bfs

    int[] previous =
        new int[size]; // used to store the ids of the previous node in order to retrace the path

    queue.add(start);

    while (!queue.isEmpty()) {

      int cur = queue.removeFirst();

      if (cur == end) { // reached end
        Path path = new Path();

        path.addFirst(end, unodes[id2idx.get(end)].point);

        while (cur != start) { // retrace path from the end to the start
          int prev = previous[id2idx.get(cur)];
          path.addFirst(prev, unodes[id2idx.get(prev)].point);
          cur = prev;
        }

        return path;
      }

      if (!visited[id2idx.get(cur)]) { // found a new node

        for (int other = 0; other < size; other++) {

          if (adjMat[id2idx.get(cur)][other] != 0.0 // Fixed to 0.0 now that matrix is edge weights
              && !visited[other]) { // 1 means cur is connected to other
            int next = unodes[other].nid;
            previous[id2idx.get(next)] = cur;
            queue.addLast(next);
          }

          visited[id2idx.get(cur)] = true;
        }
      }
    }

    return null;
  }

  public ArrayList<Path> AStar(int start, int end) {

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    double[] fromStart = new double[size];
    double[] heuristic = new double[size];
    boolean[] visited = new boolean[size]; // true means found the shortest path to this node
    int[] lastVisited = new int[size];
    int current_node = id2idx.get(start);

    // initialize distance from start node and heuristic arrays
    // distance from start node: 0 for start, -MIN_INT for all others
    // heuristic: approximate distances from all points to the target endpoint
    String endFloor = unodes[id2idx.get(end)].floor;
    Point2D endPoint = unodes[id2idx.get(end)].point;

    for (int node = 0; node < size; node++) {

      fromStart[node] = Integer.MIN_VALUE;
      heuristic[node] = Integer.MIN_VALUE;
    }

    fromStart[id2idx.get(start)] = 0;

    while (!(current_node == id2idx.get(end))) {

      visited[current_node] = true;

      // System.out.println(nodes[current_node].id);

      // update adjacent nodes
      for (int node = 0; node < size; node++) {
        if (!(adjMat[current_node][node] == 0.0) // Check if nodes are adjacent
            && !visited[node]) { // Make sure unvisited node

          // if going to adjacent node via current is better than the previous path to node, update
          // fromStart[node]
          if (fromStart[node] < 0
              || fromStart[current_node] + adjMat[current_node][node] < fromStart[node]) {

            fromStart[node] = fromStart[current_node] + adjMat[current_node][node];
            lastVisited[node] = current_node;
          }
        }
      }

      // choose the next node based off of distance to start and heuristic
      double min = Integer.MAX_VALUE;

      for (int other = 0; other < size; other++) {

        if (!visited[other] && fromStart[other] > -1) {

          if (heuristic[other] < -501 && unodes[other].floor.equals(endFloor)) {
            heuristic[other] = unodes[other].point.distance(endPoint) - 500;
          } else if (heuristic[other] < -501) {
            heuristic[other] = unodes[other].point.distance(endPoint);
          }

          double cost = fromStart[other] + heuristic[other];

          if (cost >= -501 && cost < min) {
            min = cost;
            current_node = other;
          }
        }
      }
    }

    String currentFloor = "";
    ArrayList<Path> paths = new ArrayList<>();
    int numpaths = -1;

    // retrace path from the end to the start
    while (!(current_node == id2idx.get(start))) {

      // start new path when hitting a new floor
      if (!unodes[current_node].floor.equals(currentFloor)) {
        Path path = new Path();
        currentFloor = unodes[current_node].floor;
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;
      }

      // add current node at beginning of first path
      paths.get(0).addFirst(unodes[current_node].nid, unodes[current_node].point);

      current_node = lastVisited[current_node];
    }
    // add start node to beginning of first path
    paths.get(0).addFirst(start, unodes[current_node].point);

    System.out.println(paths.toString());

    // path.addFirst(start, nodes[id2idx.get(start)].point);

    // System.out.println(path.pathLength());

    // System.out.println(path.getDirections());

    return paths;
  }

  public double distance(String n1, String n2) {
    return unodes[id2idx.get(n1)].point.distance(unodes[id2idx.get(n2)].point);
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

  public ArrayList<String> adjacentNodes(int nodeId) {

    ArrayList<String> neighbors = new ArrayList<>();
    int idx = id2idx.get(nodeId);
    for (int i = 0; i < size; i++) {
      if (adjMat[idx][i] != 0) {
        neighbors.add(unodes[i].id);
      }
    }
    return neighbors;
  }
}
