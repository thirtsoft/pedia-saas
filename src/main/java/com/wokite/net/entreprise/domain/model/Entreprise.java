package com.wokite.net.entreprise.domain.model;

import com.wokite.net.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "entreprise")
public class Entreprise extends BaseEntity {


    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TypeEntreprise type;

    @Column(name = "email", length = 180)
    private String email;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "adresse")
    private String adresse;

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public TypeEntreprise getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setType(TypeEntreprise type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}