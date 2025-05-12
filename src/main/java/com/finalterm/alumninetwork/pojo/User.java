
package com.finalterm.alumninetwork.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nguoideptrangian
 */
@Entity
@Table(name = "User")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByFullName", query = "SELECT u FROM User u WHERE u.fullName = :fullName"),
    @NamedQuery(name = "User.findByPhone", query = "SELECT u FROM User u WHERE u.phone = :phone"),
    @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role = :role"),
    @NamedQuery(name = "User.findByAvatar", query = "SELECT u FROM User u WHERE u.avatar = :avatar"),
    @NamedQuery(name = "User.findByCoverAvatar", query = "SELECT u FROM User u WHERE u.coverAvatar = :coverAvatar"),
    @NamedQuery(name = "User.findByCreatedAt", query = "SELECT u FROM User u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "User.findByUpdatedAt", query = "SELECT u FROM User u WHERE u.updatedAt = :updatedAt"),
    @NamedQuery(name = "User.findByActive", query = "SELECT u FROM User u WHERE u.active = :active"),
    @NamedQuery(name = "User.count", query = "SELECT COUNT(u) FROM User u")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Size(min = 1, max = 50)
    @Column(name = "full_name")
    private String fullName;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "phone")
    private String phone;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Basic(optional = false)
    @Column(name = "avatar")
    private String avatar;
    @Size(max = 255)
    @Column(name = "cover_avatar")
    private String coverAvatar;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "active")
    private Boolean active;
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<GroupNetworkUser> groupNetworkUsers = new HashSet<>();
    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private LecturerInfo lecturerInfo;

    public LecturerInfo getLecturerInfo() {
        return lecturerInfo;
    }

    public void setLecturerInfo(LecturerInfo lecturerInfo) {
        this.lecturerInfo = lecturerInfo;
    }


    public Set<GroupNetworkUser> getGroupNetworkUsers() {
        return groupNetworkUsers;
    }

    public void setGroupNetworkUsers(Set<GroupNetworkUser> groupNetworkUsers) {
        this.groupNetworkUsers = groupNetworkUsers;
    }

    @Transient
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String username, String password, String email, String fullName, String phone, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.avatar = avatar;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverAvatar() {
        return coverAvatar;
    }

    public void setCoverAvatar(String coverAvatar) {
        this.coverAvatar = coverAvatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "User: " + username;
    }

}
