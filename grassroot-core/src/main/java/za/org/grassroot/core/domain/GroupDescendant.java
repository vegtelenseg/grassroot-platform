package za.org.grassroot.core.domain;

/**
 * Represents the entity that is contained inside a group, maybe directly or not.
 */
public interface GroupDescendant {
	/**
	 * Returns group that contains this entity (because some entities can have parents that are not groups).
	 * This is basically direct relationship to parent group, even if parent is not direct one.
	 * @return ancestor group that contains this entity
	 */
	Group getAncestorGroup();
}
