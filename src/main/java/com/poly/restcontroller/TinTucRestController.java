package com.poly.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.TinTucDAO;
import com.poly.entities.TinTuc;

@RestController
@CrossOrigin("*")
public class TinTucRestController {
    @Autowired
    TinTucDAO tdao;

    @GetMapping("/rest/tintuc")
    public ResponseEntity<List<TinTuc>> loadtin() {
        return ResponseEntity.ok(tdao.findAll());
    }

    @GetMapping("/rest/tintuc/{id}")
    public ResponseEntity<TinTuc> findtin(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(tdao.findById(id).get());
    }

    @GetMapping("rest/timkiemtin")
    public ResponseEntity<List<TinTuc>> searchtin(@RequestParam("noidung") String nd) {
        if (nd.isEmpty()) {
            return ResponseEntity.ok(tdao.findAll());
        } else {
            return ResponseEntity.ok(tdao.searchByProductNameOrTitle(nd));

        }
    }
    @GetMapping("rest/counttin")
    public ResponseEntity<List<Object[]>> counttin(){
        return ResponseEntity.ok(tdao.countNewsByCategory());
    } 
}
