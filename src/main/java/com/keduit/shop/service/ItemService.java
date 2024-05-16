package com.keduit.shop.service;

import com.keduit.shop.dto.ItemFormDTO;
import com.keduit.shop.dto.ItemImgDTO;
import com.keduit.shop.dto.ItemSearchDTO;
import com.keduit.shop.dto.MainItemDTO;
import com.keduit.shop.entity.Item;
import com.keduit.shop.entity.ItemImg;
import com.keduit.shop.repository.ItemImgRepository;
import com.keduit.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDTO itemFormDTO, List<MultipartFile> itemImgFileList) throws Exception{
//        상품 등록
        Item item = itemFormDTO.createItem();
        itemRepository.save(item);

//        이미지 등록
        for(int i=0; i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0) {
                itemImg.setRepimgYn("Y");
            } else {
                itemImg.setRepimgYn("N");
            }
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    //상품 데이터를 읽기만 하므로 JPA가 변경감지를 하지 않아 성능상 이점이 있음.
    @Transactional(readOnly = true) //감시하지마!!=>불필요한 작업을 하지 않아 성능이 빨라진다.
    public ItemFormDTO getItemDtl(Long itemId){

        List<ItemImg> itemImgList=itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        //image entity가 저장된 이미지니까 이 저장된 이미지를 화면에 뿌려주기 위해 타입 변환 =>그러니까 이미지 리스트를 화면에 뿌려줘야 한다.

        List<ItemImgDTO>itemImgDTOList=new ArrayList<>();


        for(ItemImg itemImg:itemImgList){
            ItemImgDTO itemImgDTO= ItemImgDTO.of(itemImg);
            itemImgDTOList.add(itemImgDTO);
        }
        //아이템에 위의 아이템이미지를 추가한다.
        Item item=itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDTO itemFormDTO=ItemFormDTO.of(item);
        itemFormDTO.setItemImgDTOList(itemImgDTOList);
        return itemFormDTO;
    }

    public Long updateItem(ItemFormDTO itemFormDTO,
                           List<MultipartFile> itemImgFileList) throws Exception{
        // 상품수정
        Item item = itemRepository.findById(itemFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDTO);

        List<Long> itemImgIds = itemFormDTO.getItemImgIds();


        // 이미지 등록
        for(int i=0; i<itemImgFileList.size();i++){
//      상품이미지를 업데이트 하기 위해
//      updateItemImg()메소드에 상품이미지 아이디와 상품이미지 파일 정보를 파라미터로 전달 함.
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDTO,pageable);
    }
    
    @Transactional(readOnly = true) //더티체킹 하지 않음
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
        return  itemRepository.getMainItemPage(itemSearchDTO,pageable);
    }
}
