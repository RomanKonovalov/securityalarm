package com.romif.securityalarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.ZonedDateTime;

/**
 * A user.
 */
@Entity
@Data
@ToString(exclude = "devices")
@DiscriminatorValue(value = "user")
public class User extends GenericUser {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "additional_phone", length = 50)
    private String additionalPhone;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column(name = "notification_types")
    private EnumSet<NotificationType> notificationTypes;

    @Column(name = "tracking_types")
    private EnumSet<TrackingType> trackingTypes;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date")
    private ZonedDateTime resetDate = null;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    //@JoinColumn(name="device_id")
    private Set<Device> devices = new HashSet<>();

    @ElementCollection(targetClass=String.class)
    @CollectionTable(name = "mac_address")
    private Set<String> macAddresses;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return getLogin().equals(user.getLogin());
    }

    @Override
    public int hashCode() {
        return getLogin().hashCode();
    }

}
