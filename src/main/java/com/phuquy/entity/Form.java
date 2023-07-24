package com.phuquy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "form")
public class Form {
    @Id
    @Column(name = "form_id")
    private long formID;
    @Column(name = "form_name")
    private String formName;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    private User userID;
    @Column(name = "create_date")
    private String createDate;
    @Column(name = "form_describe")
    private String formDescribe;
    @OneToMany(mappedBy = "form")
    @JsonIgnore
    private List<UserRate> userRates;
    @OneToMany(mappedBy = "form")
    @JsonIgnore
    private List<FormDomain> formDomains;
    @OneToMany(mappedBy = "form")
    @JsonIgnore
    private List<FormParticipant> formParticipants;

}

