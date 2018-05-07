package model;

import model.interfaces.ClientInterface;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "clients")
public class Client implements Serializable,ClientInterface {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private int id;
    @Column
    private String name;
    @Column
    private String cui;
    @Column
    private String nr;
    @Column
    private String sediu;
    @Column
    private String judet;
    @Column
    private String cont;
    @Column
    private String banca;

    public Client() {
    }


    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    @Override
    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    @Override
    public String getSediu() {
        return sediu;
    }

    public void setSediu(String sediu) {
        this.sediu = sediu;
    }

    @Override
    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    @Override
    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    @Override
    public String getBanca() {
        return banca;
    }

    public void setBanca(String banca) {
        this.banca = banca;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientName='" + name + '\'' +
                ", cui='" + cui + '\'' +
                ", nr='" + nr + '\'' +
                ", sediu='" + sediu + '\'' +
                ", judet='" + judet + '\'' +
                ", cont='" + cont + '\'' +
                ", banca='" + banca + '\'' +
                '}';
    }
}
