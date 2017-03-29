package pro.documentum.util.lifecycle;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public enum LifeCycleOp {

    ATTACH("Attach", "Attaching new policy to document"), DETACH("Detach",
            "Detaching policy from document"), RESUME("Resume",
            "Resuming document"), SUSPEND("Suspend", "Suspending document"), DEMOTE(
            "Demote", "Demoting document"), PROMOTE("Promote",
            "Promoting document");

    private final String _name;

    private final String _lowerName;

    private final String _description;

    LifeCycleOp(final String name, final String description) {
        _name = name;
        _lowerName = _name.toLowerCase();
        _description = description;
    }

    public static LifeCycleOp of(final String name) {
        for (LifeCycleOp operation : LifeCycleOp.values()) {
            if (operation.getName().equalsIgnoreCase(name)
                    || operation.getLowerName().equalsIgnoreCase(name)) {
                return operation;
            }
        }
        return null;
    }

    public String getName() {
        return _name;
    }

    public String getLowerName() {
        return _lowerName;
    }

    public String getDescription() {
        return _description;
    }

}
