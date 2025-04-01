/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


import java.io.Serializable;

/**
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Event_Invitation")
@NamedQueries({
        @NamedQuery(name = "EventInvitation.findAll", query = "SELECT e FROM EventInvitation e"),
        @NamedQuery(name = "EventInvitation.findById", query = "SELECT e FROM EventInvitation e WHERE e.id = :id"),
        @NamedQuery(name = "EventInvitation.findByStatus", query = "SELECT e FROM EventInvitation e WHERE e.status = :status")})
public class EventInvitation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 8)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Event event;

    public EventInvitation() {
    }

    public EventInvitation(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Event getEventId() {
        return event;
    }

    public void setEventId(Event event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventInvitation)) {
            return false;
        }
        EventInvitation other = (EventInvitation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.EventInvitation[ id=" + id + " ]";
    }

}
