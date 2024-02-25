package io.chrispysz.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity(name = "qao_token")
public class Token extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String guid;

    @NotNull
    @Size(min = 3, max = 50)
    public String name;

    @NotNull
    @Size(min = 3, max = 50)
    public String value;
    @NotNull
    public LocalDateTime validUntil = LocalDateTime.now().plusDays(90);


    public String getGuid() {
        return guid;
    }

    @Override
    public String toString() {
        return "Token{" +
                "name=" + name + '\'' +
                ", value='" + value + '\'' +
                ", validUntil='" + validUntil + '\'' +
                '}';
    }
}
