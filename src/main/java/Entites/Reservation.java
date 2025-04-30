package Entites;


import java.sql.Timestamp;

//table name: reservation
//field names: id:int, date_debut:datetime, date_fin:datetime, machine_id:int, client_id:int, status:varchar(ignore this i dont need it)
public class Reservation {
    private int id;
    private Timestamp dateDebut;
    private Timestamp dateFin;
    private Machine machine;
    private User client;

    public Reservation(int id, Timestamp dateDebut, Timestamp dateFin, Machine machine, User client) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.machine = machine;
        this.client = client;
    }

    public Reservation(Timestamp dateDebut, Timestamp dateFin, Machine machine, User client) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.machine = machine;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Timestamp dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Timestamp getDateFin() {
        return dateFin;
    }

    public void setDateFin(Timestamp dateFin) {
        this.dateFin = dateFin;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }
}