package com.kesa.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true)
public class Event {

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(<class_name>)
    }

    @Setter @Getter
    private String uid;

    @Setter @Getter
    private String title;

    @Setter @Getter
    private String description;

    @Setter @Getter
    private String location;

    @Setter @Getter
    private long timestamp;

    @Setter @Getter
    private String hostUid;

    @Setter @Getter
    private String image;
}
