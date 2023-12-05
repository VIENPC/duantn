package com.poly.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.ThuongHieuDAO;
import com.poly.entities.ThuongHieu;

@CrossOrigin(origins = "*")
@RestController
public class ThuongHieuRestController {
     @Autowired
    ThuongHieuDAO thdao;

    @GetMapping("rest/thuonghieu")
    public ResponseEntity<List<ThuongHieu>> loadth(){

        return  ResponseEntity.ok(thdao.findAll());
    }

    
}
