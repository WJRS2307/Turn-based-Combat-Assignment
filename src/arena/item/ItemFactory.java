package arena.item;

public final class ItemFactory {
    private ItemFactory() {}

    public static Item create(int choice) {
        return switch (choice) {
            case 1 -> new Potion();
            case 2 -> new PowerStone();
            default -> new SmokeBomb();
        };
    }
}
