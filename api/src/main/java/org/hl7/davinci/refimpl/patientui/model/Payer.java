package org.hl7.davinci.refimpl.patientui.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity that represents payer.
 *
 * @author Kseniia Lutsko
 */
@Entity
@Getter
@Setter
public class Payer extends BaseEntity {

  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String fhirServerUri;
  @Column(nullable = false)
  private String clientId;
  @Column(nullable = false)
  private String scope;
  @Column
  private String authorizeUri;
  @Column
  private String tokenUri;
  @Column
  private String sourcePatientId;
  @Column(columnDefinition = "timestamp with time zone")
  private OffsetDateTime lastImported;
  @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ImportInfo> importInfo = new HashSet<>();

  public void addImportInfo(List<ImportInfo> importInfo) {
    this.importInfo.addAll(importInfo);
    importInfo.parallelStream()
        .forEach(i -> i.setPayer(this));
  }
}
