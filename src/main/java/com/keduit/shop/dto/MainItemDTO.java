package com.keduit.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MainItemDTO {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    /* QueryProjection 사용시 생성자는 QueryDsl에 의존 */
    /* QueryDsl로 결과 조회 시 MainItemDTO객체로 바로 받아 옴. */
    @QueryProjection
    public MainItemDTO(Long id, String itemNm, String itemDetail, String imgUrl, Integer price) {

        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
