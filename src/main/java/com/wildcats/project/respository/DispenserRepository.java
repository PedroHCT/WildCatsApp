package com.wildcats.project.respository;

import com.wildcats.project.entity.Dispenser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispenserRepository extends JpaRepository<Dispenser, Integer> {

    Dispenser findByMacAddress(String macAddress);

    @Query(
            value = "select * from DISPENSER disp " +
                    "where LOWER(disp.local) like %:loc%",
            nativeQuery = true
    )
    List<Dispenser> findLikeByLocal(@Param("loc") String loc);
}
