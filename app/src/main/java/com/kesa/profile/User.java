package com.kesa.profile;


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
    @Column(name = "name", notNull = true, unique = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "program", notNull = true, unique = false)
    private String program;

    @Getter
    @Setter
    @Column(name = "mobile", notNull = true, unique = false)
    private String mobile;

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
    @Column(name = "roleId", unique = false)
    private int roleId;

    @Getter
    @Setter
    @Column(name = "contactPublic", unique = false)
    private boolean isContactPublic;

    public User() {
        // Empty default constructor, necessary for Firebase to be able to deserialize.
    }

    public static String getAdmissionYearInString(int admissionYear) {
        int lastDigit = admissionYear % 10;
        int secondLastDigit = (admissionYear/10) % 10;
        return secondLastDigit + "T" + lastDigit;
    }

}
