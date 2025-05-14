/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finalterm.alumninetwork.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 *
 * @author nguoideptrangian
 */
@Entity
@Table(name = "Choice")
@NamedQueries({
    @NamedQuery(name = "Choice.findAll", query = "SELECT c FROM Choice c"),
    @NamedQuery(name = "Choice.findById", query = "SELECT c FROM Choice c WHERE c.id = :id"),
    @NamedQuery(name = "Choice.findByContent", query = "SELECT c FROM Choice c WHERE c.content = :content")})
public class Choice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "content")
    private String content;
    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;
    @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSurveyChoice>  userSurveyChoices;

    public List<UserSurveyChoice> getUserSurveyChoices() {
        return userSurveyChoices;
    }

    public void setUserSurveyChoices(List<UserSurveyChoice> userSurveyChoices) {
        this.userSurveyChoices = userSurveyChoices;
    }

    public Choice() {
    }

    public Choice(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        if (!(object instanceof Choice)) {
            return false;
        }
        Choice other = (Choice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.Choice[ id=" + id + " ]";
    }
    
}
