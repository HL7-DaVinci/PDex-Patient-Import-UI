package org.hl7.davinci.refimpl.patientui.controllers;

import org.hl7.davinci.refimpl.patientui.importer.SampleDataImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class ImportDataController {

  public static final String PAYER_ATTR = "payer";
  private String payerAAuthUrl;
  private String payerAClientId;
  private String redirectUri;
  private String aud;

  public ImportDataController(@Value("${payer-a.auth-uri}") String payerAAuthUrl,
      @Value("${payer-a.client-id}") String payerAClientId, @Value("${payer-a.redirect-uri}") String redirectUri,
      @Value("${payer-a.fhir-server-uri}") String aud) {
    this.payerAAuthUrl = payerAAuthUrl;
    this.payerAClientId = payerAClientId;
    this.redirectUri = redirectUri;
    this.aud = aud;
  }

  @Autowired
  public SampleDataImporter dataImporter;

  @GetMapping("/importhistory")
  public RedirectView importHistory(@RequestParam("code") String code, @RequestParam("state") String state,
      HttpSession session) throws SampleDataImporter.SampleDataLoadException {
    //We do not retrieve a real data from a Payer system after authentication, but we have an Auth token, so can do
    // this without any issues when needed.
    dataImporter.importData((String) session.getAttribute(PAYER_ATTR));
    return new RedirectView("/");
  }

  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/gotopayer")
  public RedirectView goToPayer(@RequestParam String payer, HttpSession session)
      throws SampleDataImporter.SampleDataLoadException {
    session.setAttribute(PAYER_ATTR, payer);
    //Redirect URL should depend on Payer. Currently we use a single authorization server for all Payers as an
    // example of Authentication/Authorization flow.
    return new RedirectView(
        payerAAuthUrl + "?response_type=code&client_id=" + payerAClientId + "&redirect_uri=" + redirectUri + "&state="
            + UUID.randomUUID()
            .toString() + "&aud=" + aud);
  }

}
