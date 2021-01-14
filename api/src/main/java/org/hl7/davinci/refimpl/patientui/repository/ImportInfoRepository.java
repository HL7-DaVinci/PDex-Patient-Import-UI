package org.hl7.davinci.refimpl.patientui.repository;

import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.model.ImportInfo;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The {@link ImportInfo} JPA repository.
 *
 * @author Taras Vuyiv
 */
@Repository
public interface ImportInfoRepository extends JpaRepository<ImportInfo, Long> {

  @Query(value = "select distinct resourceType from ImportInfo where payer = ?1")
  Set<String> findDistinctResourcesByPayer(Payer payer);

  @Query("select new org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto(i.resourceType, sum(i.createdCount)) from "
      + "ImportInfo i where payer = ?1 group by i.resourceType")
  List<ResourceInfoDto> findAllByPayerGroupByResourceType(Payer payer);

  @Query("select new org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto(i.resourceType, sum(i.createdCount)) from "
      + "ImportInfo i group by i.resourceType")
  List<ResourceInfoDto> findAllGroupByResourceType();

  List<ImportInfo> findAllByPayerAndImportDate(Payer payer, OffsetDateTime importedDate);

  Optional<ImportInfo> findFirstByResourceTypeOrderByImportDateDesc(String resourceType);

  void deleteAllByPayer(Payer payer);
}
