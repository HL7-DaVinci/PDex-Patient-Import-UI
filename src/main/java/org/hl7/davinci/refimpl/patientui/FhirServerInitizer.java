package org.hl7.davinci.refimpl.patientui;

import org.hl7.davinci.refimpl.patientui.importer.SampleDataImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class FhirServerInitizer implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(FhirServerInitizer.class);

  @Autowired
  public SampleDataImporter dataImporter;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Initializing Patient data...");
    try {
      dataImporter.importData("Go Health");
    } catch (SampleDataImporter.SampleDataLoadException e) {
      LOG.warn("Could not load all or some files. Not all data might be imported.", e);
    }

    LOG.info("Patient data import complete.");
  }
}