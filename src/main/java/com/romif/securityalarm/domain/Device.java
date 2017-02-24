package com.romif.securityalarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.romif.securityalarm.config.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/24/2017.
 */
@Entity
@DiscriminatorValue(value = "device")
@Data
@ToString(exclude = {"alarm", "user"})
@EqualsAndHashCode(callSuper = false, of = "login")
public class Device extends GenericUser {

    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String description;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = true)
    private User user;

    @OneToOne(optional=true, mappedBy="device")
    private Alarm alarm;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "pause_token_hash",length = 60)
    private String pauseToken;


    @NotNull
    @Column(name = "apn", length = 50)
    private String apn;

}
