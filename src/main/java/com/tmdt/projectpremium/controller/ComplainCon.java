package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.request.CheckComplainIdReq;
import com.tmdt.projectpremium.dto.request.ComplainReq;
import com.tmdt.projectpremium.dto.request.RejectedComplainReq;
import com.tmdt.projectpremium.dto.request.SendMailForUserReq;
import com.tmdt.projectpremium.dto.response.ShowComplainRes;
import com.tmdt.projectpremium.service.ComplainSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complain")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ComplainCon {
    private final ComplainSer ser;

    @PostMapping("/sendMailUser")
    public void sendMailForUser(@RequestBody SendMailForUserReq req) {
        ser.sendMailForUser(req);
    }

    @GetMapping("/admin")
    public List<ShowComplainRes> showComplainAdmin() {
        return ser.showComplainAdmin();
    }

    @PostMapping("/id")
    public String getStatus(@RequestBody long complainId) {
        return ser.getStatusComplain(complainId);
    }

    @PutMapping("/change")
    public void changeStatus(@RequestBody RejectedComplainReq req) {
        ser.changeStatusComplain(req);
    }

    @PostMapping("/show")
    public List<ShowComplainRes> showComplain(@RequestBody CheckComplainIdReq req) {
        return ser.showComplain(req);
    }

    @PostMapping("/send")
    public void sendMailComplain(@RequestBody ComplainReq req) {
        ser.sendMailComplain(req);
    }
}