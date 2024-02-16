package io.chrispysz.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity(name = "qa_token")
public class Token extends PanacheEntity {

    @NotNull
    @Size(min = 3, max = 50)
    public String name;

    @NotNull
    @Size(min = 3, max = 50)
    public String value;
    @NotNull
    public LocalDateTime validUntil = LocalDateTime.now().plusDays(90);


    @Override
    public String toString() {
        return "Token{" +
                "name=" + name + '\'' +
                ", value='" + value + '\'' +
                ", validUntil='" + validUntil + '\'' +
                '}';
    }
}
