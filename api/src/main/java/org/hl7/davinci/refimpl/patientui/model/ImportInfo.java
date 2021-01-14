package org.hl7.davinci.refimpl.patientui.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

/**
 * Represents the import details entity.
 *
 * @author Taras Vuyiv
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImportInfo extends BaseEntity {

  @Column(nullable = false)
  private String resourceType;
  @Column(nullable = false)
  private Long createdCount;
  @Column(nullable = false)
  private Long updatedCount;
  @Column(nullable = false)
  private OffsetDateTime importDate;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payer_id", nullable = false)
  private Payer payer;
}
