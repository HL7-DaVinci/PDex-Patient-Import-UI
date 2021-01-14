package org.hl7.davinci.refimpl.patientui.repository;

import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The {@link Payer} JPA repository.
 *
 * @author Kseniia Lutsko
 */
@Repository
public interface PayerRepository extends JpaRepository<Payer, Long> {}
