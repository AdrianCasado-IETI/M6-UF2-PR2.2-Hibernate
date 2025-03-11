package com.project;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Ciutada")
public class Ciutada implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciutadaId")
    private long ciutadaId;

    @Column(name = "nom")
    private String nom;

    @Column(name = "cognom")
    private String cognom;

    @Column(name = "edat")
    private int edat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciutatId", nullable = true)
    private Ciutat ciutat;

    public Ciutada() { }

    public Ciutada(String nom, String cognom, int edat) {
        this.nom = nom;
        this.cognom = cognom;
        this.edat = edat;
    }

    public long getCiutadaId() {
        return ciutadaId;
    }

    public Ciutat getCiutat() {
        return ciutat;
    }

    public String getCognom() {
        return cognom;
    }

    public int getEdat() {
        return edat;
    }

    public String getNom() {
        return nom;
    }

    public void setCiutadaId(long ciutadaId) {
        this.ciutadaId = ciutadaId;
    }

    public void setCiutat(Ciutat ciutat) {
        this.ciutat = ciutat;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public void setEdat(int edat) {
        this.edat = edat;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Ciutada{" +
                "ciutadaId=" + ciutadaId +
                ", nom='" + nom + '\'' +
                ", cognom='" + cognom + '\'' +
                ", edat=" + edat +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ciutada ciutada = (Ciutada) obj;

        return ciutadaId == ciutada.ciutadaId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ciutadaId);
    }
}
