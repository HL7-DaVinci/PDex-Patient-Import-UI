package org.hl7.davinci.refimpl.patientui.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * Represents a single request during import/refresh of the Payer.
 *
 * @author Taras Vuyiv
 */
@Entity
@Getter
@Setter
public class ImportRequest extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String requestId;
  @Lob
  @Column
  private String responseBody;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payer_id", nullable = false)
  private Payer payer;
}
