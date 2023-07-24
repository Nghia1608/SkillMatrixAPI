package com.phuquy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "form_domain")
public class FormDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;
    @ManyToOne
    @JoinColumn(name = "domain_id")
    private SkillDomain domain;

}

