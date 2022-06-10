package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired CallService callService;

    @Test
    void printProxy(){
        //프록시 객체 등록 확인. --> 프록시 객체 등록된 것으로 나옴
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void externalCallV2(){
        //tx active=false
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService(){
            return new CallService(internalService());
        }
        @Bean
        InternalService internalService(){
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        //외부 클래스로 분리하고 주입
        private final InternalService internalService;
//        @Transactional
        public void external(){
            log.info("call external");
            internalService.printTxInfo();
            //이 부분부터는 트랜잭션이 필요한 부분.
            internalService.internal();
        }


    }

    //별도의 클래스로 분리
    static class InternalService {
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
