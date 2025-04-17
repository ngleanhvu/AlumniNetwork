/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Notification_Group_Network")
@NamedQueries({
    @NamedQuery(name = "NotificationGroupNetwork.findAll", query = "SELECT n FROM NotificationGroupNetwork n"),
    @NamedQuery(name = "NotificationGroupNetwork.findById", query = "SELECT n FROM NotificationGroupNetwork n WHERE n.id = :id")})
public class NotificationGroupNetwork implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Event event;
    @JoinColumn(name = "group_network_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private GroupNetwork groupNetwork;

    public NotificationGroupNetwork() {
    }

    public NotificationGroupNetwork(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
        if (!(object instanceof NotificationGroupNetwork)) {
            return false;
        }
        NotificationGroupNetwork other = (NotificationGroupNetwork) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.NotificationGroupNetwork[ id=" + id + " ]";
    }
    
}
