package com.poly.entities;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Giamgia")
public class GiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idgiam;
    String magiam;
    int soluong;
    int tiengiam;
    int loai;
    long giamin;
    long giamax;

}
