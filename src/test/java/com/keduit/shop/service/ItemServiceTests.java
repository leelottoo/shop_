package com.keduit.shop.service;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.dto.ItemFormDTO;
import com.keduit.shop.entity.Item;
import com.keduit.shop.entity.ItemImg;
import com.keduit.shop.repository.ItemImgRepository;
import com.keduit.shop.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemServiceTests {
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles(){

        List<MultipartFile> multipartFiles = new ArrayList<>();
        for(int i=0; i<5; i++){
            String path = "F:/shop/item";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
            multipartFiles.add(multipartFile);
        }
        return multipartFiles;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void saveItem() throws  Exception {
        ItemFormDTO itemFormDTO = new ItemFormDTO();
        itemFormDTO.setItemNm("테스트 상품");
        itemFormDTO.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDTO.setItemDetail("테스트 상세 설명");
        itemFormDTO.setPrice(10000);
        itemFormDTO.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();

        Long itemId = itemService.saveItem(itemFormDTO, multipartFileList);

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        System.out.println("itemImgList ---------->");
        System.out.println(itemImgList);

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        System.out.println("item ---------------");
        System.out.println(item);

        assertEquals(itemFormDTO.getItemNm(), item.getItemNm());
        assertEquals(itemFormDTO.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDTO.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDTO.getPrice(), item.getPrice());
        assertEquals(itemFormDTO.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(),itemImgList.get(0).getOriImgName());

    }
}










