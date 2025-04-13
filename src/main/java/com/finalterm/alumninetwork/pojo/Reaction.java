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
@Table(name = "Reaction")
@NamedQueries({
        @NamedQuery(name = "Reaction.findAll", query = "SELECT r FROM Reaction r"),
        @NamedQuery(name = "Reaction.findById", query = "SELECT r FROM Reaction r WHERE r.id = :id"),
        @NamedQuery(name = "Reaction.findByType", query = "SELECT r FROM Reaction r WHERE r.type = :type"),
        @NamedQuery(name = "Reaction.countByPostIdAndType", query = "SELECT COUNT(r) FROM Reaction r WHERE r.post.id = :postId AND r.type = :type"),
        @NamedQuery(name = "Reaction.countTotalByPostId", query = "SELECT COUNT(r) FROM Reaction r WHERE r.post.id = :postId"),
})
public class Reaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EnumReaction type;

    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Post post;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    public Reaction() {
    }

    public Reaction(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EnumReaction getType() {
        return type;
    }

    public void setType(EnumReaction type) {
        this.type = type;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
        if (!(object instanceof Reaction)) {
            return false;
        }
        Reaction other = (Reaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.Reaction[ id=" + id + " ]";
    }

}
