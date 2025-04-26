/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Lecturer_Info")
@NamedQueries({
    @NamedQuery(name = "LecturerInfo.findAll", query = "SELECT l FROM LecturerInfo l"),
    @NamedQuery(name = "LecturerInfo.findById", query = "SELECT l FROM LecturerInfo l WHERE l.id = :id"),
    @NamedQuery(name = "LecturerInfo.findByExpiredResetPasswordTime", query = "SELECT l FROM LecturerInfo l WHERE l.expiredResetPasswordTime = :expiredResetPasswordTime"),
    @NamedQuery(name = "LecturerInfo.findByChangedPassword", query = "SELECT l FROM LecturerInfo l WHERE l.changedPassword = :changedPassword")})
public class LecturerInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "expired_reset_password_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredResetPasswordTime;
    @Column(name = "changed_password")
    private Boolean changedPassword;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    public LecturerInfo() {
    }

    public LecturerInfo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getExpiredResetPasswordTime() {
        return expiredResetPasswordTime;
    }

    public void setExpiredResetPasswordTime(Date expiredResetPasswordTime) {
        this.expiredResetPasswordTime = expiredResetPasswordTime;
    }

    public Boolean getChangedPassword() {
        return changedPassword;
    }

    public void setChangedPassword(Boolean changedPassword) {
        this.changedPassword = changedPassword;
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
        if (!(object instanceof LecturerInfo)) {
            return false;
        }
        LecturerInfo other = (LecturerInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.LecturerInfo[ id=" + id + " ]";
    }
    
}
