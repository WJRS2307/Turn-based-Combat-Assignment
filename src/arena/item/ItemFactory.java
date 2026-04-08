package arena.item;

public final class ItemFactory {
    private ItemFactory() {}

    public static Item create(ItemType type) {
        return switch (type) {
            case POTION -> new Potion();
            case POWER_STONE -> new PowerStone();
            case SMOKE_BOMB-> new SmokeBomb();
        };
    }

    public static ItemType fromChoice(int choice) {
        return switch (choice) {
            case 1 -> ItemType.POTION;
            case 2 -> ItemType.POWER_STONE;
            case 3 -> ItemType.SMOKE_BOMB;
            default -> throw new IllegalArgumentException("Invalid item choice");
        };
    }
    }
