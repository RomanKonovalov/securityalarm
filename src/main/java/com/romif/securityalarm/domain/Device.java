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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
