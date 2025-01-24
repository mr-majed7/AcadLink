package com.majed.acadlink.entitie;

import com.majed.acadlink.enums.MaterialType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "materials")
@Data
public class Materials {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private MaterialType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;
}
