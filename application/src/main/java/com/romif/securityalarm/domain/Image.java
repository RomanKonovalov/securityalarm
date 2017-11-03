package com.romif.securityalarm.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@EqualsAndHashCode
@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    public Status status;

    @Column
    private byte[] rawImage;

    @Column
    private ZonedDateTime dateTime;

}
