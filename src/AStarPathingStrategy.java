
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<Point>();

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        HashMap<Point, Node> openHash = new HashMap<>();
        HashMap<Point, Node> closedList = new HashMap<>();

        Node curr = new Node(start, 0, start.distanceSquared(end), 0 + start.distanceSquared(end), null);
        openList.add(curr);
        while (!(withinReach.test(curr.position, end))) {
            List<Point> neighbors = potentialNeighbors.apply(curr.position)
                    .filter(canPassThrough)
                    .collect(Collectors.toList());
            for (Point p : neighbors) {
                if (!(closedList.containsKey(p))) {
                    int g_value = curr.g + 1;

                    if (openHash.containsKey(p)) {
                        if (g_value < openHash.get(p).g) {
                            openList.remove(openHash.get(p));
                            Node replace = new Node(p, g_value, p.distanceSquared(end), g_value + p.distanceSquared(end), curr);
                            openList.add(replace);
                            openHash.replace(p, replace);
                        }
                    }
                    else {
                        Node node = new Node(p, g_value, p.distanceSquared(end), g_value + p.distanceSquared(end), curr);
                        openList.add(node);
                        openHash.put(p, node);
                    }
                }
            }
            closedList.put(curr.position, curr);
            if (openList.isEmpty()){
                return path;
            }
            curr = openList.remove();
        }
        while (!(curr.position == start)) {
            path.add(curr.position);
            curr = curr.prev_node;
        }
        Collections.reverse(path);
        return path;
    }

    class Node
    {
        private Point position;
        private int g;
        private int h;
        private int f;
        private Node prev_node;

        public Node (Point position, int g, int h, int f, Node prev_node) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = f;
            this.prev_node = prev_node;
        }

        public int getF() { return f; }

    }
}
