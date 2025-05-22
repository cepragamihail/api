package ro.kyosai.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.kyosai.api.entity.FactoryReport;

public interface FactoryReportRepository extends JpaRepository<FactoryReport, String> {


    List<FactoryReport> findByDatetimeBetween(LocalDateTime start, LocalDateTime end);

    Double findAverageWeightByDatetimeBetween(LocalDateTime start, LocalDateTime end);

}
