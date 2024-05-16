package com.keduit.shop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
//    @OneToOne: 회원테이블과 1:1 매핑
//    @JoinColumn: 외래키 지정, name = "member_id": 외래키의 이름
//    즉시 로딩 전략 : cart를 읽을 때 member를 바로 읽어 옴. 
//    @OneToOne과 @ManyToOne는 EAGER 전략이 디폴트임
//    @OneToMany, @ManyToMany는 LAZY젼략(지연로딩)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}
