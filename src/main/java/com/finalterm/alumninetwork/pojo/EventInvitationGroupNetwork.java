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
@Table(name = "Event_Invitation_Group_Network")
@NamedQueries({
        @NamedQuery(name = "EventInvitationGroupNetwork.findAll", query = "SELECT e FROM EventInvitationGroupNetwork e"),
        @NamedQuery(name = "EventInvitationGroupNetwork.findById", query = "SELECT e FROM EventInvitationGroupNetwork e WHERE e.id = :id")})
public class EventInvitationGroupNetwork implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "event_invitation_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EventInvitation eventInvitation;
    @JoinColumn(name = "group_network_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private GroupNetwork groupNetwork;

    public EventInvitationGroupNetwork() {
    }

    public EventInvitationGroupNetwork(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EventInvitation getEventInvitation() {
        return eventInvitation;
    }

    public void setEventInvitation(EventInvitation eventInvitation) {
        this.eventInvitation = eventInvitation;
    }

    public GroupNetwork getGroupNetwork() {
        return groupNetwork;
    }

    public void setGroupNetwork(GroupNetwork groupNetwork) {
        this.groupNetwork = groupNetwork;
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
        if (!(object instanceof EventInvitationGroupNetwork)) {
            return false;
        }
        EventInvitationGroupNetwork other = (EventInvitationGroupNetwork) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.EventInvitationGroupNetwork[ id=" + id + " ]";
    }

}
