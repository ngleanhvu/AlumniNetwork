/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 *
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Post_Image")
@NamedQueries({
    @NamedQuery(name = "PostImage.findAll", query = "SELECT p FROM PostImage p"),
    @NamedQuery(name = "PostImage.findById", query = "SELECT p FROM PostImage p WHERE p.id = :id"),
    @NamedQuery(name = "PostImage.findByUrl", query = "SELECT p FROM PostImage p WHERE p.url = :url"),
    @NamedQuery(name = "PostImage.findByPostId", query = "SELECT p FROM PostImage  p WHERE p.post.id = :postId")})

public class PostImage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;

    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public PostImage() {
    }

    public PostImage(Integer id) {
        this.id = id;
    }

    public PostImage(Integer id, String url) {
        this.id = id;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
        if (!(object instanceof PostImage)) {
            return false;
        }
        PostImage other = (PostImage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.PostImage[ id=" + id + " ]";
    }

}
