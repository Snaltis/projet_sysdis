package com.service_inventory.get_stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Fournisseurs")
public class FournisseurModel{
    @Id
    @Column(name = "nom")
    private String nom;
}
