/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Group_Network")
@NamedQueries({
    @NamedQuery(name = "GroupNetwork.findAll", query = "SELECT g FROM GroupNetwork g"),
    @NamedQuery(name = "GroupNetwork.findById", query = "SELECT g FROM GroupNetwork g WHERE g.id = :id"),
    @NamedQuery(name = "GroupNetwork.findByName", query = "SELECT g FROM GroupNetwork g WHERE g.name = :name")})
public class GroupNetwork implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "groupNetwork", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GroupNetworkUser> groupNetworkUsers = new HashSet<>();

    public Set<GroupNetworkUser> getGroupNetworkUsers() {
        return groupNetworkUsers;
    }

    public void setGroupNetworkUsers(Set<GroupNetworkUser> groupNetworkUsers) {
        this.groupNetworkUsers = groupNetworkUsers;
    }

    public GroupNetwork() {
    }

    public GroupNetwork(Integer id) {
        this.id = id;
    }

    public GroupNetwork(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (!(object instanceof GroupNetwork)) {
            return false;
        }
        GroupNetwork other = (GroupNetwork) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.GroupNetwork[ id=" + id + " ]";
    }
    
}
