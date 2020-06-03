package com.devellany.sample.account.ui;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.ui.form.SignUpForm;
import com.devellany.sample.account.ui.validator.SignUpFormValidator;
import com.devellany.sample.common.application.AuthenticationHelper;
import com.devellany.sample.common.ui.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController extends BaseController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    @InitBinder("signUpForm")
    public void initSignUpFormBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/account/sign-in")
    public String signInForm() {
        return "/account/sign-in";
    }

    @GetMapping("/account/sign-up")
    public String signUpFrom(Model model) {
        model.addAttribute(new SignUpForm());

        return "/account/sign-up";
    }

    @PostMapping("/account/sign-up")
    public String signUpProcess(@Valid SignUpForm signUpForm, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "/account/sign-up";
        }

        accountService.processNewAccount(signUpForm);
        redirectAttributes.addAttribute("email", signUpForm.getEmail());
        return "redirect:/account/email/confirm";
    }

    @GetMapping("/account/sign-out")
    public String SignOut(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationHelper.signOut(request, response);

        return "redirect:/";
    }
}