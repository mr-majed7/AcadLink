package com.majed.acadlink.entitie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.majed.acadlink.enums.Privacy;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "folders")
@Data
@JsonIgnoreProperties({"materials"})
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    private LocalDate createdAt;

    @Column(nullable = false)
    private Privacy privacy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materials;


    /**
     * No args constructor for Hibernate
     */
    public Folder() { //For Hibernate

    }
}
