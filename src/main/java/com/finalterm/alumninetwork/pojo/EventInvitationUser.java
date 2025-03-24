/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Event_Invitation_User")
@NamedQueries({
        @NamedQuery(name = "EventInvitationUser.findAll", query = "SELECT e FROM EventInvitationUser e"),
        @NamedQuery(name = "EventInvitationUser.findById", query = "SELECT e FROM EventInvitationUser e WHERE e.id = :id")})
public class EventInvitationUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "event_invitation_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EventInvitation eventInvitation;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    public EventInvitationUser() {
    }

    public EventInvitationUser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EventInvitation getEventInvitation() {
        return eventInvitation;
    }

    public void setEventInvitation(EventInvitation eventInvitation) {
        this.eventInvitation = eventInvitation;
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
        if (!(object instanceof EventInvitationUser)) {
            return false;
        }
        EventInvitationUser other = (EventInvitationUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.EventInvitationUser[ id=" + id + " ]";
    }

}
