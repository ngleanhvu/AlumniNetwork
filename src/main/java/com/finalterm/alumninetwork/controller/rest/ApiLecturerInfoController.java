package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.service.LecturerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lecturer-infos")
public class ApiLecturerInfoController {
    @Autowired
    private LecturerInfoService lecturerInfoService;

//    @GetMapping
//    @CrossOrigin
//    public ResponseEntity<List<LecturerInfo>> getLecturerInfos(@RequestParam Map<String, String> params) {
//        return ResponseEntity.ok(lecturerInfoService.getLecturerInfos(params));
//    }


    @GetMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<LecturerInfo> getLecturerInfos(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(lecturerInfoService.getLecturerInfoByUserId(id), HttpStatus.OK);
    }
}
