package com.romif.securityalarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.romif.securityalarm.config.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/24/2017.
 */
@Entity
@DiscriminatorValue(value = "device")
public class Device extends GenericUser {

    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String description;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = true)
    private User user;

    @OneToOne(optional=true, mappedBy="device")
    private Alarm alarm;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
