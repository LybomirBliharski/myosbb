package com.softserve.osbb.repository;

import com.softserve.osbb.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Roman on 06.07.2016.
 */

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

     List<Vote> findByOrderByStartTimeDesc();

     @Query("SELECT v FROM  Vote v WHERE v.available = TRUE")
     List<Vote> findAllAvailable();
}
