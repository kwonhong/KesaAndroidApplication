package com.kesa.profile;


import lombok.Getter;
import lombok.Setter;

/**
 * TODO(hongil): Add Javadoc
 */
public class User {

    @Getter
    private final String uid;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String field;

    public User(String uid, String name, String field) {
        this.uid = uid;
        this.name = name;
        this.field = field;
    }
}
