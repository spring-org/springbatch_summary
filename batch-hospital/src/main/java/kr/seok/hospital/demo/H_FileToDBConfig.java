package kr.seok.hospital.demo;

import kr.seok.hospital.repository.HospitalJpaRepository;
import kr.seok.hospital.step.Step_H_FileToDB_tasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * seoul_hospital_position_info_utf8.csv 파일 데이터
 * MySQL: TB_HOSPITAL 테이블에 저장
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class H_FileToDBConfig {

    private final JobBuilderFactory jobBuilderFactory;
//    private final Step_H_FileToDB stepHFileToDB; // 53s526ms
    private final Step_H_FileToDB_tasklet step_h_fileToDB_tasklet; // 22s

    private final HospitalJpaRepository hospitalJpaRepository;
    private final String JOB_NAME = "H_FileToDB";

    @Bean(name = JOB_NAME + "_JOB")
    public Job hFileToDb() {
        return jobBuilderFactory.get(JOB_NAME + "_JOB")
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        hospitalJpaRepository.deleteAllInBatch();
                        log.info("JOB 실행 전 : TB_HOSPITAL({})", hospitalJpaRepository.count());
                    }
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("JOB 실행 후 : TB_HOSPITAL({})", hospitalJpaRepository.count());
                    }
                })
                .incrementer(new RunIdIncrementer())
                /* reader -> processor -> writer 프로세스 */
//                .start(stepHFileToDB.hFileToDbStep())
                /* tasklet 프로세스 */
                .start(step_h_fileToDB_tasklet.hFileToDbStep())
                .build();
    }
}
