package com.finalterm.alumninetwork.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Post")
@NamedQueries({
        @NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p"),
        @NamedQuery(name = "Post.findById", query = "SELECT p FROM Post p WHERE p.id = :id"),
        @NamedQuery(name = "Post.findByBlockedComment", query = "SELECT p FROM Post p WHERE p.blockedComment = :blockedComment"),
        @NamedQuery(name = "Post.findByActive", query = "SELECT p FROM Post p WHERE p.active = :active"),
        @NamedQuery(name = "Post.findByCreatedAt", query = "SELECT p FROM Post p WHERE p.createdAt = :createdAt"),
        @NamedQuery(name = "Post.findAllByUserId", query = "SELECT p from Post p WHERE p.user = :userId")})

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "content")
    private String content;
    @Column(name = "blocked_comment")
    private Boolean blockedComment;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date createdAt;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,  fetch = FetchType.LAZY) //References -> when create post, image will save in cloudinary
    @Column(nullable = true)
    @JsonIgnore
    List<PostImage> images;

    public Post() {

    }

    public Post(Integer id) {
        this.id = id;
    }

    public Post(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getBlockedComment() {
        return blockedComment;
    }

    public void setBlockedComment(Boolean blockedComment) {
        this.blockedComment = blockedComment;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public List<PostImage> getImages() {
        return images;
    }

    public void setImages(List<PostImage> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Post)) {
            return false;
        }
        Post other = (Post) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.Post[ id=" + id + " ]";
    }

}
