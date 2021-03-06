package com.sequenceiq.datalake.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sequenceiq.common.api.type.CertExpirationState;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.projection.SdxClusterIdView;

@Repository
@Transactional(TxType.REQUIRED)
public interface SdxClusterRepository extends CrudRepository<SdxCluster, Long> {

    @Override
    List<SdxCluster> findAll();

    @Query("SELECT s.id as id, s.stackCrn as stackCrn " +
            "FROM SdxCluster s " +
            "WHERE s.deleted is null " +
            "AND s.stackCrn is not null")
    List<SdxClusterIdView> findAllAliveView();

    Optional<SdxCluster> findByAccountIdAndClusterNameAndDeletedIsNull(String accountId, String clusterName);

    Optional<SdxCluster> findByAccountIdAndCrnAndDeletedIsNull(String accountId, String crn);

    List<SdxCluster> findByAccountIdAndDeletedIsNull(String accountId);

    List<SdxCluster> findByAccountIdAndEnvCrnAndDeletedIsNull(String accountId, String envCrn);

    List<SdxCluster> findByAccountIdAndEnvNameAndDeletedIsNull(String accountId, String envName);

    List<SdxCluster> findByIdIn(Set<Long> resourceIds);

    @Query("SELECT s.crn FROM SdxCluster s WHERE s.accountId = :accountId")
    List<String> findAllCrnInAccount(@Param("accountId") String accountId);

    @Query("SELECT s.stackCrn FROM SdxCluster s WHERE s.crn = :crn")
    Optional<String> findStackCrnByClusterCrn(@Param("crn") String crn);

    @Modifying
    @Query("UPDATE SdxCluster s SET s.certExpirationState = :state WHERE s.id = :id")
    void updateCertExpirationState(@Param("id") Long id, @Param("state") CertExpirationState state);
}
