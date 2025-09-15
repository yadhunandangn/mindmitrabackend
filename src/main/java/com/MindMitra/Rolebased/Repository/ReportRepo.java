package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepo extends JpaRepository<Report, Long> {

}
