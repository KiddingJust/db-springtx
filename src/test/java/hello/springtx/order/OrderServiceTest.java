package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        //given
        Order order= new Order();
        order.setUsername("정상");
        //when
        orderService.order(order);
        //then
        // 원래는 ENUM등이 활용되는 게 맞음.
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }
    @Test
    void runtimeException() {
        //given
        Order order= new Order();
        order.setUsername("예외");
        //when
        Assertions.assertThatThrownBy(()->orderService.order(order))
                        .isInstanceOf(RuntimeException.class);
        //then
        //롤백되어야 함. 따라서 DB에도 데이터가 없어야 함.
        //롤백이 되면 사실 DB에 INSERT 쿼리 자체도 안날아감.
        //--> JPA는 트랜잭션 커밋 시점에 쿼리가 날아가므로, 롤백 시에는 DB에 쿼리를 날릴 필요가 없기 때문.
        Optional<Order> findOrder = orderRepository.findById(order.getId());
        assertThat(findOrder.isEmpty()).isTrue();
    }
    @Test
    void bizException() {
        //given
        Order order= new Order();
        order.setUsername("잔고부족");
        //when
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }
        //then
        //커밋이 되므로 주문 정보가 있어야함.
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }
}