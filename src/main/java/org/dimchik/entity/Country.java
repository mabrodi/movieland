package org.dimchik.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;
}
