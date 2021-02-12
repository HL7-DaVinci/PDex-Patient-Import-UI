package org.hl7.davinci.refimpl.patientui.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration.
 *
 * @author Kseniia Lutsko
 */
@Configuration
@EnableJpaRepositories(basePackages = {"org.hl7.davinci.refimpl.patientui.repository"})
@EntityScan(basePackages = {"org.hl7.davinci.refimpl.patientui.model"})
public class JpaConfig {}
