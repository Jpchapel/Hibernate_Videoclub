package pojos;
// Generated 03-feb-2021 16:42:40 by Hibernate Tools 3.6.0


import java.util.Date;

/**
 * FilmActor generated by hbm2java
 */
public class FilmActor  implements java.io.Serializable {


     private FilmActorId id;
     private Film film;
     private Actor actor;
     private Date lastUpdate;

    public FilmActor() {
    }

    public FilmActor(FilmActorId id, Film film, Actor actor, Date lastUpdate) {
       this.id = id;
       this.film = film;
       this.actor = actor;
       this.lastUpdate = lastUpdate;
    }
   
    public FilmActorId getId() {
        return this.id;
    }
    
    public void setId(FilmActorId id) {
        this.id = id;
    }
    public Film getFilm() {
        return this.film;
    }
    
    public void setFilm(Film film) {
        this.film = film;
    }
    public Actor getActor() {
        return this.actor;
    }
    
    public void setActor(Actor actor) {
        this.actor = actor;
    }
    public Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }




}


