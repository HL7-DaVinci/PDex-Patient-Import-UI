package org.hl7.davinci.refimpl.patientui.controllers;

import org.hl7.davinci.refimpl.patientui.config.properties.Payers;
import org.hl7.davinci.refimpl.patientui.services.ImportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
public class ImportDataController {

  public static final String PAYER_ATTR = "payer";

  @Autowired
  Payers payers;
  @Autowired
  ImportDataService importDataService;

  @GetMapping("/auth")
  public RedirectView auth(@RequestParam("code") String code, HttpSession session) {
    //We do not retrieve a real data from a Payer system after authentication, but we have an Auth token, so can do
    // this without any issues when needed.
    Payers.Payer payerConfig = (Payers.Payer) session.getAttribute(PAYER_ATTR);
    if (payerConfig == null) {
      throw new IllegalStateException("No payer found in current session.");
    }
    importDataService.importRecords(payerConfig, code);
    return new RedirectView("/");
  }

  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/gotopayer")
  public RedirectView goToPayer(@RequestParam String payer, HttpSession session,
      @Value("${app.redirect-uri}") String redirectUri) throws UnsupportedEncodingException {
    Payers.Payer payerConfig = payers.getList()
        .stream()
        .filter(p -> p.getName()
            .equals(payer))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No configuration available for payer " + payer));
    session.setAttribute(PAYER_ATTR, payerConfig);
    return new RedirectView(
        payerConfig.getAuthUri() + "?response_type=code&client_id=" + payerConfig.getClientId() + "&redirect_uri="
            + redirectUri + "&state=" + UUID.randomUUID()
            .toString() + "&aud=" + payerConfig.getFhirServerUri() + "&scope=" + URLEncoder.encode(
            payerConfig.getScope(), StandardCharsets.UTF_8.name()));
  }
}
