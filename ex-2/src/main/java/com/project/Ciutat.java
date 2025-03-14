package com.project;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Ciutat")
public class Ciutat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciutatId")
    private long ciutatId;

    @Column(name = "nom")
    private String nom;

    @Column(name = "pais")
    private String pais;

    @Column(name = "poblacio")
    private int poblacio;

    @OneToMany(mappedBy = "ciutat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ciutada> ciutadans = new HashSet<>();

    public Ciutat() { }

    public Ciutat(String nom, String pais, int poblacio) {
        this.nom = nom;
        this.pais = pais;
        this.poblacio = poblacio;
    }

    public Set<Ciutada> getCiutadans() {
        return ciutadans;
    }

    public long getCiutatId() {
        return ciutatId;
    }

    public int getPoblacio() {
        return poblacio;
    }

    public String getNom() {
        return nom;
    }

    public String getPais() {
        return pais;
    }

    public void addCiutada(Ciutada ciutada) {
        ciutadans.add(ciutada);
        ciutada.setCiutat(this);
    }

    public void setCiutadans(Set<Ciutada> ciutadans) {
        if(ciutadans != null) {
            this.ciutadans = ciutadans;
        }
    }

    public void setCiutatId(long ciutatId) {
        this.ciutatId = ciutatId;
    }

    public void setPoblacio(int poblacio) {
        this.poblacio = poblacio;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void removeCiutada(Ciutada ciutada) {
        ciutadans.remove(ciutada);
        ciutada.setCiutat(null);
    }

    @Override
    public String toString() {
        return "Ciutat{" +
                "ciutatId=" + ciutatId +
                ", nom='" + nom + '\'' +
                ", pais='" + pais + '\'' +
                ", poblacio=" + poblacio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ciutat ciutat = (Ciutat) o;
        return ciutatId == ciutat.ciutatId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ciutatId);
    }
}
