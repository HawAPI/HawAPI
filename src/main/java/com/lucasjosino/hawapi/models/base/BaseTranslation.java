package com.lucasjosino.hawapi.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract public class BaseTranslation implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, insertable = false, updatable = false)
    private Integer id;

    @Column
    private String language;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "BaseTranslation{" +
                "id=" + id +
                ", language='" + language + '\'' +
                '}';
    }
}