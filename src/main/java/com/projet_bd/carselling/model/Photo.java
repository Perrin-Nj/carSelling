package com.projet_bd.carselling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(unique = true)
    private String name;

    private String type;

   @JsonIgnore
   @Lob
   private byte[] data;

    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL)
    private Car car;

    public Photo(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
