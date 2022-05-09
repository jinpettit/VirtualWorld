public abstract class Position extends ActionEntity {
    abstract Point nextPosition(WorldModel world, Point destPos);
}
