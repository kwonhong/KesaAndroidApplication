package com.kesa.user;


import android.graphics.Bitmap;

import com.orm.SugarRecord;
import com.orm.dsl.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true)
public class User extends SugarRecord{

    @Setter
    @Getter
    @Column(name = "uid", notNull = true, unique = true)
    private String uid;

    @Getter
    @Setter
    @Column(name = "firstName", notNull = true, unique = false)
    private String firstName;

    @Getter
    @Setter
    @Column(name = "lastName", notNull = true, unique = false)
    private String lastName;

    @Getter
    @Setter
    @Column(name = "program", notNull = true, unique = false)
    private String program;

    @Getter
    @Setter
    @Column(name = "mobile", notNull = true, unique = false)
    private String mobile;

    /**
     * Contains two different values depending on the circumstances.
     * 1. Base64 String encoded {@link Bitmap} for external storage.
     * 2. Absolute path for internal storage.
     */
    @Getter
    @Setter
    @Column(name = "profileImage", notNull = true, unique = false)
    private String profileImage;

    @Getter
    @Setter
    @Column(name = "email", notNull = true, unique = false)
    private String email;

    @Getter
    @Setter
    @Column(name = "admissionYear", unique = false)
    private int admissionYear;

    @Getter
    @Setter
    @Column(name = "isExecutive", unique = false)
    private boolean isExecutive;

    @Getter
    @Setter
    @Column(name = "isContactPublic", unique = false)
    private boolean isContactPublic;

    public User() {
        // Empty default constructor, necessary for Firebase to be able to deserialize.
    }

    public static String getAdmissionYearInString(int admissionYear) {
        int graduationYear = admissionYear + 4;
        int lastDigit = graduationYear % 10;
        int secondLastDigit = (graduationYear/10) % 10;
        return secondLastDigit + "T" + lastDigit;
    }

    public static String getFullName(User user) {
        return user.firstName + " " + user.lastName;
    }

}
