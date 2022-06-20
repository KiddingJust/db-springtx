package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {
    private final EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage){
        log.info("log 저장");
        em.persist(logMessage);
        
        //예외 상황 가정. 메세지에 '로그예외'가 들어가면 RuntimeException
        //RuntimeException이므로 롤백될 것
        if(logMessage.getMessage().contains("로그예외")){
            log.info("log 저장 시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }

    public Optional<Log> find(String message){
        return em.createQuery(
                        "select l from Log l where l.message = :message", Log.class)
                .setParameter("message", message)
                .getResultList().stream().findAny();    //findAny면 값이 2개일 때도 1개만 조회됨.
    }
}
