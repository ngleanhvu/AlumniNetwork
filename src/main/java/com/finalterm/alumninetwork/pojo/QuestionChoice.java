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
@Table(name = "Question_Choice")
@NamedQueries({
        @NamedQuery(name = "QuestionChoice.findAll", query = "SELECT q FROM QuestionChoice q"),
        @NamedQuery(name = "QuestionChoice.findById", query = "SELECT q FROM QuestionChoice q WHERE q.id = :id")})
public class QuestionChoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "choice_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Choice choice;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Question question;

    public QuestionChoice() {
    }

    public QuestionChoice(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
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
        if (!(object instanceof QuestionChoice)) {
            return false;
        }
        QuestionChoice other = (QuestionChoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finalterm.alumninetwork.pojo.QuestionChoice[ id=" + id + " ]";
    }

}
