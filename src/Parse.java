import java.util.Scanner;

public class Parse {
    private static final int PROPERTY_KEY = 0;

    public static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    public static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 5;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;
    private static final int OBSTACLE_ANIMATION_PERIOD = 4;

    public static final String DUDE_KEY = "dude";
    private static final int DUDE_NUM_PROPERTIES = 7;
    private static final int DUDE_ID = 1;
    private static final int DUDE_COL = 2;
    private static final int DUDE_ROW = 3;
    private static final int DUDE_LIMIT = 4;
    private static final int DUDE_ACTION_PERIOD = 5;
    private static final int DUDE_ANIMATION_PERIOD = 6;

    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final int SAPLING_ACTION_ANIMATION_PERIOD = 1000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_NUM_PROPERTIES = 4;
    private static final int SAPLING_ID = 1;
    private static final int SAPLING_COL = 2;
    private static final int SAPLING_ROW = 3;
    private static final int SAPLING_HEALTH = 4;

    public static final String HOUSE_KEY = "house";
    private static final int HOUSE_NUM_PROPERTIES = 4;
    private static final int HOUSE_ID = 1;
    private static final int HOUSE_COL = 2;
    private static final int HOUSE_ROW = 3;

    public static final String FAIRY_KEY = "fairy";
    private static final int FAIRY_NUM_PROPERTIES = 6;
    private static final int FAIRY_ID = 1;
    private static final int FAIRY_COL = 2;
    private static final int FAIRY_ROW = 3;
    private static final int FAIRY_ANIMATION_PERIOD = 4;
    private static final int FAIRY_ACTION_PERIOD = 5;

    public static final String GHOST_KEY = "ghost";
    private static final int GHOST_NUM_PROPERTIES = 6;
    private static final int GHOST_ID = 1;
    private static final int GHOST_COL = 2;
    private static final int GHOST_ROW = 3;
    private static final int GHOST_ANIMATION_PERIOD = 4;
    private static final int GHOST_ACTION_PERIOD = 5;

    public static final String KNIGHT_KEY = "knight";
    private static final int KNIGHT_NUM_PROPERTIES = 6;
    private static final int KNIGHT_ID = 1;
    private static final int KNIGHT_COL = 2;
    private static final int KNIGHT_ROW = 3;
    private static final int KNIGHT_ANIMATION_PERIOD = 4;
    private static final int KNIGHT_ACTION_PERIOD = 5;

    public static final String STUMP_KEY = "stump";

    public static final String TREE_KEY = "tree";
    private static final int TREE_NUM_PROPERTIES = 7;
    private static final int TREE_ID = 1;
    private static final int TREE_COL = 2;
    private static final int TREE_ROW = 3;
    private static final int TREE_ANIMATION_PERIOD = 4;
    private static final int TREE_ACTION_PERIOD = 5;
    private static final int TREE_HEALTH = 6;

    public static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            world.setBackground(pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    public static boolean parseSapling(
            String[] properties,WorldModel world, ImageStore imageStore)
    {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[SAPLING_COL]),
                    Integer.parseInt(properties[SAPLING_ROW]));
            String id = properties[SAPLING_ID];
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = new Sapling(id, pt, imageStore.getImageList(SAPLING_KEY),
                    SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, health, SAPLING_HEALTH_LIMIT);
            world.tryAddEntity(entity);
        }

        return properties.length == SAPLING_NUM_PROPERTIES;
    }

    public static boolean parseDude(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[DUDE_COL]),
                    Integer.parseInt(properties[DUDE_ROW]));
            Entity entity = Factory.createDudeNotFull(properties[DUDE_ID],
                    pt,
                    Integer.parseInt(properties[DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[DUDE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[DUDE_LIMIT]),
                    imageStore.getImageList(DUDE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == DUDE_NUM_PROPERTIES;
    }

    public static boolean parseFairy(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[FAIRY_COL]),
                    Integer.parseInt(properties[FAIRY_ROW]));
            AnimationEntity entity = Factory.createFairy(properties[FAIRY_ID],
                    pt,
                    Integer.parseInt(properties[FAIRY_ACTION_PERIOD]),
                    Integer.parseInt(properties[FAIRY_ANIMATION_PERIOD]),
                    imageStore.getImageList(FAIRY_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == FAIRY_NUM_PROPERTIES;
    }

    public static boolean parseTree(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[TREE_COL]),
                    Integer.parseInt(properties[TREE_ROW]));
            Entity entity = Factory.createTree(properties[TREE_ID],
                    pt,
                    Integer.parseInt(properties[TREE_ACTION_PERIOD]),
                    Integer.parseInt(properties[TREE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[TREE_HEALTH]),
                    imageStore.getImageList(TREE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == TREE_NUM_PROPERTIES;
    }

    public static boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = Factory.createObstacle(properties[OBSTACLE_ID], pt,
                    Integer.parseInt(properties[OBSTACLE_ANIMATION_PERIOD]),
                    imageStore.getImageList(
                            OBSTACLE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public static boolean parseHouse(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[HOUSE_COL]),
                    Integer.parseInt(properties[HOUSE_ROW]));
            Entity entity = Factory.createHouse(properties[HOUSE_ID], pt,
                    imageStore.getImageList(
                            HOUSE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == HOUSE_NUM_PROPERTIES;
    }

    public static void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                e.getMessage()));
            }
            lineNumber++;
        }
    }

    public static boolean parseGhost(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == GHOST_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[GHOST_COL]),
                    Integer.parseInt(properties[GHOST_ROW]));
            AnimationEntity entity = Factory.createGhost(properties[GHOST_ID],
                    pt,
                    Integer.parseInt(properties[GHOST_ACTION_PERIOD]),
                    Integer.parseInt(properties[GHOST_ANIMATION_PERIOD]),
                    imageStore.getImageList(GHOST_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == GHOST_NUM_PROPERTIES;
    }

    public static boolean parseKnight(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == KNIGHT_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[KNIGHT_COL]),
                    Integer.parseInt(properties[KNIGHT_ROW]));
            AnimationEntity entity = Factory.createKnight(properties[KNIGHT_ID],
                    pt,
                    Integer.parseInt(properties[KNIGHT_ACTION_PERIOD]),
                    Integer.parseInt(properties[KNIGHT_ANIMATION_PERIOD]),
                    imageStore.getImageList(KNIGHT_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == KNIGHT_NUM_PROPERTIES;
    }



    private static boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case DUDE_KEY:
                    return parseDude(properties, world, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case FAIRY_KEY:
                    return parseFairy(properties, world, imageStore);
                case HOUSE_KEY:
                    return parseHouse(properties, world, imageStore);
                case TREE_KEY:
                    return parseTree(properties, world, imageStore);
                case SAPLING_KEY:
                    return parseSapling(properties, world, imageStore);
                case GHOST_KEY:
                    return parseGhost(properties, world, imageStore);
                case KNIGHT_KEY:
                    return parseKnight(properties, world, imageStore);
            }
        }

        return false;
    }
}
