package com.keduit.shop.entity;

import com.keduit.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private  Member member;

    private LocalDateTime orderDate;  //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;   // 주문상태

//  order와 orderItem은 1대다의 연관 관계를 가진다.
//  외래키가 orderItem에 있으므로 연관 관계의 주인은 orderItem가 된다.
//  -> order는 주인이 아니므로 mappedBy="order"을 추가
//  여기에서 order는 orderItem에서 관리하는 FK이름
//    order와 orderItem은 양방향 매핑이 됨.
    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL,
                orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

//    orderItem에는 주문 상품 정보를 추가함. orderItem 객체를 order객체의 orderItems에 추가함
//    Order엔티티와 OrderItem엔티티가 양방향 참조관계이므로, orderItem객체에도 Order객체를 넣어줌.
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    
    public void cancelOrder(){
        
//        주문 상태를 cancel로 변경
        this.orderStatus = OrderStatus.CANCEL;
//        주문 상품의 주문수량을 재고에 증가시킴
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

}
