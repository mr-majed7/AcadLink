package com.majed.acadlink.domain.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

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

    @Column(nullable = false)
    private Privacy privacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    @JsonBackReference
    private Folder folder;
}
