package com.blogcode.example3.batch;

import com.blogcode.example3.domain.History;
import com.blogcode.example3.domain.PurchaseOrder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

/**
 * Created by jojoldu@gmail.com on 2017. 4. 12.
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

@Configuration
@ConditionalOnProperty(name="job.name", havingValue = EntityContextConfiguration.JOB_NAME)
public class EntityContextConfiguration {
    public static final String JOB_NAME = "entityContextJob";
    private static final String STEP_NAME = "entityContextStep";

    private EntityManagerFactory entityManagerFactory;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public EntityContextConfiguration(EntityManagerFactory entityManagerFactory, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get(STEP_NAME)
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get(STEP_NAME)
                .<PurchaseOrder, History>chunk(100)
//                .reader(reader())
                .reader(fixReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(name = JOB_NAME +"_reader")
    @StepScope
    public JpaPagingItemReader<PurchaseOrder> reader() {
        return new JpaPagingItemReaderBuilder<PurchaseOrder>()
                .name(JOB_NAME +"_reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select o from PurchaseOrder o")
                .pageSize(10)
                .build();
    }

    @Bean(name = JOB_NAME +"_fixReader")
    @StepScope
    public JpaPagingItemReader<PurchaseOrder> fixReader() {
        return new JpaPagingItemReaderBuilder<PurchaseOrder>()
                .name(JOB_NAME +"_fixReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select o from PurchaseOrder o")
                .pageSize(100)
                .build();
    }

    private ItemProcessor<PurchaseOrder, History> processor() {
        return item -> History.builder()
                .purchaseOrderId(item.getId())
                .productIdList(item.getProductList())
                .build();
    }

    private JpaItemWriter<History> writer() {
        JpaItemWriter<History> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
