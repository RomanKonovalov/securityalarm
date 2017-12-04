package com.romif.securityalarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Roman_Konovalov on 1/24/2017.
 */
@Entity
@DiscriminatorValue(value = "device")
@Data
@ToString(exclude = {"alarm", "user"})
@EqualsAndHashCode(of = "login")
public class Device extends GenericUser {

    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String description;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne(mappedBy="device")
    private Alarm alarm;

    @NotNull
    @Column(name = "apn", length = 50)
    private String apn;

    @Column(name = "config_status", length = 50)
    private ConfigStatus configStatus = ConfigStatus.NOT_CONFIGURED;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "traffic")
    private BigDecimal traffic;

}
