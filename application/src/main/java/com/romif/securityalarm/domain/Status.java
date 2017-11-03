package com.romif.securityalarm.domain;


import com.romif.securityalarm.api.dto.DeviceState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * A Status.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "status")
@Data
@ToString(exclude = {"images", "thumbnail"})
@NoArgsConstructor
public class Status extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_state")
    @Enumerated(EnumType.STRING)
    private DeviceState deviceState;

    @OneToOne(cascade = CascadeType.ALL)
    private Location location;

    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    private List<Image> images;

}
