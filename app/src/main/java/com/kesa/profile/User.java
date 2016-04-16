package com.kesa.profile;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true)
public class User {

    @Setter
    @Getter
    private String uid;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String program;

    @Getter
    @Setter
    private String mobile;

    @Getter
    @Setter
    private String profileImage;

    public User() {
        // Empty default constructor, necessary for Firebase to be able to deserialize.
    }
}
