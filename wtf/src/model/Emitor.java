package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "emitors")
public class Emitor implements Serializable{
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private int id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String seria;
    @Column
    private String nr;
    @Column
    private String ciEmitor;

    public Emitor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSeria() {
        return seria;
    }

    public void setSeria(String seria) {
        this.seria = seria;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getCiEmitor() {
        return ciEmitor;
    }

    public void setCiEmitor(String ciEmitor) {
        this.ciEmitor = ciEmitor;
    }


    @Override
    public String toString() {
        return "Emitor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", seria='" + seria + '\'' +
                ", nr='" + nr + '\'' +
                ", ciEmitor='" + ciEmitor + '\'' +
                '}';
    }
}

