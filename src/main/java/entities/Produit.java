package com.shopfx.entities;

public class Produit {
    private int id;
    private String nom;
    private String categorie;
    private double prix;
    private int stock;

    public Produit() {}

    public Produit(int id, String nom, String categorie, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
    }

    public Produit(String nom, String categorie, double prix, int stock) {
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return nom;
    }
}
