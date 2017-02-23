package com.romif.securityalarm.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device_credentials")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name="device_id")
    private Device device;

    @NotNull
    @Column(name = "raw_password")
    private String rawPassword;

    @NotNull
    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "pause_token")
    private String pauseToken;

    @NotNull
    @Size(min = 8, max = 8)
    @Column(name = "secret",length = 8)
    private String secret;

}
