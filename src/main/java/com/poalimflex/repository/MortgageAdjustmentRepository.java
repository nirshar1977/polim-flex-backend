package com.poalimflex.repository;

import com.poalimflex.entity.MortgageAdjustment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MortgageAdjustmentRepository extends MongoRepository<MortgageAdjustment, String> {
    /**
     * Find adjustments for a specific mortgage
     */
    List<MortgageAdjustment> findByMortgageId(String mortgageId);

    /**
     * Count adjustments in the last 12 months
     */
    @Query("{ 'mortgageId': ?0, 'adjustmentDate': { $gte: ?1 } }")
    long countAdjustmentsInLastYear(String mortgageId, LocalDateTime oneYearAgo);

    /**
     * Find recent adjustments with specific status
     */
    List<MortgageAdjustment> findByMortgageIdAndStatusOrderByAdjustmentDateDesc(
            String mortgageId,
            MortgageAdjustment.AdjustmentStatus status
    );
}