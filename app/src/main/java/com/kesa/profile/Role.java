package com.kesa.profile;

import lombok.Getter;

/**
 * Created by james on 16-04-19.
 */
public enum Role {
    MEMBER(0, "Member"),
    PRESIDENT(1, "President", Group.EXECUTIVE),
    VICE_PRESIDENT(2, "Vice President", Group.EXECUTIVE),
    IT_DIRECTOR(3, "IT Director", Group.EXECUTIVE);

    private final Group[] groups;
    @Getter private final String label;
    @Getter private final int roleId;

    Role(int roleId, String label, Group... groups) {
        this.groups = groups;
        this.label = label;
        this.roleId = roleId;
    }

    public static Role getRole(int roleId) {
        for (Role role : values()) {
            if (role.roleId == roleId) {
                return role;
            }
        }

        throw new IllegalArgumentException();
    }

    public boolean isInGroup(Group group) {
        if (groups == null) {
            return false;
        }

        for (Group currentGroup : groups) {
            if (currentGroup == group) {
                return true;
            }
        }

        return false;
    }


    public enum Group {
        EXECUTIVE
    }
}
