package com.shopfx.entities;

import java.time.LocalDateTime;

public class Commande {
    private int id;
    private String numeroUnique;
    private int clientId;
    private LocalDateTime date;
    private String statut;
    private String clientNom; // Pour l'affichage

    public Commande() {
    }

    public Commande(int id, String numeroUnique, int clientId, LocalDateTime date, String statut) {
        this.id = id;
        this.numeroUnique = numeroUnique;
        this.clientId = clientId;
        this.date = date;
        this.statut = statut;
    }

    public Commande(String numeroUnique, int clientId, LocalDateTime date, String statut) {
        this.numeroUnique = numeroUnique;
        this.clientId = clientId;
        this.date = date;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroUnique() {
        return numeroUnique;
    }

    public void setNumeroUnique(String numeroUnique) {
        this.numeroUnique = numeroUnique;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }
}
