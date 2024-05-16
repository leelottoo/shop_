package com.keduit.shop.entity;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.repository.ItemRepository;
import com.keduit.shop.repository.MemberRepository;
import com.keduit.shop.repository.OrderItemRepository;
import com.keduit.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class OrderTests {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("order 테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("order 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
//      item.setRegDate(LocalDateTime.now());
        return item;
    }

    public Order createOrder(){
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem(); // 아이템 넣어줌
            itemRepository.save(item); //제품 만들어짐

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(10000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for(int i=0; i<5; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(10000);
            orderItem.setOrder(order);
//          아직 영속성 컨텍스트에 저장되지 않은 orderItem엔티티를 order엔티티에 담아둠
            order.getOrderItems().add(orderItem);
        }

//        order 엔티티를 저장하면서 강제로 flush를 호출하여 영속성 컨텍스트에 있는 객체들을 DB에 반영.
        orderRepository.saveAndFlush(order);
//        영속성 컨텍스트의 상태를 초기화
        em.clear();
        
        Order saveOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(5, saveOrder.getOrderItems().size());
        
    }

    @Test
    @DisplayName("고아 객체 제거 테스트")
    public void orphanRemovalTests(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("order class : " + orderItem.getOrder().getClass());
        System.out.println(".....................................");
        orderItem.getOrder().getOrderDate();  // 메서드를 호출 할 때 join된다.
        System.out.println(".....................................");
//        지연로딩-> 자원을 아낄 수 있다.
    }

}
