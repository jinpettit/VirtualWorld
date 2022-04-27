public class ActivityAction implements Action {
    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public ActivityAction(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        switch (this.entity.getKind()) {
            case SAPLING:
                this.entity.executeSaplingActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case TREE:
                this.entity.executeTreeActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case FAIRY:
                this.entity.executeFairyActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case DUDE_NOT_FULL:
                this.entity.executeDudeNotFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case DUDE_FULL:
                this.entity.executeDudeFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        this.entity.getKind()));

        }
    }
}



