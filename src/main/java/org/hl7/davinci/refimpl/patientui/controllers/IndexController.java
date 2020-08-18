package org.hl7.davinci.refimpl.patientui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping("/index.html")
  public String main(Model model) {
    return "index";
  }

}
