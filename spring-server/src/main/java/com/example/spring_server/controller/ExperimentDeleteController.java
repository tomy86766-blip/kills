package com.example.spring_server.controller;

import com.example.spring_server.entity.ExperimentLog;
import com.example.spring_server.service.ExperimentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ExperimentDeleteController {

    private final ExperimentService service;

    public ExperimentDeleteController(ExperimentService service) {
        this.service = service;
    }

    // 목록 페이지 (삭제 버튼 포함) — 추후 윤서(Read) 컨트롤러로 이관 예정
    @GetMapping("/experiments")
    public String list(Model model) {
        List<ExperimentLog> experiments = service.findAllNotDeleted();
        model.addAttribute("experiments", experiments);
        return "experimentList";
    }

    // POST /experiments/{id}/delete
    // HTML form은 GET/POST만 지원하므로 POST로 받음
    @PostMapping("/experiments/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.softDelete(id);
        return "redirect:/experiments";
    }
}
