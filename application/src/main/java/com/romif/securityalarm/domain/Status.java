package com.romif.securityalarm.domain;


import com.romif.securityalarm.api.dto.DeviceState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Status.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "status")
@Data
@ToString(exclude = {"image", "thumbnail"})
@NoArgsConstructor
public class Status extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_state")
    @Enumerated(EnumType.STRING)
    private DeviceState deviceState;

    @Column(name = "latitude")
    private float latitude;

    @Column(name = "longitude")
    private float longitude;

    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Transient
    @JsonProperty
    private byte[] image;

}
