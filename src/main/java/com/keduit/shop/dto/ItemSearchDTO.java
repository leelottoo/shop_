package com.keduit.shop.dto;

import com.keduit.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemSearchDTO {
    
    private String searchDateType;  
    // all : 전체   
    // 1d : 최근 하루 동안 등록 된 상품
    // 1w : 최근 1주  동안 등록 된 상품
    // 1m : 최근 한달 동안 등록 된 상품
    // 6m : 최근 6개월 동안 등록 된 상품

    private ItemSellStatus searchSellStatus;
    
    private String searchBy;
    // itemNm : 상품명으로 조회
    // createBy : 상품 등록자 아이디
    
    private String searchQuery = "";
    // 조회 할 검색어를 저장할 변수
}
