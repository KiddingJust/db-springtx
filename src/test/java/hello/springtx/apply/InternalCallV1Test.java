package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Call;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    void printProxy(){
        //프록시 객체 등록 확인. --> 프록시 객체 등록된 것으로 나옴
        log.info("callService class={}", callService.getClass());
    }
    @Test
    void internalCall(){
        //tx active=true
        callService.internal();
    }
    @Test
    void externalCall(){
        //tx active=false
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService(){
            return new CallService();
        }
    }
    @Slf4j
    static class CallService {
//        @Transactional
        public void external(){
            log.info("call external");
            printTxInfo();
            //이 부분부터는 트랜잭션이 필요한 부분.
            internal();
        }
        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }
        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }
}
