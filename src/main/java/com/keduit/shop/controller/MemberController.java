package com.keduit.shop.controller;

import com.keduit.shop.dto.MemberFormDTO;
import com.keduit.shop.entity.Member;
import com.keduit.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDTO", new MemberFormDTO());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String memberForm(MemberFormDTO memberFormDTO){
        Member member = Member.createMember(memberFormDTO,passwordEncoder);
        memberService.saveMember(member);
        return "redirect:/";
    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDTO memberFormDTO,
                            BindingResult bindingResult, Model model){
//        memberFormDTO의 유효성 체크 결과를 확인-> 에러이면 다시 입력 폼을 리턴
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
//      회원가입 시 메일이 중복된 경우 에러를 처리
        try {
            Member member = Member.createMember(memberFormDTO, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }
    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 혹은 비밀번호를 확인해주세요.");
        return "/member/memberLoginForm";
    }
}
