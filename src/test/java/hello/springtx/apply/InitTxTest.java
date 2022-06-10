package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void go(){
        //초기화 코드는 스프링이 초기화 되는 시점에 호출한다.
        //hello.initV1() 직접 호출하면 당연히 트랜잭션은 적용 된다.
    }
    @TestConfiguration
    static class InitTxTestConfig{
        @Bean
        Hello hello(){
            return new Hello();
        }
    }
    @Slf4j
    static class Hello {
        @PostConstruct
        @Transactional
        public void initV1(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }

        //스프링 컨테이너가 모두 올라왔을 때 호출되는 부분
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @EventListener tx active={}", isActive);
        }
    }
}
